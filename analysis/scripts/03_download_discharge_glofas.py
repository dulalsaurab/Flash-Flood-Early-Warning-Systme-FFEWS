"""
Fallback discharge source: GloFAS (Global Flood Awareness System) reanalysis
river discharge, via the `cdsapi` client. NOTE: GloFAS is hosted on a
SEPARATE Copernicus portal from the main Climate Data Store -- the CEMS
Early Warning Data Store (EWDS, ewds.climate.copernicus.eu), not
cds.climate.copernicus.eu. Free account + API key required (see
../.env.example), and you must accept the "CEMS-GloFAS historical" dataset's
license on the EWDS website once before API calls will work.

Use this ONLY if a real observed gauge record (GRDC / ICIMOD RDS / DHM) could
not be obtained -- GloFAS is a model reanalysis, not an observed record, and
that distinction must be stated plainly in the paper if this is the source
used (see analysis/README.md and paper/draft.md Section IV/VI).

Schema CONFIRMED against the dataset's own process-description endpoint
(GET {CDS_URL}/retrieve/v1/processes/cems-glofas-historical) on 2026-09-11 --
NOT from the EWDS web form's "Show API request" panel, which turned out to
be a stale/generic example (it used `hyear`/`hmonth`/`hday`, `hydrological_model:
htessel_lisflood`, and `variable: mean_discharge_in_the_last_24_hours`, all of
which the API rejects with a 400 "invalid combination of values" -- none of
those are in the real enum). Confirmed-valid values as of this writing:
  - hydrological_model: only "lisflood" (not "htessel_lisflood")
  - variable: "average_river_discharge_in_the_last_24_hours" (not
    "mean_discharge_in_the_last_24_hours" or "river_discharge_in_the_last_24_hours")
  - date fields are `year`/`month`/`day` (not `hyear`/`hmonth`/`hday`)
  - timespan (REQUIRED, wasn't in any older tutorial): "time_mean" for the
    discharge variable (it's a 24h average); "instantaneous" is for snapshot
    state variables like snow depth instead
  - data_format: "netcdf" or "grib2" (not "netcdf4")
  - download_format: "unarchived" or "zip"
  - system_version: "version_4_0" (Operational) or "version_5_0" (Pre-operational)
  - product_type: "consolidated" or "intermediate"
If the API rejects a request again in the future (GloFAS's schema has
already changed at least once, per the dataset's own 30 Jul 2026 notice),
re-derive the schema from the process-description endpoint directly rather
than trusting the web form's generated code snippet -- see the debug
one-liner in analysis/README.md.

The EWDS API also enforces a per-request "cost" limit (observed: ~4 units
per requested day for this small a bounding box; the default limit is 500,
i.e. roughly 3-4 months per request). This script chunks the request by a
configurable number of months and retrieves+concatenates automatically.

Usage:
    python 03_download_discharge_glofas.py

Output:
    data/raw/glofas/glofas_<start>_<end>.nc  (one file per chunk)
"""

import os
import sys

# Fixes a common macOS/Homebrew-Python issue where the object-store download
# step fails with SSL_CERT_VERIFY_FAILED even though the API request itself
# succeeds. MUST happen before `requests` (pulled in transitively by cdsapi)
# is imported anywhere -- requests/urllib3 resolve their default CA bundle
# path at import time, so setting this after importing cdsapi is a no-op.
import certifi  # noqa: E402

os.environ.setdefault("SSL_CERT_FILE", certifi.where())
os.environ.setdefault("REQUESTS_CA_BUNDLE", certifi.where())
os.environ.setdefault("CURL_CA_BUNDLE", certifi.where())

from pathlib import Path  # noqa: E402

import cdsapi  # noqa: E402
from dotenv import load_dotenv  # noqa: E402

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_BBOX, DATA_RAW  # noqa: E402

OUT_DIR = DATA_RAW / "glofas"
OUT_DIR.mkdir(parents=True, exist_ok=True)

# Full period to pull. GloFAS historical covers 1979-near-real-time.
# Extended to the full available record for a more statistically robust FFA
# (11 years of annual maxima is thin for return-period estimation).
START_YEAR = 1979
END_YEAR = 2025

# Months per request chunk -- ~3 months stayed safely under the observed
# ~500-unit cost limit for a full year; reduce if a chunk is still rejected
# as "too large" (error message states the exact cost vs. limit).
MONTHS_PER_CHUNK = 3

DATA_FORMAT = "netcdf"      # confirmed valid; "grib2" is the alternative
SYSTEM_VERSION = "version_4_0"  # "Operational"; "version_5_0" is "Pre-operational"


def month_chunks(start_year: int, end_year: int, chunk_size: int):
    months = [(y, m) for y in range(start_year, end_year + 1) for m in range(1, 13)]
    for i in range(0, len(months), chunk_size):
        yield months[i : i + chunk_size]


def main():
    load_dotenv(Path(__file__).resolve().parent.parent / ".env")
    url = os.environ.get("CDS_URL")
    key = os.environ.get("CDS_KEY")
    if not url or not key:
        raise SystemExit(
            "Missing CDS_URL/CDS_KEY in analysis/.env -- see .env.example "
            "(NOTE: must be the EWDS endpoint/key, not the main CDS one, "
            "for this dataset)."
        )
    client = cdsapi.Client(url=url, key=key)

    # area = [north, west, south, east], matching the dataset's own default
    # ([90, -180, -90, 180]) ordering.
    area = [BASIN_BBOX["north"], BASIN_BBOX["west"], BASIN_BBOX["south"], BASIN_BBOX["east"]]

    for chunk in month_chunks(START_YEAR, END_YEAR, MONTHS_PER_CHUNK):
        years = sorted({str(y) for y, _ in chunk})
        months = sorted({f"{m:02d}" for _, m in chunk})
        label = f"{chunk[0][0]}{chunk[0][1]:02d}_{chunk[-1][0]}{chunk[-1][1]:02d}"
        out_path = OUT_DIR / f"glofas_{label}.nc"
        if out_path.exists() and out_path.stat().st_size > 0:
            print(f"Skipping {out_path} (already downloaded)")
            continue
        elif out_path.exists():
            print(f"Removing empty/failed leftover {out_path} and re-requesting")
            out_path.unlink()

        request = {
            "system_version": [SYSTEM_VERSION],
            "hydrological_model": ["lisflood"],
            "product_type": ["consolidated"],
            "timespan": ["time_mean"],
            "variable": ["average_river_discharge_in_the_last_24_hours"],
            "year": years,
            "month": months,
            "day": [f"{d:02d}" for d in range(1, 32)],
            "data_format": DATA_FORMAT,
            "download_format": "unarchived",
            "area": area,
        }
        print(f"Requesting GloFAS discharge for {label} (years={years}, months={months}) ...")
        try:
            client.retrieve("cems-glofas-historical", request, str(out_path))
        except Exception as exc:  # noqa: BLE001
            print(
                f"FAILED for {label}: {exc}\n"
                "If this is a 400 'invalid combination of values' error, "
                "re-check the schema via the process-description endpoint "
                "(see docstring) -- it may have changed again."
            )
            raise
        print(f"Saved {out_path}")

    print(f"\nAll chunks downloaded to {OUT_DIR}. Run 03c_extract_glofas_point.py next.")


if __name__ == "__main__":
    main()
