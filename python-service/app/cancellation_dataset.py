from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime, timedelta
import csv
import random
from pathlib import Path


LANGUAGES = ["FR", "EN", "AR", "VO"]
GENRES = ["Action", "Comedy", "Drama", "Horror", "Animation", "Sci-Fi", "Romance", "Thriller"]
LOYALTY_LEVELS = ["BRONZE", "SILVER", "GOLD"]


@dataclass
class DatasetConfig:
    output_path: Path
    user_count: int = 120
    rows_per_user_min: int = 6
    rows_per_user_max: int = 18
    seed: int = 42


def _clamp(value: float, lower: float, upper: float) -> float:
    return max(lower, min(upper, value))


def _sample_status(cancel_probability: float, rng: random.Random) -> str:
    if rng.random() < cancel_probability:
        return "CANCELLED"
    return "CONFIRMED" if rng.random() < 0.72 else "PENDING"


def build_dataset(config: DatasetConfig) -> int:
    rng = random.Random(config.seed)
    config.output_path.parent.mkdir(parents=True, exist_ok=True)

    fieldnames = [
        "reservation_id",
        "user_id",
        "user_total_bookings",
        "user_cancelled_bookings",
        "user_recent_bookings_30d",
        "loyalty_level",
        "loyalty_points",
        "date_reservation",
        "seance_date_heure",
        "days_until_seance",
        "booking_lead_hours",
        "seance_hour",
        "seance_weekday",
        "is_weekend",
        "prix_base",
        "langue",
        "film_genre",
        "seat_number",
        "is_premium_seat",
        "statut",
        "cancellation_target",
    ]

    reservation_id = 1
    rows_written = 0
    now = datetime(2026, 4, 20, 10, 0, 0)

    with config.output_path.open("w", newline="", encoding="utf-8") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
        writer.writeheader()

        for user_id in range(1, config.user_count + 1):
            total_rows = rng.randint(config.rows_per_user_min, config.rows_per_user_max)
            loyalty_level = rng.choices(LOYALTY_LEVELS, weights=[0.48, 0.34, 0.18], k=1)[0]
            loyalty_points = {
                "BRONZE": rng.randint(20, 180),
                "SILVER": rng.randint(160, 520),
                "GOLD": rng.randint(500, 1200),
            }[loyalty_level]

            user_cancelled_bookings = 0
            reservation_dates: list[datetime] = []

            for booking_index in range(total_rows):
                reservation_date = now - timedelta(days=rng.randint(1, 180), hours=rng.randint(0, 18))
                seance_date = reservation_date + timedelta(hours=rng.randint(6, 24 * 20))
                seance_hour = seance_date.hour
                seance_weekday = seance_date.weekday()
                is_weekend = 1 if seance_weekday >= 5 else 0
                prix_base = round(rng.uniform(12.0, 32.0), 2)
                language = rng.choices(LANGUAGES, weights=[0.46, 0.24, 0.18, 0.12], k=1)[0]
                genre = rng.choices(
                    GENRES,
                    weights=[0.14, 0.16, 0.15, 0.08, 0.09, 0.13, 0.12, 0.13],
                    k=1,
                )[0]
                seat_number = rng.randint(1, 120)
                is_premium_seat = 1 if seat_number <= 12 else 0
                days_until_seance = max((seance_date.date() - reservation_date.date()).days, 0)
                booking_lead_hours = max(int((seance_date - reservation_date).total_seconds() // 3600), 1)
                recent_bookings = sum(
                    1 for previous_date in reservation_dates if (reservation_date - previous_date).days <= 30
                )

                cancel_probability = 0.12
                cancel_probability += min(user_cancelled_bookings * 0.035, 0.32)
                cancel_probability += 0.15 if booking_lead_hours <= 12 else 0
                cancel_probability += 0.08 if booking_lead_hours <= 48 else 0
                cancel_probability += 0.06 if genre in {"Horror", "Thriller"} else 0
                cancel_probability += 0.05 if language == "VO" else 0
                cancel_probability += 0.05 if prix_base >= 26 else 0
                cancel_probability += 0.04 if is_weekend else 0
                cancel_probability += 0.03 if seance_hour >= 21 else 0
                cancel_probability += 0.04 if recent_bookings >= 4 else 0
                cancel_probability -= 0.04 if loyalty_level == "SILVER" else 0
                cancel_probability -= 0.08 if loyalty_level == "GOLD" else 0
                cancel_probability -= min(loyalty_points / 10000, 0.08)
                cancel_probability = _clamp(cancel_probability, 0.04, 0.88)

                status = _sample_status(cancel_probability, rng)
                cancellation_target = 1 if status == "CANCELLED" else 0
                if cancellation_target:
                    user_cancelled_bookings += 1

                reservation_dates.append(reservation_date)

                writer.writerow(
                    {
                        "reservation_id": reservation_id,
                        "user_id": user_id,
                        "user_total_bookings": booking_index + 1,
                        "user_cancelled_bookings": user_cancelled_bookings,
                        "user_recent_bookings_30d": recent_bookings,
                        "loyalty_level": loyalty_level,
                        "loyalty_points": loyalty_points,
                        "date_reservation": reservation_date.isoformat(timespec="seconds"),
                        "seance_date_heure": seance_date.isoformat(timespec="seconds"),
                        "days_until_seance": days_until_seance,
                        "booking_lead_hours": booking_lead_hours,
                        "seance_hour": seance_hour,
                        "seance_weekday": seance_weekday,
                        "is_weekend": is_weekend,
                        "prix_base": prix_base,
                        "langue": language,
                        "film_genre": genre,
                        "seat_number": seat_number,
                        "is_premium_seat": is_premium_seat,
                        "statut": status,
                        "cancellation_target": cancellation_target,
                    }
                )
                reservation_id += 1
                rows_written += 1

    return rows_written
