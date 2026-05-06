from __future__ import annotations

from typing import Literal

from pydantic import BaseModel, Field


class MovieRecommendation(BaseModel):
    id: int
    title: str
    genre: str
    posterUrl: str = ""
    score: float
    matchPercent: int
    year: int | None = None
    rating: float | None = None
    director: str | None = None
    description: str | None = None
    similarityScore: float | None = None
    recommendationType: str | None = None
    ratingCount: int | None = None


class RandomMovieResponse(BaseModel):
    movie: MovieRecommendation
    suggestions: list[MovieRecommendation]


class RecommendationOptions(BaseModel):
    catalogSize: int
    genres: list[str]
    movies: list[str]
    minYear: int | None = None
    maxYear: int | None = None


class TicketPredictionInput(BaseModel):
    reservationId: int = Field(..., ge=1)
    userId: int = Field(..., ge=1)
    seanceId: int = Field(..., ge=1)
    dateReservation: str | None = None
    seanceDateHeure: str | None = None
    prixBase: float | None = None
    langue: str | None = None
    filmGenre: str | None = None
    seatNumber: int | None = None
    userTotalBookings: int = 0
    userCancelledBookings: int = 0
    userRecentBookings30d: int = 0
    loyaltyLevel: str | None = None
    loyaltyPoints: int | None = 0
    statut: str | None = None


class BatchPredictionRequest(BaseModel):
    tickets: list[TicketPredictionInput]


class TicketPredictionResult(BaseModel):
    reservationId: int
    cancellationProbability: float
    riskLevel: Literal["LOW", "MEDIUM", "HIGH"]
    recommendedAction: str


class BatchPredictionResponse(BaseModel):
    modelVersion: str
    trainedAt: str
    datasetSize: int
    predictions: list[TicketPredictionResult]


class FeedbackTextRequest(BaseModel):
    text: str | None = None
    comment: str | None = None
    commentaire: str | None = None
    message: str | None = None
    note: float | None = None


class FeedbackModerationMatch(BaseModel):
    word: str
    occurrences: int = Field(default=1, ge=1)


class FeedbackModerationResponse(BaseModel):
    hasBadWords: bool
    score: float
    severity: Literal["clean", "warning", "critical"]
    matches: list[FeedbackModerationMatch]
    model: str


class FeedbackSentimentResponse(BaseModel):
    label: Literal["positive", "neutral", "negative", "unknown"]
    score: float
    confidence: float
    emoji: str
    model: str
