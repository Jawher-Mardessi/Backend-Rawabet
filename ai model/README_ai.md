# AI Model — Film Box-Office Hit Predictor

Prédit si un film sera un **hit au box-office (>$100M)** et recommande s'il vaut la peine d'être programmé dans un cinéma à une date donnée.

## Architecture

```
Angular (front) → Spring Boot → FastAPI (main.py) → hit_classifier_final.pkl
```

## Fichiers

| Fichier | Description |
|---|---|
| `main.py` | API FastAPI exposant l'endpoint `/predict` |
| `train.py` | Script pour réentraîner le modèle |
| `hit_classifier_final.pkl` | Modèle Random Forest entraîné |
| `requirements.txt` | Dépendances Python |

## Installation

```bash
pip install -r requirements.txt
```

## Lancer le serveur

```bash
uvicorn main:app --reload --port 8000
```

API disponible sur `http://localhost:8000`  
Documentation interactive : `http://localhost:8000/docs`

## Réentraîner le modèle

1. Télécharger le dataset : https://www.kaggle.com/datasets/tmdb/tmdb-movie-metadata
2. Placer `tmdb_5000_movies.csv` dans ce dossier
3. Lancer :
```bash
python train.py
```

## Logique de prédiction

Le modèle combine deux scores :

- **Score IA** — prédit si le film a le profil d'un blockbuster (budget, genre, durée, synopsis, saison de sortie)
- **Score temporel** — calcule la fraîcheur selon les semaines depuis la sortie

```
Score final = Score IA × Score temporel
```

| Fenêtre | Facteur temporel |
|---|---|
| Semaine 1-2 | 100% |
| Semaine 3-4 | 60% |
| Semaine 5-6 | 25% |
| > 6 semaines | 5-10% |
| Classique (>1 an) | 2% |

## Métriques du modèle

- **Accuracy** : 78%
- **AUC-ROC** : 0.84
- **F1 macro** : 0.76
- Dataset : 3 157 films TMDB (2000-2016)
