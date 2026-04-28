from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
import csv
import json
from pathlib import Path
from typing import Any

import numpy as np
import xgboost as xgb


NUMERIC_FIELDS = [
    "user_total_bookings",
    "user_cancelled_bookings",
    "user_recent_bookings_30d",
    "loyalty_points",
    "days_until_seance",
    "booking_lead_hours",
    "seance_hour",
    "seance_weekday",
    "is_weekend",
    "prix_base",
    "seat_number",
    "is_premium_seat",
]

CATEGORICAL_FIELDS = ["loyalty_level", "langue", "film_genre"]


def _safe_float(value: Any, default: float = 0.0) -> float:
    try:
        if value is None or value == "":
            return default
        return float(value)
    except (TypeError, ValueError):
        return default


def _date_or_default(value: str | None) -> datetime | None:
    if not value:
        return None
    try:
        return datetime.fromisoformat(value)
    except ValueError:
        return None


def enrich_features(payload: dict[str, Any]) -> dict[str, Any]:
    enriched = dict(payload)
    reservation_date = _date_or_default(str(payload.get("date_reservation") or ""))
    seance_date = _date_or_default(str(payload.get("seance_date_heure") or ""))

    if reservation_date and seance_date:
        lead_hours = max(int((seance_date - reservation_date).total_seconds() // 3600), 0)
        days_until = max((seance_date.date() - reservation_date.date()).days, 0)
        enriched["booking_lead_hours"] = lead_hours
        enriched["days_until_seance"] = days_until
        enriched["seance_hour"] = seance_date.hour
        enriched["seance_weekday"] = seance_date.weekday()
        enriched["is_weekend"] = 1 if seance_date.weekday() >= 5 else 0
    else:
        enriched.setdefault("booking_lead_hours", 24)
        enriched.setdefault("days_until_seance", 1)
        enriched.setdefault("seance_hour", 20)
        enriched.setdefault("seance_weekday", 4)
        enriched.setdefault("is_weekend", 0)

    seat_number = int(_safe_float(enriched.get("seat_number"), 0))
    enriched["seat_number"] = seat_number
    enriched["is_premium_seat"] = 1 if 0 < seat_number <= 12 else int(_safe_float(enriched.get("is_premium_seat"), 0))
    return enriched


def _roc_auc_score(y_true: np.ndarray, y_prob: np.ndarray) -> float:
    positives = y_prob[y_true == 1]
    negatives = y_prob[y_true == 0]
    if positives.size == 0 or negatives.size == 0:
        return 0.5

    total_pairs = positives.size * negatives.size
    wins = 0.0
    for positive_score in positives:
        wins += float(np.sum(positive_score > negatives))
        wins += 0.5 * float(np.sum(positive_score == negatives))
    return wins / total_pairs


def _accuracy_score(y_true: np.ndarray, y_prob: np.ndarray) -> float:
    predictions = (y_prob >= 0.5).astype(np.float32)
    return float((predictions == y_true).mean())


def _build_categories(rows: list[dict[str, Any]]) -> dict[str, list[str]]:
    return {
        field: sorted({str(row.get(field) or "UNKNOWN") for row in rows})
        for field in CATEGORICAL_FIELDS
    }


def _build_feature_names(categories: dict[str, list[str]]) -> list[str]:
    feature_names = list(NUMERIC_FIELDS)
    for field in CATEGORICAL_FIELDS:
        feature_names.extend(f"{field}::{category}" for category in categories[field])
    return feature_names


def _encode_payload(payload: dict[str, Any], categories: dict[str, list[str]]) -> np.ndarray:
    enriched = enrich_features(payload)
    features: list[float] = []

    for field in NUMERIC_FIELDS:
        features.append(_safe_float(enriched.get(field), 0.0))

    for field in CATEGORICAL_FIELDS:
        raw_value = str(enriched.get(field) or "UNKNOWN")
        features.extend(1.0 if raw_value == category else 0.0 for category in categories[field])

    return np.array(features, dtype=np.float32)


def _encode_rows(rows: list[dict[str, Any]], categories: dict[str, list[str]]) -> tuple[np.ndarray, np.ndarray]:
    feature_rows = [_encode_payload(row, categories) for row in rows]
    targets = [_safe_float(row.get("cancellation_target"), 0.0) for row in rows]
    return np.vstack(feature_rows), np.array(targets, dtype=np.float32)


@dataclass
class TrainedCancellationModel:
    booster: xgb.Booster
    categories: dict[str, list[str]]
    feature_names: list[str]
    metadata: dict[str, Any]

    def predict_proba(self, payload: dict[str, Any]) -> float:
        vector = _encode_payload(payload, self.categories).reshape(1, -1)
        matrix = xgb.DMatrix(vector, feature_names=self.feature_names)
        probability = self.booster.predict(matrix)
        return float(probability[0])


def train_model_from_csv(dataset_path: Path, model_path: Path) -> TrainedCancellationModel:
    with dataset_path.open("r", encoding="utf-8") as dataset_file:
        rows = [enrich_features(row) for row in csv.DictReader(dataset_file)]

    categories = _build_categories(rows)
    feature_names = _build_feature_names(categories)
    features, targets = _encode_rows(rows, categories)

    rng = np.random.default_rng(42)
    indices = np.arange(features.shape[0])
    rng.shuffle(indices)

    split_index = max(int(len(indices) * 0.8), 1)
    train_indices = indices[:split_index]
    test_indices = indices[split_index:] if split_index < len(indices) else indices[:split_index]

    train_matrix = xgb.DMatrix(features[train_indices], label=targets[train_indices], feature_names=feature_names)
    test_matrix = xgb.DMatrix(features[test_indices], label=targets[test_indices], feature_names=feature_names)

    booster = xgb.train(
        params={
            "objective": "binary:logistic",
            "eval_metric": ["logloss", "auc"],
            "max_depth": 4,
            "eta": 0.08,
            "subsample": 0.9,
            "colsample_bytree": 0.85,
            "min_child_weight": 2,
            "seed": 42,
        },
        dtrain=train_matrix,
        num_boost_round=140,
        evals=[(train_matrix, "train"), (test_matrix, "test")],
        verbose_eval=False,
    )

    test_probabilities = booster.predict(test_matrix)
    accuracy = _accuracy_score(targets[test_indices], test_probabilities)
    auc = _roc_auc_score(targets[test_indices], test_probabilities)
    importance = booster.get_score(importance_type="gain")
    top_features = [
        {"feature": feature_name, "gain": round(float(gain), 4)}
        for feature_name, gain in sorted(importance.items(), key=lambda item: item[1], reverse=True)[:8]
    ]

    metadata = {
        "trainedAt": datetime.utcnow().isoformat(timespec="seconds") + "Z",
        "datasetSize": int(features.shape[0]),
        "featureCount": len(feature_names),
        "accuracy": round(accuracy, 4),
        "auc": round(auc, 4),
        "algorithm": "XGBoost",
        "version": "cancellation-risk-xgboost-v2",
        "topFeatures": top_features,
    }

    model_path.parent.mkdir(parents=True, exist_ok=True)
    booster_path = model_path.with_name(f"{model_path.stem}.booster.json")
    booster.save_model(booster_path)
    model_path.write_text(
        json.dumps(
            {
                "categories": categories,
                "feature_names": feature_names,
                "metadata": metadata,
                "booster_file": booster_path.name,
            },
            indent=2,
        ),
        encoding="utf-8",
    )
    return load_model(model_path)


def load_model(model_path: Path) -> TrainedCancellationModel:
    raw_bundle = json.loads(model_path.read_text(encoding="utf-8"))
    booster = xgb.Booster()
    booster.load_model(model_path.with_name(raw_bundle["booster_file"]))
    return TrainedCancellationModel(
        booster=booster,
        categories={key: list(values) for key, values in raw_bundle["categories"].items()},
        feature_names=list(raw_bundle["feature_names"]),
        metadata=dict(raw_bundle["metadata"]),
    )


def risk_level_from_probability(probability: float) -> str:
    if probability >= 0.7:
        return "HIGH"
    if probability >= 0.4:
        return "MEDIUM"
    return "LOW"


def recommended_action_from_probability(probability: float) -> str:
    if probability >= 0.7:
        return "Intervention prioritaire: appel client, rappel et verification manuelle."
    if probability >= 0.4:
        return "Envoyer un rappel automatique et surveiller la reservation."
    return "Client stable, aucune action urgente."
