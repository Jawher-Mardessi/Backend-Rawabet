from __future__ import annotations

from pathlib import Path

from app.cancellation_dataset import DatasetConfig, build_dataset
from app.cancellation_model import train_model_from_csv


SCRIPT_DIR = Path(__file__).resolve().parent
SERVICE_DIR = SCRIPT_DIR.parent

TARGET_DATA_DIR = SERVICE_DIR / "data"
TARGET_MODEL_DIR = SERVICE_DIR / "models"

TARGET_RECOMMENDATION_DATASET = TARGET_DATA_DIR / "recommendation_dataset.csv"
TARGET_CANCELLATION_DATASET = TARGET_DATA_DIR / "cancellation_training_dataset.csv"
TARGET_CANCELLATION_MODEL = TARGET_MODEL_DIR / "cancellation_model.json"


def main() -> None:
    TARGET_DATA_DIR.mkdir(parents=True, exist_ok=True)
    TARGET_MODEL_DIR.mkdir(parents=True, exist_ok=True)

    if not TARGET_CANCELLATION_DATASET.exists():
        build_dataset(DatasetConfig(output_path=TARGET_CANCELLATION_DATASET))

    if not TARGET_RECOMMENDATION_DATASET.exists():
        raise FileNotFoundError(
            "Recommendation dataset not found inside Backend-Rawabet. "
            f"Expected file at {TARGET_RECOMMENDATION_DATASET}"
        )

    train_model_from_csv(TARGET_CANCELLATION_DATASET, TARGET_CANCELLATION_MODEL)
    print("Assets prepared inside Backend-Rawabet/python-service")


if __name__ == "__main__":
    main()
