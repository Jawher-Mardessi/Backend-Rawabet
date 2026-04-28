from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime, timezone
import pickle
import re
from pathlib import Path
from typing import Any

import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression


@dataclass
class FeedbackAiAssets:
    vectorizer: TfidfVectorizer
    moderation_model: LogisticRegression
    sentiment_model: LogisticRegression
    metadata: dict[str, Any]
    profanity_hints: list[str]


PROFANITY_HINTS = [
    "idiot",
    "imbecile",
    "stupide",
    "stupid",
    "nul",
    "hate",
    "merde",
    "fuck",
    "shit",
    "con",
    "debile",
    "salope",
    "connard",
    "ta gueule",
    "ferme ta gueule",
]

POSITIVE_SENTENCES = [
    "super film, j'ai adore l'ambiance",
    "experience magnifique au cinema",
    "excellent service et projection parfaite",
    "tres bonne qualite image et son",
    "j'ai passe un tres bon moment",
    "incroyable seance, bravo a l'equipe",
    "the movie was amazing and inspiring",
    "great experience, everything was perfect",
    "awesome atmosphere and very kind staff",
    "i loved this film so much",
]

NEUTRAL_SENTENCES = [
    "film correct, rien de special",
    "la seance etait moyenne",
    "experience acceptable sans plus",
    "c'etait okay mais pas exceptionnel",
    "avis neutre sur la projection",
    "service normal, salle propre",
    "the movie was fine, nothing more",
    "average experience at the theater",
    "it was okay overall",
    "neutral feeling about the event",
]

NEGATIVE_CLEAN_SENTENCES = [
    "je n'ai pas aime ce film",
    "experience decevante et longue",
    "projection pas top, je suis decu",
    "service lent et organisation faible",
    "tres mauvais rapport qualite prix",
    "le son etait mauvais pendant la seance",
    "i disliked this movie",
    "bad experience, very disappointing",
    "not satisfied with this screening",
    "the event quality was poor",
]

TOXIC_SENTENCES = [
    "ce film est nul, equipe idiot",
    "service de merde, vous etes stupide",
    "film de shit, organisation debile",
    "ferme ta gueule, cinema nul",
    "connard de service, experience merde",
    "this is a fuck movie, stupid staff",
    "you are idiots and this is shit",
    "hate this place, service is trash",
    "bande de cons, film nul",
    "salope de seance, experience horrible",
]


def _normalize_text(value: str | None) -> str:
    return " ".join(str(value or "").strip().lower().split())


def _build_feedback_dataset() -> pd.DataFrame:
    rows: list[dict[str, Any]] = []

    for sentence in POSITIVE_SENTENCES:
        rows.append(
            {
                "text": sentence,
                "toxic": 0,
                "sentiment": "positive",
            }
        )

    for sentence in NEUTRAL_SENTENCES:
        rows.append(
            {
                "text": sentence,
                "toxic": 0,
                "sentiment": "neutral",
            }
        )

    for sentence in NEGATIVE_CLEAN_SENTENCES:
        rows.append(
            {
                "text": sentence,
                "toxic": 0,
                "sentiment": "negative",
            }
        )

    for sentence in TOXIC_SENTENCES:
        rows.append(
            {
                "text": sentence,
                "toxic": 1,
                "sentiment": "negative",
            }
        )

    expanded_rows: list[dict[str, Any]] = []
    suffixes = [
        "",
        " aujourdhui",
        " vraiment",
        " a rawabet",
        " pour moi",
    ]
    for row in rows:
        for suffix in suffixes:
            expanded_rows.append(
                {
                    "text": _normalize_text(f"{row['text']}{suffix}"),
                    "toxic": row["toxic"],
                    "sentiment": row["sentiment"],
                }
            )

    return pd.DataFrame(expanded_rows).drop_duplicates(subset=["text"]).reset_index(drop=True)


def _normalize_sentiment(value: Any) -> str:
    normalized = _normalize_text(str(value))
    if normalized == "positive":
        return "positive"
    if normalized == "negative":
        return "negative"
    if normalized == "neutral":
        return "neutral"
    return "neutral"


def _load_or_create_feedback_dataset(dataset_path: Path) -> pd.DataFrame:
    required_columns = {"text", "toxic", "sentiment"}

    if dataset_path.exists():
        dataset = pd.read_csv(dataset_path, encoding="utf-8")
        if not required_columns.issubset(set(dataset.columns)):
            raise ValueError(
                f"Dataset {dataset_path} must include columns: text,toxic,sentiment"
            )
    else:
        dataset = _build_feedback_dataset()
        dataset_path.parent.mkdir(parents=True, exist_ok=True)
        dataset.to_csv(dataset_path, index=False, encoding="utf-8")

    dataset = dataset.copy()
    dataset["text"] = dataset["text"].astype(str).map(_normalize_text)
    dataset["toxic"] = dataset["toxic"].fillna(0).astype(int).clip(lower=0, upper=1)
    dataset["sentiment"] = dataset["sentiment"].map(_normalize_sentiment)
    dataset = dataset[dataset["text"].str.len() > 0]
    dataset = dataset.drop_duplicates(subset=["text"]).reset_index(drop=True)

    if len(dataset) < 20:
        bootstrap = _build_feedback_dataset()
        dataset = (
            pd.concat([dataset, bootstrap], ignore_index=True)
            .drop_duplicates(subset=["text"])
            .reset_index(drop=True)
        )

    return dataset


def train_feedback_ai_model(dataset_path: Path, model_path: Path) -> FeedbackAiAssets:
    dataset = _load_or_create_feedback_dataset(dataset_path)
    dataset_path.parent.mkdir(parents=True, exist_ok=True)
    dataset.to_csv(dataset_path, index=False, encoding="utf-8")

    vectorizer = TfidfVectorizer(
        ngram_range=(1, 2),
        min_df=1,
        max_features=5000,
        strip_accents="unicode",
    )
    x_matrix = vectorizer.fit_transform(dataset["text"].astype(str))

    moderation_model = LogisticRegression(
        max_iter=1500,
        class_weight="balanced",
        random_state=42,
    )
    moderation_model.fit(x_matrix, dataset["toxic"].astype(int))

    sentiment_model = LogisticRegression(
        max_iter=1500,
        class_weight="balanced",
        random_state=42,
    )
    sentiment_model.fit(x_matrix, dataset["sentiment"].astype(str))

    metadata = {
        "version": "feedback-ai-v1",
        "trainedAt": datetime.now(timezone.utc).isoformat(),
        "datasetSize": int(len(dataset)),
        "algorithm": "tfidf+logistic-regression",
    }

    model_path.parent.mkdir(parents=True, exist_ok=True)
    with model_path.open("wb") as file:
        pickle.dump(
            {
                "vectorizer": vectorizer,
                "moderation_model": moderation_model,
                "sentiment_model": sentiment_model,
                "metadata": metadata,
                "profanity_hints": PROFANITY_HINTS,
            },
            file,
        )

    return FeedbackAiAssets(
        vectorizer=vectorizer,
        moderation_model=moderation_model,
        sentiment_model=sentiment_model,
        metadata=metadata,
        profanity_hints=list(PROFANITY_HINTS),
    )


def load_feedback_ai_model(dataset_path: Path, model_path: Path) -> FeedbackAiAssets:
    if model_path.exists():
        try:
            with model_path.open("rb") as file:
                payload = pickle.load(file)
            return FeedbackAiAssets(
                vectorizer=payload["vectorizer"],
                moderation_model=payload["moderation_model"],
                sentiment_model=payload["sentiment_model"],
                metadata=payload.get("metadata", {}),
                profanity_hints=list(payload.get("profanity_hints", PROFANITY_HINTS)),
            )
        except Exception:
            # If loading fails, retrain from dataset.
            pass

    return train_feedback_ai_model(dataset_path, model_path)


def _detect_matches(text: str, hints: list[str]) -> list[dict[str, Any]]:
    matches: list[dict[str, Any]] = []
    normalized_text = _normalize_text(text)
    for word in hints:
        escaped = re.escape(_normalize_text(word))
        if not escaped:
            continue
        occurrences = len(re.findall(rf"\b{escaped}\b", normalized_text))
        if occurrences > 0:
            matches.append(
                {
                    "word": word,
                    "occurrences": occurrences,
                }
            )
    return matches


def analyze_feedback_moderation(assets: FeedbackAiAssets, text: str | None) -> dict[str, Any]:
    normalized_text = _normalize_text(text)
    if not normalized_text:
        return {
            "hasBadWords": False,
            "score": 0.0,
            "severity": "clean",
            "matches": [],
            "model": str(assets.metadata.get("version", "feedback-ai-v1")),
        }

    vectorized = assets.vectorizer.transform([normalized_text])
    probabilities = assets.moderation_model.predict_proba(vectorized)[0]
    toxic_probability = float(probabilities[1])
    has_bad_words = toxic_probability >= 0.5

    if toxic_probability >= 0.8:
        severity = "critical"
    elif toxic_probability >= 0.5:
        severity = "warning"
    else:
        severity = "clean"

    matches = _detect_matches(normalized_text, assets.profanity_hints)

    return {
        "hasBadWords": has_bad_words,
        "score": round(toxic_probability, 4),
        "severity": severity,
        "matches": matches,
        "model": str(assets.metadata.get("version", "feedback-ai-v1")),
    }


def _sentiment_emoji(label: str) -> str:
    if label == "positive":
        return "😊"
    if label == "negative":
        return "😞"
    if label == "neutral":
        return "😐"
    return "😶"


def analyze_feedback_sentiment(
    assets: FeedbackAiAssets,
    text: str | None,
    note: float | None = None,
) -> dict[str, Any]:
    normalized_text = _normalize_text(text)
    if not normalized_text:
        return {
            "label": "unknown",
            "score": 0.0,
            "confidence": 0.0,
            "emoji": _sentiment_emoji("unknown"),
            "model": str(assets.metadata.get("version", "feedback-ai-v1")),
        }

    vectorized = assets.vectorizer.transform([normalized_text])
    classes = list(assets.sentiment_model.classes_)
    probabilities = assets.sentiment_model.predict_proba(vectorized)[0]
    proba_by_class = {
        str(label): float(probabilities[index])
        for index, label in enumerate(classes)
    }

    positive_score = proba_by_class.get("positive", 0.0)
    negative_score = proba_by_class.get("negative", 0.0)
    neutral_score = proba_by_class.get("neutral", 0.0)

    score = positive_score - negative_score
    if note is not None:
        try:
            note_value = max(1.0, min(5.0, float(note)))
            note_score = (note_value - 3.0) / 2.0
            score = (0.75 * score) + (0.25 * note_score)
        except (TypeError, ValueError):
            pass

    if score >= 0.25:
        label = "positive"
    elif score <= -0.25:
        label = "negative"
    else:
        label = "neutral" if neutral_score > 0 else "unknown"

    confidence = max(positive_score, negative_score, neutral_score)

    return {
        "label": label,
        "score": round(float(score), 4),
        "confidence": round(float(confidence), 4),
        "emoji": _sentiment_emoji(label),
        "model": str(assets.metadata.get("version", "feedback-ai-v1")),
    }
