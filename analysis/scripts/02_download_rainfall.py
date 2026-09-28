"""
Download GPM IMERG (Final Run, monthly) precipitation for the Koshi Basin AOI
using NASA's `earthaccess` package. Free NASA Earthdata login required (see
../.env.example).

This replaces the original 2014 system's empirical geostationary-IR cloud-top
rainfall retrieval with the modern GPM-era satellite rainfall product.

Usage:
    python 02_download_rainfall.py

Output:
    data/raw/imerg/*.nc4  (one granule per month in IMERG_START_DATE..IMERG_END_DATE)
"""

import os
import sys
from pathlib import Path

import earthaccess
from dotenv import load_dotenv

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_BBOX, DATA_RAW, IMERG_START_DATE, IMERG_END_DATE  # noqa: E402

load_dotenv(Path(__file__).resolve().parent.parent / ".env")

OUT_DIR = DATA_RAW / "imerg"
OUT_DIR.mkdir(parents=True, exist_ok=True)

# GPM_3IMERGM = IMERG Final Precipitation L3 Monthly, v07 (mm/hr, ~0.1 deg)
SHORT_NAME = "GPM_3IMERGM"


def main():
    username = os.environ.get("EARTHDATA_USERNAME")
    password = os.environ.get("EARTHDATA_PASSWORD")
    if not username or not password:
        raise SystemExit(
            "Missing EARTHDATA_USERNAME/EARTHDATA_PASSWORD. Copy "
            "analysis/.env.example to analysis/.env and fill it in (free "
            "account at urs.earthdata.nasa.gov)."
        )

    # earthaccess reads EARTHDATA_USERNAME/EARTHDATA_PASSWORD from the
    # environment automatically when strategy="environment" is used.
    auth = earthaccess.login(strategy="environment")
    if not auth.authenticated:
        raise SystemExit("NASA Earthdata authentication failed -- check credentials.")

    bbox = (
        BASIN_BBOX["west"],
        BASIN_BBOX["south"],
        BASIN_BBOX["east"],
        BASIN_BBOX["north"],
    )

    print(f"Searching {SHORT_NAME} granules for {IMERG_START_DATE}..{IMERG_END_DATE} over {bbox} ...")
    results = earthaccess.search_data(
        short_name=SHORT_NAME,
        bounding_box=bbox,
        temporal=(IMERG_START_DATE, IMERG_END_DATE),
    )
    print(f"Found {len(results)} granules.")
    if not results:
        print("No granules found -- check date range/short_name/product version.")
        return

    earthaccess.download(results, str(OUT_DIR))
    print(f"Downloaded IMERG granules to {OUT_DIR}")


if __name__ == "__main__":
    main()
