"""
Download a Copernicus GLO-30 DEM for the Koshi Basin AOI via the OpenTopography
Global DEM API. Free API key required (see ../.env.example).

Usage:
    python 01_download_dem.py

Output:
    data/raw/dem_koshi_glo30.tif
"""

import os
import sys
from pathlib import Path

import requests
from dotenv import load_dotenv

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_BBOX, DATA_RAW  # noqa: E402

load_dotenv(Path(__file__).resolve().parent.parent / ".env")

API_URL = "https://portal.opentopography.org/API/globaldem"
OUT_PATH = DATA_RAW / "dem_koshi_glo30.tif"


def main():
    api_key = os.environ.get("OPENTOPOGRAPHY_API_KEY")
    if not api_key:
        raise SystemExit(
            "Missing OPENTOPOGRAPHY_API_KEY. Copy analysis/.env.example to "
            "analysis/.env and fill it in (free key from opentopography.org)."
        )

    params = {
        "demtype": "COP30",
        "south": BASIN_BBOX["south"],
        "north": BASIN_BBOX["north"],
        "west": BASIN_BBOX["west"],
        "east": BASIN_BBOX["east"],
        "outputFormat": "GTiff",
        "API_Key": api_key,
    }

    print(f"Requesting Copernicus GLO-30 DEM for bbox {BASIN_BBOX} ...")
    resp = requests.get(API_URL, params=params, timeout=120)
    resp.raise_for_status()

    OUT_PATH.write_bytes(resp.content)
    print(f"Saved DEM to {OUT_PATH} ({OUT_PATH.stat().st_size / 1e6:.1f} MB)")


if __name__ == "__main__":
    main()
