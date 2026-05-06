from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date, datetime
import numpy as np
import scipy.sparse as sp
import joblib
import os

app = FastAPI(
    title="Film Programming Advisor",
    description="Recommande si un film vaut la peine d'être programmé CE MOIS-CI",
    version="3.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

MODEL_PATH = os.path.join(os.path.dirname(__file__), "hit_classifier_final.pkl")
artifacts  = joblib.load(MODEL_PATH)

model        = artifacts["model"]
scaler       = artifacts["scaler"]
lang_enc     = artifacts["lang_encoder"]
season_enc   = artifacts["season_encoder"]
tfidf        = artifacts["tfidf"]
ALL_GENRES   = artifacts["all_genres"]
METRICS      = artifacts["metrics"]
SEUIL        = artifacts["seuil"]


# ── Helpers ───────────────────────────────────────────────────────

def get_season(month: int) -> str:
    if month in [6, 7, 8]:  return 'summer'
    if month in [11, 12]:   return 'holiday'
    if month in [3, 4, 5]:  return 'spring'
    return 'other'


def compute_temporal_score(release_date_str: Optional[str]) -> dict:
    """
    Calcule le facteur temporel selon la fenêtre d'exploitation.
    Retourne un score entre 0.0 et 1.0 + un label lisible.
    """
    today = date.today()

    if not release_date_str:
        return {"score": 0.5, "label": "Date inconnue", "weeks": None, "status": "unknown"}

    try:
        release = datetime.strptime(release_date_str[:10], "%Y-%m-%d").date()
    except ValueError:
        return {"score": 0.5, "label": "Date invalide", "weeks": None, "status": "unknown"}

    days_diff = (today - release).days
    weeks     = days_diff / 7

    # Film pas encore sorti
    if days_diff < 0:
        days_until = abs(days_diff)
        if days_until <= 14:
            return {"score": 0.95, "label": f"Sort dans {days_until}j — fort buzz d'attente", "weeks": round(weeks, 1), "status": "upcoming_soon"}
        elif days_until <= 60:
            return {"score": 0.80, "label": f"Sort dans {days_until}j — bonne anticipation", "weeks": round(weeks, 1), "status": "upcoming"}
        else:
            return {"score": 0.50, "label": f"Sort dans {days_until}j — trop tôt pour programmer", "weeks": round(weeks, 1), "status": "future"}

    # Film sorti — courbe de décroissance
    if weeks <= 1:
        score, label, status = 1.00, "1ère semaine — audience maximale", "peak"
    elif weeks <= 2:
        score, label, status = 0.85, "2ème semaine — très forte demande", "hot"
    elif weeks <= 3:
        score, label, status = 0.65, "3ème semaine — demande soutenue", "good"
    elif weeks <= 4:
        score, label, status = 0.45, "4ème semaine — demande en baisse", "declining"
    elif weeks <= 6:
        score, label, status = 0.25, "5-6 semaines — fin d'exploitation", "ending"
    elif weeks <= 12:
        score, label, status = 0.10, "Plus de 6 semaines — très peu de demande", "low"
    elif weeks <= 52:
        score, label, status = 0.05, "Plus de 3 mois — film récent mais fini", "old"
    else:
        years = int(weeks / 52)
        score, label, status = 0.02, f"Sorti il y a {years} an(s) — classique uniquement", "classic"

    return {"score": score, "label": label, "weeks": round(weeks, 1), "status": status}


def get_recommendation(final_score: float, temporal_status: str) -> dict:
    """Génère la recommandation finale pour le cinéma."""
    if temporal_status in ("peak", "hot", "upcoming_soon"):
        if final_score >= 0.65:
            return {"text": "Fortement recommandé à programmer maintenant", "level": "strong_yes"}
        elif final_score >= 0.40:
            return {"text": "Recommandé — audience attendue satisfaisante", "level": "yes"}
        else:
            return {"text": "Potentiel limité mais fenêtre favorable", "level": "maybe"}

    elif temporal_status in ("good", "upcoming"):
        if final_score >= 0.55:
            return {"text": "Bon moment pour le programmer", "level": "yes"}
        else:
            return {"text": "Audience modérée attendue", "level": "maybe"}

    elif temporal_status in ("declining", "ending"):
        if final_score >= 0.70:
            return {"text": "Dernière chance — demande encore acceptable", "level": "maybe"}
        else:
            return {"text": "Déconseillé — fenêtre d'exploitation terminée", "level": "no"}

    elif temporal_status == "future":
        return {"text": "Trop tôt — à réévaluer à l'approche de la sortie", "level": "wait"}

    else:
        if final_score >= 0.50:
            return {"text": "Film ancien — envisageable en séance spéciale uniquement", "level": "special"}
        return {"text": "Non recommandé pour une programmation standard", "level": "no"}


# ── Schémas ───────────────────────────────────────────────────────

class PredictRequest(BaseModel):
    title:          str            = Field(..., example="Avatar")
    budget:         float          = Field(..., gt=0, example=237000000)
    runtime:        float          = Field(..., gt=0, example=162)
    release_year:   int            = Field(..., example=2009)
    release_month:  int            = Field(..., ge=1, le=12, example=12)
    release_date:   Optional[str]  = Field(default=None, example="2009-12-18")
    language:       str            = Field(..., example="en")
    genres:         List[str]      = Field(..., example=["Action", "Adventure"])
    overview:       Optional[str]  = Field(default="")

class PredictResponse(BaseModel):
    title:            str
    ai_score:         float
    temporal_score:   float
    final_score:      float
    temporal_label:   str
    temporal_status:  str
    weeks_since_release: Optional[float]
    recommendation:   str
    recommendation_level: str
    label:            str


# ── Endpoints ─────────────────────────────────────────────────────

@app.get("/")
def health():
    best = artifacts.get("model_name", "Gradient Boosting")
    return {"status": "ok", "model_name": best,
            "version": "3.0 — fenêtre d'exploitation", "metrics": METRICS.get(best, {})}


@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    try:
        # ── 1. Score IA ──────────────────────────────────────────
        g_vec = np.array([[int(g in req.genres) for g in ALL_GENRES]])

        try:    lang_encoded = lang_enc.transform([req.language])[0]
        except: lang_encoded = 0

        season = get_season(req.release_month)
        try:    season_encoded = season_enc.transform([season])[0]
        except: season_encoded = 0

        budget_log = np.log1p(req.budget)
        num_vec    = scaler.transform([[budget_log, req.runtime,
                                        req.release_year, req.release_month]])
        txt_vec    = tfidf.transform([req.overview or ""])

        X = sp.hstack([sp.csr_matrix(num_vec), sp.csr_matrix(g_vec),
                       sp.csr_matrix([[lang_encoded]]),
                       sp.csr_matrix([[season_encoded]]), txt_vec])

        proba    = model.predict_proba(X)[0]
        ai_score = round(float(proba[1]), 4)

        # ── 2. Score temporel ────────────────────────────────────
        temporal = compute_temporal_score(req.release_date)
        t_score  = temporal["score"]

        # ── 3. Score final combiné ───────────────────────────────
        final_score = round(ai_score * t_score, 4)

        # ── 4. Recommandation ────────────────────────────────────
        reco = get_recommendation(final_score, temporal["status"])

        # Label lisible
        if reco["level"] in ("strong_yes", "yes"):
            label = "À programmer"
        elif reco["level"] == "maybe":
            label = "À considérer"
        elif reco["level"] == "wait":
            label = "Trop tôt"
        elif reco["level"] == "special":
            label = "Séance spéciale"
        else:
            label = "Non recommandé"

        return {
            "title":                 req.title,
            "ai_score":              ai_score,
            "temporal_score":        round(t_score, 4),
            "final_score":           final_score,
            "temporal_label":        temporal["label"],
            "temporal_status":       temporal["status"],
            "weeks_since_release":   temporal["weeks"],
            "recommendation":        reco["text"],
            "recommendation_level":  reco["level"],
            "label":                 label,
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/genres")
def get_genres():
    return {"genres": ALL_GENRES}