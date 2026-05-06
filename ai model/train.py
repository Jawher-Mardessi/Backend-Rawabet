"""
train.py — Script d'entraînement du modèle de prédiction box-office
Entraîne un Random Forest sur le dataset TMDB 5000 Movies.

Usage :
    pip install -r requirements.txt
    python train.py

Le dataset tmdb_5000_movies.csv doit être dans le même dossier.
Téléchargeable sur : https://www.kaggle.com/datasets/tmdb/tmdb-movie-metadata
"""

import pandas as pd
import numpy as np
import ast
import joblib
import warnings
import scipy.sparse as sp
from datetime import date

from sklearn.model_selection import train_test_split, StratifiedKFold, cross_val_score
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import classification_report, accuracy_score, f1_score, roc_auc_score

warnings.filterwarnings('ignore')

# ── Config ────────────────────────────────────────────────────────
DATASET_PATH = 'tmdb_5000_movies.csv'
MODEL_OUTPUT  = 'hit_classifier_final.pkl'
SEUIL         = 100_000_000  # $100M

ALL_GENRES = ['Drama','Comedy','Thriller','Action','Romance','Adventure',
              'Crime','Science Fiction','Horror','Family','Fantasy',
              'Mystery','Animation','History','Music','War','Documentary','Western']


def parse_list(g):
    try: return [x['name'] for x in ast.literal_eval(g)]
    except: return []


def get_season(month: int) -> str:
    if month in [6, 7, 8]:  return 'summer'
    if month in [11, 12]:   return 'holiday'
    if month in [3, 4, 5]:  return 'spring'
    return 'other'


def main():
    print("=== Chargement du dataset ===")
    movies = pd.read_csv(DATASET_PATH)

    # ── 1. Nettoyage & label ─────────────────────────────────────
    df = movies[(movies['budget'] > 100_000) & (movies['revenue'] > 100_000)].copy()
    df['hit']          = (df['revenue'] >= SEUIL).astype(int)
    df['release_year'] = pd.to_datetime(df['release_date'], errors='coerce').dt.year
    df['release_month']= pd.to_datetime(df['release_date'], errors='coerce').dt.month
    df['runtime']      = df['runtime'].fillna(df['runtime'].median())
    df['overview']     = df['overview'].fillna('')
    df = df.dropna(subset=['release_year'])

    print(f"Films utilisables : {len(df)}")
    print(f"Hits (>$100M)     : {df['hit'].sum()} ({df['hit'].mean()*100:.0f}%)")
    print(f"Non-hits          : {(df['hit']==0).sum()} ({(1-df['hit'].mean())*100:.0f}%)")

    # ── 2. Feature engineering ───────────────────────────────────
    df['genres_list'] = df['genres'].apply(parse_list)
    for g in ALL_GENRES:
        df[f'g_{g.replace(" ","_")}'] = df['genres_list'].apply(lambda gs: int(g in gs))

    df['season'] = df['release_month'].apply(get_season)
    season_enc   = LabelEncoder()
    df['season_enc'] = season_enc.fit_transform(df['season'])

    lang_enc = LabelEncoder()
    df['lang_enc'] = lang_enc.fit_transform(df['original_language'])

    df['budget_log'] = np.log1p(df['budget'])

    scaler   = StandardScaler()
    num_cols = ['budget_log', 'runtime', 'release_year', 'release_month']
    X_num    = scaler.fit_transform(df[num_cols])

    genre_cols = [f'g_{g.replace(" ","_")}' for g in ALL_GENRES]
    X_gen  = df[genre_cols].values
    X_lang = df['lang_enc'].values.reshape(-1, 1)
    X_seas = df['season_enc'].values.reshape(-1, 1)

    tfidf = TfidfVectorizer(max_features=150, stop_words='english', ngram_range=(1, 2))
    X_txt = tfidf.fit_transform(df['overview'])

    X = sp.hstack([sp.csr_matrix(X_num), sp.csr_matrix(X_gen),
                   sp.csr_matrix(X_lang), sp.csr_matrix(X_seas), X_txt])
    y = df['hit']

    X_tr, X_te, y_tr, y_te = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y)

    print(f"\nTrain : {X_tr.shape[0]} | Test : {X_te.shape[0]} | Features : {X.shape[1]}")

    # ── 3. Entraînement 3 modèles ────────────────────────────────
    models = {
        'Logistic Regression': LogisticRegression(max_iter=1000, C=1.0,
                                   class_weight='balanced', random_state=42),
        'Random Forest':       RandomForestClassifier(n_estimators=200, max_depth=12,
                                   min_samples_leaf=3, class_weight='balanced',
                                   random_state=42, n_jobs=-1),
        'Gradient Boosting':   GradientBoostingClassifier(n_estimators=200,
                                   max_depth=4, learning_rate=0.05, random_state=42),
    }

    cv      = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    results = {}

    for name, clf in models.items():
        clf.fit(X_tr, y_tr)
        y_pred  = clf.predict(X_te)
        y_proba = clf.predict_proba(X_te)[:, 1]
        acc     = accuracy_score(y_te, y_pred)
        f1      = f1_score(y_te, y_pred, average='macro')
        auc     = roc_auc_score(y_te, y_proba)
        cv_f1   = cross_val_score(clf, X, y, cv=cv, scoring='f1_macro', n_jobs=-1)
        results[name] = {'acc': acc, 'f1': f1, 'auc': auc,
                         'cv_mean': cv_f1.mean(), 'cv_std': cv_f1.std(), 'clf': clf}
        print(f"\n=== {name} ===")
        print(f"  Accuracy : {acc:.4f} | F1 macro : {f1:.4f} | AUC-ROC : {auc:.4f}")
        print(f"  CV F1    : {cv_f1.mean():.4f} ± {cv_f1.std():.4f}")
        print(classification_report(y_te, y_pred, target_names=['Non-hit','Hit']))

    # ── 4. Meilleur modèle ───────────────────────────────────────
    best_name = max(results, key=lambda k: results[k]['auc'])
    best_clf  = results[best_name]['clf']
    print(f"\nMeilleur modèle : {best_name} (AUC={results[best_name]['auc']:.4f})")

    # ── 5. Sauvegarde ────────────────────────────────────────────
    artifacts = {
        'model':             best_clf,
        'model_name':        best_name,
        'all_models':        {k: v['clf'] for k, v in results.items()},
        'tfidf':             tfidf,
        'scaler':            scaler,
        'lang_encoder':      lang_enc,
        'season_encoder':    season_enc,
        'genre_cols':        genre_cols,
        'all_genres':        ALL_GENRES,
        'numeric_features':  num_cols,
        'seuil':             SEUIL,
        'classes':           ['Non-hit', 'Hit'],
        'trained_on':        str(date.today()),
        'metrics': {
            name: {k: round(float(v), 4) for k, v in r.items()
                   if k in ('acc', 'f1', 'auc', 'cv_mean', 'cv_std')}
            for name, r in results.items()
        }
    }
    joblib.dump(artifacts, MODEL_OUTPUT)
    print(f"\nModèle sauvegardé : {MODEL_OUTPUT}")
    print(f"Taille : {__import__('os').path.getsize(MODEL_OUTPUT) // 1024} KB")


if __name__ == '__main__':
    main()
