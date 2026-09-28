"""
Preferred discharge source: a REAL observed gauge record (GRDC, ICIMOD RDS,
or DHM), if one is found for a Koshi Basin (or other Nepal) station.

This script does not fetch anything automatically -- GRDC/ICIMOD access is
manual (portal browsing / data request), so drop the raw file you obtain into
data/raw/ and point RAW_FILE at it below. This script's only job is to
standardize whatever format that source uses into the same schema the rest
of the pipeline expects: data/processed/discharge_daily.csv with columns
`date` (YYYY-MM-DD) and `discharge_m3s` (float).

Edit RAW_FILE and the parsing block below once you know the real file's
columns -- formats vary a lot between GRDC (semicolon-delimited, metadata
header block) and ICIMOD/DHM exports (usually plain CSV/Excel).

Usage:
    python 03b_import_gauge_csv.py
"""

import sys
from pathlib import Path

import pandas as pd

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import DATA_RAW, DATA_PROCESSED  # noqa: E402

# TODO: point this at the real file once obtained from GRDC / ICIMOD RDS / DHM.
RAW_FILE = DATA_RAW / "gauge_station_raw.csv"
OUT_PATH = DATA_PROCESSED / "discharge_daily.csv"

# TODO: set these to match the real file's column names once known.
DATE_COL = "date"
DISCHARGE_COL = "discharge"


def main():
    if not RAW_FILE.exists():
        raise SystemExit(
            f"{RAW_FILE} not found. Save the real gauge export there first, "
            "then adjust DATE_COL/DISCHARGE_COL (and the read_csv call below, "
            "e.g. GRDC files often need sep=';' and skiprows=N for the "
            "metadata header) to match its actual format."
        )

    df = pd.read_csv(RAW_FILE)
    df = df.rename(columns={DATE_COL: "date", DISCHARGE_COL: "discharge_m3s"})
    df["date"] = pd.to_datetime(df["date"])
    df = df[["date", "discharge_m3s"]].dropna().sort_values("date")

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    df.to_csv(OUT_PATH, index=False)
    print(f"Standardized {len(df)} daily discharge records -> {OUT_PATH}")
    print(f"Period of record: {df['date'].min().date()} to {df['date'].max().date()}")


if __name__ == "__main__":
    main()
