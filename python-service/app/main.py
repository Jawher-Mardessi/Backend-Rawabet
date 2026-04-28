from __future__ import annotations

from pathlib import Path

from fastapi import FastAPI, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse

from .cancellation_dataset import DatasetConfig, build_dataset
from .cancellation_model import (
    load_model,
    recommended_action_from_probability,
    risk_level_from_probability,
    train_model_from_csv,
)
from .feedback_ai_model import (
    analyze_feedback_moderation,
    analyze_feedback_sentiment,
    load_feedback_ai_model,
)
from .recommendation_model import (
    get_feedback_recommendations,
    get_random_movie,
    get_recommendation_options,
    get_recommendations_by_film_id,
    get_recommendations_by_genre,
    get_recommendations_by_movie,
    get_recommendations_by_year,
    get_recommendations_for_user,
    get_top_movies,
    load_recommendation_assets,
)
from .schemas import (
    BatchPredictionRequest,
    BatchPredictionResponse,
    FeedbackModerationResponse,
    FeedbackSentimentResponse,
    FeedbackTextRequest,
    MovieRecommendation,
    RandomMovieResponse,
    RecommendationOptions,
    TicketPredictionResult,
)


APP_DIR = Path(__file__).resolve().parent
SERVICE_DIR = APP_DIR.parent

LOCAL_DATA_DIR = SERVICE_DIR / "data"
LOCAL_MODEL_DIR = SERVICE_DIR / "models"

RECOMMENDATION_DATASET_PATH = LOCAL_DATA_DIR / "recommendation_dataset.csv"
CANCELLATION_DATASET_PATH = LOCAL_DATA_DIR / "cancellation_training_dataset.csv"
CANCELLATION_MODEL_PATH = LOCAL_MODEL_DIR / "cancellation_model.json"
FEEDBACK_DATASET_PATH = LOCAL_DATA_DIR / "feedback_badwords_dataset.csv"
FEEDBACK_MODEL_PATH = LOCAL_MODEL_DIR / "feedback_badwords_model.pkl"


def ensure_assets():
    LOCAL_DATA_DIR.mkdir(parents=True, exist_ok=True)
    LOCAL_MODEL_DIR.mkdir(parents=True, exist_ok=True)

    if not RECOMMENDATION_DATASET_PATH.exists():
        raise FileNotFoundError(
            f"Missing recommendation dataset at {RECOMMENDATION_DATASET_PATH}. "
            "Run setup/build assets from Backend-Rawabet first."
        )

    if not CANCELLATION_DATASET_PATH.exists():
        build_dataset(DatasetConfig(output_path=CANCELLATION_DATASET_PATH))
    if not CANCELLATION_MODEL_PATH.exists():
        train_model_from_csv(CANCELLATION_DATASET_PATH, CANCELLATION_MODEL_PATH)

    cancellation_model = load_model(CANCELLATION_MODEL_PATH)
    recommendation_assets = load_recommendation_assets(RECOMMENDATION_DATASET_PATH)
    feedback_assets = load_feedback_ai_model(FEEDBACK_DATASET_PATH, FEEDBACK_MODEL_PATH)
    return cancellation_model, recommendation_assets, feedback_assets


app = FastAPI(title="Rawabet Python AI Microservice", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",
        "http://127.0.0.1:4200",
        "http://localhost:4201",
        "http://127.0.0.1:4201",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

CANCELLATION_MODEL, RECOMMENDATION_ASSETS, FEEDBACK_AI_ASSETS = ensure_assets()


def _limit(value: int, upper: int = 20) -> int:
    return max(1, min(value, upper))


def _feedback_text(payload: FeedbackTextRequest) -> str:
    return (
        payload.text
        or payload.comment
        or payload.commentaire
        or payload.message
        or ""
    ).strip()


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.get("/model/info")
def model_info() -> dict[str, object]:
    return {
        "modelVersion": CANCELLATION_MODEL.metadata["version"],
        "trainedAt": CANCELLATION_MODEL.metadata["trainedAt"],
        "datasetSize": CANCELLATION_MODEL.metadata["datasetSize"],
        "accuracy": CANCELLATION_MODEL.metadata["accuracy"],
        "auc": CANCELLATION_MODEL.metadata["auc"],
        "algorithm": CANCELLATION_MODEL.metadata["algorithm"],
        "topFeatures": CANCELLATION_MODEL.metadata["topFeatures"],
    }


@app.get("/reco/{user_id}", response_model=list[MovieRecommendation])
def get_recommendations(user_id: int, limit: int = Query(5, ge=1, le=20)) -> list[MovieRecommendation]:
    return [
        MovieRecommendation(**item)
        for item in get_recommendations_for_user(RECOMMENDATION_ASSETS, user_id, _limit(limit))
    ]


@app.get("/reco-feedback/{user_id}", response_model=list[MovieRecommendation])
def get_feedback_recommendations_endpoint(
    user_id: int,
    limit: int = Query(6, ge=1, le=20),
    titles: list[str] = Query(default=[]),
    genre: str | None = Query(default=None),
    start_year: int | None = Query(default=None, ge=1900, le=2100),
    end_year: int | None = Query(default=None, ge=1900, le=2100),
) -> list[MovieRecommendation]:
    if start_year is not None and end_year is not None and start_year > end_year:
        start_year, end_year = end_year, start_year

    return [
        MovieRecommendation(**item)
        for item in get_feedback_recommendations(
            RECOMMENDATION_ASSETS,
            user_id=user_id,
            titles=titles,
            genre=genre,
            start_year=start_year,
            end_year=end_year,
            limit=_limit(limit),
        )
    ]


@app.get("/recommendations/options", response_model=RecommendationOptions)
def recommendation_options() -> RecommendationOptions:
    return RecommendationOptions(**get_recommendation_options(RECOMMENDATION_ASSETS))


@app.get("/recommendations/movie", response_model=list[MovieRecommendation])
def recommendations_by_movie(
    title: list[str] = Query(default=[]),
    limit: int = Query(5, ge=1, le=20),
    genre: str | None = Query(default=None),
    start_year: int | None = Query(default=None, ge=1900, le=2100),
    end_year: int | None = Query(default=None, ge=1900, le=2100),
) -> list[MovieRecommendation]:
    if start_year is not None and end_year is not None and start_year > end_year:
        start_year, end_year = end_year, start_year

    return [
        MovieRecommendation(**item)
        for item in get_recommendations_by_movie(
            RECOMMENDATION_ASSETS,
            title,
            _limit(limit),
            genre=genre,
            start_year=start_year,
            end_year=end_year,
        )
    ]


@app.get("/recommendations/movie/{film_id}", response_model=list[MovieRecommendation])
def recommendations_by_film_id(film_id: int, limit: int = Query(5, ge=1, le=20)) -> list[MovieRecommendation]:
    return [
        MovieRecommendation(**item)
        for item in get_recommendations_by_film_id(RECOMMENDATION_ASSETS, film_id, _limit(limit))
    ]


@app.get("/recommendations/genre/{genre}", response_model=list[MovieRecommendation])
def recommendations_by_genre(genre: str, limit: int = Query(8, ge=1, le=20)) -> list[MovieRecommendation]:
    return [
        MovieRecommendation(**item)
        for item in get_recommendations_by_genre(RECOMMENDATION_ASSETS, genre, _limit(limit))
    ]


@app.get("/recommendations/year", response_model=list[MovieRecommendation])
def recommendations_by_year(
    start_year: int = Query(1990, ge=1900, le=2100),
    end_year: int = Query(2024, ge=1900, le=2100),
    limit: int = Query(10, ge=1, le=20),
) -> list[MovieRecommendation]:
    if start_year > end_year:
        start_year, end_year = end_year, start_year
    return [
        MovieRecommendation(**item)
        for item in get_recommendations_by_year(RECOMMENDATION_ASSETS, start_year, end_year, _limit(limit))
    ]


@app.get("/recommendations/top", response_model=list[MovieRecommendation])
def top_recommendations(
    limit: int = Query(10, ge=1, le=20),
    sort_by: str = Query("rating", pattern="^(rating|year)$"),
) -> list[MovieRecommendation]:
    return [
        MovieRecommendation(**item)
        for item in get_top_movies(RECOMMENDATION_ASSETS, _limit(limit), sort_by)
    ]


@app.get("/recommendations/random", response_model=RandomMovieResponse)
def random_recommendation(limit: int = Query(3, ge=1, le=10)) -> RandomMovieResponse:
    return RandomMovieResponse(**get_random_movie(RECOMMENDATION_ASSETS, _limit(limit, upper=10)))


@app.get("/recommendations/ui", response_class=HTMLResponse)
def recommendation_ui() -> HTMLResponse:
    return HTMLResponse(_recommendation_ui_html())


@app.post("/predictions/batch", response_model=BatchPredictionResponse)
def predict_batch(request: BatchPredictionRequest) -> BatchPredictionResponse:
    predictions: list[TicketPredictionResult] = []

    for ticket in request.tickets:
        probability = CANCELLATION_MODEL.predict_proba(
            {
                "user_total_bookings": ticket.userTotalBookings,
                "user_cancelled_bookings": ticket.userCancelledBookings,
                "user_recent_bookings_30d": ticket.userRecentBookings30d,
                "loyalty_level": ticket.loyaltyLevel or "BRONZE",
                "loyalty_points": ticket.loyaltyPoints or 0,
                "date_reservation": ticket.dateReservation,
                "seance_date_heure": ticket.seanceDateHeure,
                "prix_base": ticket.prixBase or 0,
                "langue": ticket.langue or "FR",
                "film_genre": ticket.filmGenre or "Drama",
                "seat_number": ticket.seatNumber or 0,
            }
        )
        predictions.append(
            TicketPredictionResult(
                reservationId=ticket.reservationId,
                cancellationProbability=round(probability, 4),
                riskLevel=risk_level_from_probability(probability),
                recommendedAction=recommended_action_from_probability(probability),
            )
        )

    return BatchPredictionResponse(
        modelVersion=str(CANCELLATION_MODEL.metadata["version"]),
        trainedAt=str(CANCELLATION_MODEL.metadata["trainedAt"]),
        datasetSize=int(CANCELLATION_MODEL.metadata["datasetSize"]),
        predictions=predictions,
    )


@app.post("/feedback/moderation", response_model=FeedbackModerationResponse)
def feedback_moderation(request: FeedbackTextRequest) -> FeedbackModerationResponse:
    result = analyze_feedback_moderation(FEEDBACK_AI_ASSETS, _feedback_text(request))
    return FeedbackModerationResponse(**result)


@app.post("/feedback/sentiment", response_model=FeedbackSentimentResponse)
def feedback_sentiment(request: FeedbackTextRequest) -> FeedbackSentimentResponse:
    result = analyze_feedback_sentiment(
        FEEDBACK_AI_ASSETS,
        _feedback_text(request),
        request.note,
    )
    return FeedbackSentimentResponse(**result)


def _recommendation_ui_html() -> str:
    return """
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Rawabet recommandations films</title>
  <style>
    body { margin: 0; font-family: Segoe UI, Arial, sans-serif; background: #f6f7fb; color: #172033; }
    header { background: #172033; color: white; padding: 28px min(5vw, 56px); }
    header h1 { margin: 0; font-size: clamp(28px, 4vw, 44px); letter-spacing: 0; }
    header p { max-width: 760px; margin: 10px 0 0; color: #c9d3e5; }
    main { display: grid; grid-template-columns: 300px 1fr; gap: 24px; padding: 24px min(5vw, 56px); }
    aside, section { background: white; border: 1px solid #dde3ef; border-radius: 8px; box-shadow: 0 8px 24px rgba(20, 32, 55, 0.08); }
    aside { padding: 18px; align-self: start; position: sticky; top: 16px; }
    section { padding: 22px; min-height: 520px; }
    label { display: block; font-weight: 700; margin: 14px 0 6px; }
    select, input { width: 100%; box-sizing: border-box; padding: 11px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 15px; }
    .tabs { display: grid; gap: 8px; }
    .tabs button, .action { border: 0; border-radius: 6px; padding: 12px 14px; cursor: pointer; font-weight: 800; }
    .tabs button { background: #e9edf5; color: #253149; text-align: left; }
    .tabs button.active, .action { background: #e7334f; color: white; }
    .action { width: 100%; margin-top: 16px; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 14px; margin-top: 18px; }
    .card { border: 1px solid #dce3ee; border-radius: 8px; padding: 16px; background: #fbfcff; }
    .card h3 { margin: 0 0 6px; font-size: 19px; }
    .meta { color: #56657d; font-size: 14px; line-height: 1.45; }
    .score { display: flex; gap: 8px; flex-wrap: wrap; margin: 12px 0; }
    .pill { background: #eef2f8; color: #24314a; padding: 6px 9px; border-radius: 999px; font-size: 13px; font-weight: 800; }
    .bar { height: 8px; background: #edf1f6; border-radius: 999px; overflow: hidden; margin-top: 12px; }
    .bar span { display: block; height: 100%; background: linear-gradient(90deg, #e7334f, #f59e0b); }
    .empty { color: #667085; padding: 30px; text-align: center; }
    .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    @media (max-width: 820px) { main { grid-template-columns: 1fr; } aside { position: static; } }
  </style>
</head>
<body>
  <header>
    <h1>Systeme de recommandation de films</h1>
    <p>Filtrage base sur le contenu: genres, realisateurs, descriptions, notes, periode et similarite.</p>
  </header>
  <main>
    <aside>
      <div class="tabs">
        <button data-mode="movie" class="active">Basee sur un film</button>
        <button data-mode="genre">Par genre</button>
        <button data-mode="year">Par periode</button>
        <button data-mode="top">Top films</button>
        <button data-mode="random">Aleatoire</button>
      </div>
      <div id="controls"></div>
    </aside>
    <section>
      <h2 id="title">Recommandations basees sur un film</h2>
      <div id="result" class="empty">Choisis des criteres puis lance la recherche.</div>
    </section>
  </main>
<script>
let options = { movies: [], genres: [], minYear: 1970, maxYear: 2024 };
let mode = 'movie';
const controls = document.getElementById('controls');
const result = document.getElementById('result');
const title = document.getElementById('title');
  const titles = { movie: 'Recommandations basees sur plusieurs films', genre: 'Recommandations par genre', year: 'Recommandations par periode', top: 'Top films du catalogue', random: 'Decouvrir un film au hasard' };
  function optionList(values) { return values.map(v => `<option value="${String(v).replaceAll('"', '&quot;')}">${v}</option>`).join(''); }
  function selectedMovieQuery() {
    const select = document.getElementById('movie');
    if (!select) return '';
    return Array.from(select.selectedOptions || [])
      .map(option => `title=${encodeURIComponent(option.value)}`)
      .join('&');
  }
  function renderControls() {
  title.textContent = titles[mode];
  if (mode === 'movie') controls.innerHTML = `<label>Films (plusieurs)</label><select id="movie" multiple size="8">${optionList(options.movies)}</select><label>Genre</label><select id="movie-genre"><option value="">Tous</option>${optionList(options.genres)}</select><div class="row"><div><label>Debut</label><input id="movie-start" type="number" value="${options.minYear || 1990}"></div><div><label>Fin</label><input id="movie-end" type="number" value="${options.maxYear || 2024}"></div></div><label>Nombre</label><input id="limit" type="number" min="3" max="20" value="8"><button class="action" onclick="search()">Obtenir des recommandations</button>`;
  else if (mode === 'genre') controls.innerHTML = `<label>Genre</label><select id="genre">${optionList(options.genres)}</select><label>Nombre</label><input id="limit" type="number" min="3" max="20" value="8"><button class="action" onclick="search()">Trouver des films</button>`;
  else if (mode === 'year') controls.innerHTML = `<div class="row"><div><label>Debut</label><input id="start" type="number" value="${options.minYear || 1990}"></div><div><label>Fin</label><input id="end" type="number" value="${options.maxYear || 2024}"></div></div><label>Nombre</label><input id="limit" type="number" min="3" max="20" value="10"><button class="action" onclick="search()">Rechercher</button>`;
  else if (mode === 'top') controls.innerHTML = `<label>Trier par</label><select id="sort"><option value="rating">Note</option><option value="year">Annee</option></select><label>Nombre</label><input id="limit" type="number" min="5" max="20" value="10"><button class="action" onclick="search()">Afficher le top</button>`;
  else controls.innerHTML = `<label>Suggestions similaires</label><input id="limit" type="number" min="1" max="10" value="3"><button class="action" onclick="search()">Film aleatoire</button>`;
}
function card(movie) {
  const year = movie.year ? ` (${movie.year})` : '';
  const sim = movie.similarityScore != null ? `<span class="pill">Similarite ${(movie.similarityScore * 100).toFixed(1)}%</span>` : '';
  return `<article class="card"><h3>${movie.title}${year}</h3><div class="meta">${movie.genre || ''}<br>${movie.director || 'Unknown'}</div><div class="score"><span class="pill">Note ${Number(movie.rating || movie.score).toFixed(1)}/5</span><span class="pill">Match ${movie.matchPercent}%</span>${sim}</div><p class="meta">${movie.description || ''}</p><div class="bar"><span style="width:${movie.matchPercent}%"></span></div></article>`;
}
function renderMovies(items) {
  if (!items || !items.length) { result.className = 'empty'; result.textContent = 'Aucune recommandation trouvee.'; return; }
  result.className = 'grid'; result.innerHTML = items.map(card).join('');
}
async function search() {
  result.className = 'empty'; result.textContent = 'Analyse en cours...';
  const limit = document.getElementById('limit')?.value || 5;
  let url = '';
  if (mode === 'movie') {
    const titlesQuery = selectedMovieQuery();
    if (!titlesQuery) {
      result.className = 'empty';
      result.textContent = 'Selectionne un ou plusieurs films.';
      return;
    }
    const genre = document.getElementById('movie-genre').value;
    const start = document.getElementById('movie-start').value;
    const end = document.getElementById('movie-end').value;
    url = `/recommendations/movie?${titlesQuery}&limit=${limit}`;
    if (genre) url += `&genre=${encodeURIComponent(genre)}`;
    if (start) url += `&start_year=${encodeURIComponent(start)}`;
    if (end) url += `&end_year=${encodeURIComponent(end)}`;
  }
  if (mode === 'genre') url = `/recommendations/genre/${encodeURIComponent(document.getElementById('genre').value)}?limit=${limit}`;
  if (mode === 'year') url = `/recommendations/year?start_year=${document.getElementById('start').value}&end_year=${document.getElementById('end').value}&limit=${limit}`;
  if (mode === 'top') url = `/recommendations/top?sort_by=${document.getElementById('sort').value}&limit=${limit}`;
  if (mode === 'random') url = `/recommendations/random?limit=${limit}`;
  const data = await (await fetch(url)).json();
  if (mode === 'random') result.innerHTML = `<h2>${data.movie.title}</h2><div class="grid">${card(data.movie)}</div><h2>Suggestions</h2><div class="grid">${data.suggestions.map(card).join('')}</div>`;
  else renderMovies(data);
}
document.querySelectorAll('.tabs button').forEach(button => button.addEventListener('click', () => {
  document.querySelectorAll('.tabs button').forEach(item => item.classList.remove('active'));
  button.classList.add('active'); mode = button.dataset.mode; renderControls();
}));
fetch('/recommendations/options').then(response => response.json()).then(data => { options = data; renderControls(); });
</script>
</body>
</html>
"""
