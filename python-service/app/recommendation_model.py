from __future__ import annotations

from dataclasses import dataclass
import json
import os
from pathlib import Path
from typing import Any
import unicodedata
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


CATALOG_ROWS = [
    ("The Shawshank Redemption", "Drama", 1994, 9.3, "Frank Darabont", "Two imprisoned men bond over a number of years."),
    ("The Godfather", "Crime,Drama", 1972, 9.2, "Francis Ford Coppola", "The aging patriarch of an organized crime dynasty transfers control."),
    ("The Dark Knight", "Action,Crime,Drama", 2008, 9.0, "Christopher Nolan", "When the menace known as the Joker wreaks havoc on Gotham."),
    ("Pulp Fiction", "Crime,Drama", 1994, 8.9, "Quentin Tarantino", "The lives of two mob hitmen, a boxer, and diner bandits."),
    ("Forrest Gump", "Drama,Romance", 1994, 8.8, "Robert Zemeckis", "The presidencies of Kennedy and Nixon, and the events of Vietnam."),
    ("Inception", "Action,Sci-Fi,Thriller", 2010, 8.8, "Christopher Nolan", "A thief steals corporate secrets through dream-sharing technology."),
    ("The Matrix", "Action,Sci-Fi", 1999, 8.7, "Wachowski Sisters", "A computer hacker learns about the true nature of reality."),
    ("Goodfellas", "Crime,Drama", 1990, 8.7, "Martin Scorsese", "The story of Henry Hill and his life in the mob."),
    ("Fight Club", "Drama", 1999, 8.8, "David Fincher", "An office worker forms an underground fight club."),
    ("Star Wars: Episode V", "Action,Adventure,Sci-Fi", 1980, 8.7, "Irvin Kershner", "Luke Skywalker begins Jedi training after the Empire overpowers the rebels."),
    ("The Lord of the Rings: The Return of the King", "Action,Adventure,Drama", 2003, 9.0, "Peter Jackson", "Gandalf and Aragorn lead the World of Men against Sauron's army."),
    ("Spirited Away", "Animation,Adventure,Family", 2001, 8.6, "Hayao Miyazaki", "A young girl wanders into a world ruled by gods and spirits."),
    ("Interstellar", "Sci-Fi,Drama", 2014, 8.6, "Christopher Nolan", "A team of explorers travel through a wormhole in space."),
    ("The Lion King", "Animation,Family,Musical", 1994, 8.5, "Roger Allers", "Lion prince Simba and his father are targeted by his bitter uncle."),
    ("Gladiator", "Action,Drama", 2000, 8.5, "Ridley Scott", "A former Roman General sets out to exact vengeance."),
    ("Titanic", "Drama,Romance", 1997, 7.9, "James Cameron", "An aristocrat falls in love with a kind but poor artist."),
    ("Avatar", "Action,Adventure,Fantasy", 2009, 7.9, "James Cameron", "A Marine is dispatched to Pandora on a unique mission."),
    ("Avengers: Endgame", "Action,Adventure,Drama", 2019, 8.4, "Anthony Russo", "After Infinity War, the universe is in ruins."),
    ("Joker", "Crime,Drama,Thriller", 2019, 8.8, "Todd Phillips", "A troubled comedian embarks on a downward spiral."),
    ("Parasite", "Comedy,Drama,Thriller", 2019, 8.6, "Bong Joon-ho", "Greed and class discrimination threaten a symbiotic relationship."),
    ("1917", "Guerre", 2019, 8.2, "Sam Mendes", "Two young British soldiers race against time to deliver a lifesaving message during World War I."),
    ("Marriage Story", "Drame", 2019, 7.9, "Noah Baumbach", "A stage director and an actress navigate a painful divorce while trying to protect their family."),
    ("Once Upon a Time in Hollywood", "Comédie", 2019, 7.6, "Quentin Tarantino", "A fading television actor and his stunt double chase one last shot at fame in late-1960s Hollywood."),
    ("Tenet", "Sci-Fi", 2020, 7.3, "Christopher Nolan", "A secret agent manipulates time inversion to stop a global catastrophe."),
    ("The Irishman", "Drame", 2019, 7.8, "Martin Scorsese", "An aging hitman looks back on the choices and crimes that shaped his life."),
]

DEFAULT_FILMS_API_URL = os.environ.get("RAWABET_FILMS_API_URL", "http://localhost:8081/rawabet/api/films")


@dataclass
class RecommendationAssets:
    ratings: pd.DataFrame
    user_item_matrix: pd.DataFrame
    user_similarity_matrix: pd.DataFrame
    catalog: pd.DataFrame
    content_similarity_matrix: Any
    title_index: dict[str, int]


def _normalize_title(title: str) -> str:
    normalized = " ".join(str(title).strip().lower().split())
    return "".join(
        character
        for character in unicodedata.normalize("NFKD", normalized)
        if not unicodedata.combining(character)
    )


def _normalize_catalog_rating(value: Any) -> float:
    rating = _safe_float(value, default=0.0)
    if rating > 5:
        rating = rating / 2.0
    return round(rating, 2)


def _merge_genres(*values: Any) -> str:
    merged: list[str] = []
    seen: set[str] = set()

    for value in values:
        for part in str(value or "").split(","):
            label = str(part).strip()
            normalized = _normalize_title(label)
            if not label or not normalized or normalized in seen:
                continue
            seen.add(normalized)
            merged.append(label)

    return ",".join(merged)


def _safe_int(value: Any) -> int | None:
    if pd.isna(value):
        return None
    try:
        return int(value)
    except (TypeError, ValueError):
        return None


def _safe_float(value: Any, default: float = 0.0) -> float:
    if pd.isna(value):
        return default
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def _extract_year(value: Any) -> int | None:
    if pd.isna(value) or value is None:
        return None

    raw = str(value).strip()
    if not raw:
        return None

    try:
        return int(raw[:4])
    except (TypeError, ValueError):
        return None


def _fetch_backend_film_catalog() -> list[dict[str, Any]]:
    request = Request(DEFAULT_FILMS_API_URL, headers={"Accept": "application/json"})
    try:
        with urlopen(request, timeout=2.5) as response:
            payload = json.loads(response.read().decode("utf-8"))
    except (HTTPError, URLError, TimeoutError, json.JSONDecodeError, ValueError):
        return []

    return payload if isinstance(payload, list) else []


def _build_seed_rows(backend_catalog: list[dict[str, Any]] | None = None) -> dict[str, dict[str, Any]]:
    rows_by_title: dict[str, dict[str, Any]] = {}

    for item in backend_catalog or []:
        title = str(item.get("title") or "").strip()
        if not title:
            continue

        normalized_title = _normalize_title(title)
        average_rating = _normalize_catalog_rating(item.get("averageRating"))
        total_reviews = _safe_int(item.get("totalReviews")) or 0
        rows_by_title[normalized_title] = {
            "film_id": _safe_int(item.get("id")),
            "film_title": title,
            "genre": str(item.get("genre") or ""),
            "year": _extract_year(item.get("releaseDate")),
            "rating": average_rating,
            "director": str(item.get("director") or "Unknown"),
            "description": str(item.get("synopsis") or "No description available in the local catalog."),
            "posterUrl": str(item.get("posterUrl") or ""),
            "mean_rating": average_rating,
            "rating_count": total_reviews,
        }

    for title, genre, year, rating, director, description in CATALOG_ROWS:
        normalized_title = _normalize_title(title)
        if normalized_title in rows_by_title:
            rows_by_title[normalized_title]["genre"] = _merge_genres(
                rows_by_title[normalized_title].get("genre"),
                genre,
            )
            if rows_by_title[normalized_title].get("year") is None:
                rows_by_title[normalized_title]["year"] = year
            if not str(rows_by_title[normalized_title].get("director") or "").strip():
                rows_by_title[normalized_title]["director"] = director
            if not str(rows_by_title[normalized_title].get("description") or "").strip():
                rows_by_title[normalized_title]["description"] = description
            continue
        rows_by_title[normalized_title] = {
            "film_id": None,
            "film_title": title,
            "genre": genre,
            "year": year,
            "rating": _normalize_catalog_rating(rating),
            "director": director,
            "description": description,
            "posterUrl": "",
            "mean_rating": _normalize_catalog_rating(rating),
            "rating_count": 0,
        }

    return rows_by_title


def _genre_matches(value: Any, genre: str | None) -> bool:
    if not genre:
        return True

    requested_parts = [
        _normalize_title(part)
        for part in str(genre).split(",")
        if _normalize_title(part)
    ]
    if not requested_parts:
        return True

    movie_parts = [
        _normalize_title(part)
        for part in str(value or "").split(",")
        if _normalize_title(part)
    ]

    return any(
        requested in movie_part or movie_part in requested
        for requested in requested_parts
        for movie_part in movie_parts
    )


def _normalize_titles(titles: str | list[str]) -> list[str]:
    if isinstance(titles, str):
        values = [part.strip() for part in titles.split(",")]
    else:
        values = [str(title).strip() for title in titles]

    normalized_titles: list[str] = []
    seen: set[str] = set()
    for value in values:
        normalized = _normalize_title(value)
        if not normalized or normalized in seen:
            continue
        seen.add(normalized)
        normalized_titles.append(normalized)

    return normalized_titles


def _apply_catalog_filters(
    catalog: pd.DataFrame,
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
    exclude_titles: set[str] | None = None,
) -> pd.DataFrame:
    filtered = catalog.copy()

    if genre:
        filtered = filtered[filtered["genre"].apply(lambda value: _genre_matches(value, genre))]

    if start_year is not None or end_year is not None:
        filtered["year"] = pd.to_numeric(filtered["year"], errors="coerce")
        if start_year is not None:
            filtered = filtered[filtered["year"] >= start_year]
        if end_year is not None:
            filtered = filtered[filtered["year"] <= end_year]

    if exclude_titles:
        filtered = filtered[
            ~filtered["film_title"].astype(str).map(_normalize_title).isin(exclude_titles)
        ]

    return filtered


def _build_catalog(ratings: pd.DataFrame, backend_catalog: list[dict[str, Any]] | None = None) -> pd.DataFrame:
    rating_catalog = (
        ratings.groupby("film_id", as_index=False)
        .agg(
            film_title=("film_title", "first"),
            genre=("genre", "first"),
            mean_rating=("rating", "mean"),
            rating_count=("rating", "count"),
        )
        .sort_values("film_id")
    )

    rows_by_title = _build_seed_rows(backend_catalog)

    for row in rating_catalog.itertuples(index=False):
        key = _normalize_title(row.film_title)
        if key in rows_by_title:
            rows_by_title[key]["film_id"] = int(row.film_id)
            rows_by_title[key]["genre"] = _merge_genres(rows_by_title[key].get("genre"), row.genre)
            rows_by_title[key]["mean_rating"] = round(float(row.mean_rating), 2)
            rows_by_title[key]["rating"] = round(float(row.mean_rating), 2)
            rows_by_title[key]["rating_count"] = int(row.rating_count)
        else:
            rows_by_title[key] = {
                "film_id": int(row.film_id),
                "film_title": str(row.film_title),
                "genre": str(row.genre),
                "year": None,
                "rating": round(float(row.mean_rating), 2),
                "director": "Unknown",
                "description": "No description available in the local catalog.",
                "posterUrl": "",
                "mean_rating": round(float(row.mean_rating), 2),
                "rating_count": int(row.rating_count),
            }

    next_id = int(rating_catalog["film_id"].max()) + 1 if not rating_catalog.empty else 1
    for row in rows_by_title.values():
        if row["film_id"] is None:
            row["film_id"] = next_id
            next_id += 1

    catalog = pd.DataFrame(rows_by_title.values())
    catalog["features"] = (
        catalog["genre"].fillna("")
        + " "
        + catalog["director"].fillna("")
        + " "
        + catalog["description"].fillna("")
    )
    return catalog.sort_values(["mean_rating", "rating_count"], ascending=[False, False]).reset_index(drop=True)


def load_recommendation_assets(csv_path: Path) -> RecommendationAssets:
    backend_catalog = _fetch_backend_film_catalog()
    ratings = pd.read_csv(csv_path, encoding="utf-8")
    ratings["user_id"] = ratings["user_id"].astype(int)
    ratings["rating"] = ratings["rating"].astype(float)
    title_to_backend_id = {
        _normalize_title(str(item.get("title") or "")): _safe_int(item.get("id"))
        for item in backend_catalog
        if str(item.get("title") or "").strip()
    }
    ratings["film_id"] = ratings.apply(
        lambda row: title_to_backend_id.get(_normalize_title(row["film_title"])) or int(row["film_id"]),
        axis=1,
    )
    ratings["film_id"] = ratings["film_id"].astype(int)

    catalog = _build_catalog(ratings, backend_catalog)

    user_item_matrix = ratings.pivot_table(
        index="user_id",
        columns="film_id",
        values="rating",
        aggfunc="mean",
        fill_value=0.0,
    )

    user_similarity_matrix = pd.DataFrame()
    if not user_item_matrix.empty:
        user_similarity = cosine_similarity(user_item_matrix)
        user_similarity_matrix = pd.DataFrame(
            user_similarity,
            index=user_item_matrix.index,
            columns=user_item_matrix.index,
        )

    tfidf = TfidfVectorizer(stop_words="english", max_features=5000)
    content_matrix = tfidf.fit_transform(catalog["features"])
    content_similarity_matrix = cosine_similarity(content_matrix, content_matrix)
    title_index = {_normalize_title(title): index for index, title in enumerate(catalog["film_title"].tolist())}

    return RecommendationAssets(
        ratings=ratings,
        user_item_matrix=user_item_matrix,
        user_similarity_matrix=user_similarity_matrix,
        catalog=catalog,
        content_similarity_matrix=content_similarity_matrix,
        title_index=title_index,
    )


def _match_percent(score: float, max_score: float = 10.0) -> int:
    percent = int(round(score * 100)) if max_score <= 1.0 else int(round((score / max_score) * 100))
    return min(99, max(1, percent))


def _format_movie(row: pd.Series, score: float | None = None, similarity: float | None = None, kind: str = "catalog") -> dict[str, Any]:
    rating = round(_safe_float(row.get("mean_rating", row.get("rating", 0.0))), 2)
    final_score = round(float(score), 2) if score is not None else rating
    return {
        "id": int(row["film_id"]),
        "title": str(row["film_title"]),
        "genre": str(row.get("genre") or ""),
        "posterUrl": str(row.get("posterUrl") or ""),
        "score": final_score,
        "matchPercent": _match_percent(similarity if similarity is not None else final_score, 1.0 if similarity is not None else 10.0),
        "year": _safe_int(row.get("year")),
        "rating": rating,
        "director": str(row.get("director") or "Unknown"),
        "description": str(row.get("description") or ""),
        "similarityScore": round(float(similarity), 4) if similarity is not None else None,
        "recommendationType": kind,
        "ratingCount": int(row.get("rating_count") or 0),
    }


def _top_catalog(assets: RecommendationAssets, limit: int) -> list[dict[str, Any]]:
    top = assets.catalog.sort_values(["mean_rating", "rating_count"], ascending=[False, False]).head(limit)
    return [_format_movie(row, kind="top") for _, row in top.iterrows()]


def _top_catalog_filtered(
    assets: RecommendationAssets,
    limit: int,
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
    exclude_titles: set[str] | None = None,
    kind: str = "top",
) -> list[dict[str, Any]]:
    filtered = _apply_catalog_filters(
        assets.catalog,
        genre=genre,
        start_year=start_year,
        end_year=end_year,
        exclude_titles=exclude_titles,
    )
    top = filtered.sort_values(["mean_rating", "rating_count", "year"], ascending=[False, False, False]).head(limit)
    return [_format_movie(row, kind=kind) for _, row in top.iterrows()]


def _movie_matches_filters(
    movie: dict[str, Any],
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
) -> bool:
    if genre and not _genre_matches(movie.get("genre"), genre):
        return False

    year = _safe_int(movie.get("year"))
    if start_year is not None and (year is None or year < start_year):
        return False
    if end_year is not None and (year is None or year > end_year):
        return False

    return True


def get_recommendations_for_user(assets: RecommendationAssets, user_id: int, limit: int = 5) -> list[dict[str, Any]]:
    if user_id not in assets.user_item_matrix.index or assets.user_similarity_matrix.empty:
        return _top_catalog(assets, limit)

    active_user_ratings = assets.user_item_matrix.loc[user_id]
    seen_movies = set(active_user_ratings[active_user_ratings > 0].index.tolist())
    user_similarity = assets.user_similarity_matrix.loc[user_id].drop(labels=[user_id], errors="ignore")
    similar_users = user_similarity[user_similarity > 0].sort_values(ascending=False)

    predictions: dict[int, float] = {}
    for film_id in assets.user_item_matrix.columns:
        if film_id in seen_movies:
            continue

        weighted_score = 0.0
        similarity_sum = 0.0
        for similar_user_id, similarity_value in similar_users.items():
            candidate_rating = float(assets.user_item_matrix.at[similar_user_id, film_id])
            if candidate_rating <= 0:
                continue
            weighted_score += candidate_rating * float(similarity_value)
            similarity_sum += float(similarity_value)

        if similarity_sum > 0:
            predictions[int(film_id)] = weighted_score / similarity_sum

    if not predictions:
        return _top_catalog(assets, limit)

    catalog_by_id = assets.catalog.set_index("film_id", drop=False)
    recommendations: list[dict[str, Any]] = []
    for film_id, score in sorted(predictions.items(), key=lambda item: item[1], reverse=True):
        if film_id not in catalog_by_id.index:
            continue
        recommendations.append(_format_movie(catalog_by_id.loc[film_id], score=score, kind="collaborative"))
        if len(recommendations) >= limit:
            break

    return recommendations or _top_catalog(assets, limit)


def get_recommendations_by_movies(
    assets: RecommendationAssets,
    titles: str | list[str],
    limit: int = 5,
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
) -> list[dict[str, Any]]:
    normalized_titles = _normalize_titles(titles)
    matched_indices = [
        assets.title_index[normalized_title]
        for normalized_title in normalized_titles
        if normalized_title in assets.title_index
    ]

    if not matched_indices:
        fallback_kind = "filter" if genre or start_year is not None or end_year is not None else "top"
        return _top_catalog_filtered(
            assets,
            limit,
            genre=genre,
            start_year=start_year,
            end_year=end_year,
            kind=fallback_kind,
        )

    similarity_scores = np.asarray(assets.content_similarity_matrix[matched_indices]).mean(axis=0).ravel()

    catalog = assets.catalog.copy()
    catalog["aggregated_similarity"] = similarity_scores
    filtered = _apply_catalog_filters(
        catalog,
        genre=genre,
        start_year=start_year,
        end_year=end_year,
        exclude_titles=set(normalized_titles),
    )
    filtered = filtered.sort_values(
        ["aggregated_similarity", "mean_rating", "rating_count"],
        ascending=[False, False, False],
    ).head(limit)

    recommendations = [
        _format_movie(row, similarity=float(row["aggregated_similarity"]), kind="content")
        for _, row in filtered.iterrows()
    ]

    if len(recommendations) >= limit:
        return recommendations

    seen_titles = {_normalize_title(item["title"]) for item in recommendations}
    seen_titles.update(normalized_titles)
    recommendations.extend(
        _top_catalog_filtered(
            assets,
            limit - len(recommendations),
            genre=genre,
            start_year=start_year,
            end_year=end_year,
            exclude_titles=seen_titles,
        )
    )
    return recommendations[:limit]


def get_recommendations_by_movie(
    assets: RecommendationAssets,
    title: str | list[str],
    limit: int = 5,
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
) -> list[dict[str, Any]]:
    return get_recommendations_by_movies(
        assets,
        titles=title,
        limit=limit,
        genre=genre,
        start_year=start_year,
        end_year=end_year,
    )


def get_recommendations_by_film_id(assets: RecommendationAssets, film_id: int, limit: int = 5) -> list[dict[str, Any]]:
    matches = assets.catalog.index[assets.catalog["film_id"] == film_id].tolist()
    if not matches:
        return []
    title = str(assets.catalog.iloc[matches[0]]["film_title"])
    return get_recommendations_by_movie(assets, title, limit)


def get_recommendations_by_genre(assets: RecommendationAssets, genre: str, limit: int = 8) -> list[dict[str, Any]]:
    return _top_catalog_filtered(assets, limit, genre=genre, kind="genre")


def get_recommendations_by_year(assets: RecommendationAssets, start_year: int, end_year: int, limit: int = 10) -> list[dict[str, Any]]:
    return _top_catalog_filtered(
        assets,
        limit,
        start_year=start_year,
        end_year=end_year,
        kind="year",
    )


def get_feedback_recommendations(
    assets: RecommendationAssets,
    user_id: int,
    titles: list[str] | None = None,
    genre: str | None = None,
    start_year: int | None = None,
    end_year: int | None = None,
    limit: int = 6,
) -> list[dict[str, Any]]:
    normalized_titles = _normalize_titles(titles or [])
    if normalized_titles:
        return get_recommendations_by_movies(
            assets,
            titles=normalized_titles,
            limit=limit,
            genre=genre,
            start_year=start_year,
            end_year=end_year,
        )

    if genre or start_year is not None or end_year is not None:
        return _top_catalog_filtered(
            assets,
            limit,
            genre=genre,
            start_year=start_year,
            end_year=end_year,
            kind="filter",
        )

    personalized = [
        movie
        for movie in get_recommendations_for_user(assets, user_id, max(limit * 4, limit))
        if _movie_matches_filters(movie, genre=genre, start_year=start_year, end_year=end_year)
    ]

    if len(personalized) >= limit:
        return personalized[:limit]

    seen_titles = {_normalize_title(item["title"]) for item in personalized}
    personalized.extend(
        _top_catalog_filtered(
            assets,
            limit - len(personalized),
            genre=genre,
            start_year=start_year,
            end_year=end_year,
            exclude_titles=seen_titles,
        )
    )
    return personalized[:limit]


def get_top_movies(assets: RecommendationAssets, limit: int = 10, sort_by: str = "rating") -> list[dict[str, Any]]:
    if sort_by == "year":
        movies = assets.catalog.sort_values(["year", "mean_rating"], ascending=[False, False]).head(limit)
    else:
        movies = assets.catalog.sort_values(["mean_rating", "rating_count"], ascending=[False, False]).head(limit)
    return [_format_movie(row, kind="top") for _, row in movies.iterrows()]


def get_random_movie(assets: RecommendationAssets, limit: int = 3) -> dict[str, Any]:
    movie = assets.catalog.sample(1).iloc[0]
    return {
        "movie": _format_movie(movie, kind="random"),
        "suggestions": get_recommendations_by_movie(assets, str(movie["film_title"]), limit),
    }


def get_recommendation_options(assets: RecommendationAssets) -> dict[str, Any]:
    genres: set[str] = set()
    for value in assets.catalog["genre"].dropna().tolist():
        genres.update(part.strip() for part in str(value).split(",") if part.strip())

    years = pd.to_numeric(assets.catalog["year"], errors="coerce").dropna()
    return {
        "catalogSize": int(len(assets.catalog)),
        "genres": sorted(genres),
        "movies": sorted(assets.catalog["film_title"].astype(str).tolist()),
        "minYear": int(years.min()) if not years.empty else None,
        "maxYear": int(years.max()) if not years.empty else None,
    }
