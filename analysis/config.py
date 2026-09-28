"""
Shared configuration for the FFEWS re-run pipeline.

Basin: Koshi Basin, Nepal. Station coordinates below are CONFIRMED from DHM's
official "List of Published Stations" (Department of Hydrology and
Meteorology, Government of Nepal, updated July 2025), a copy of which is
saved at data/raw/dhm_published_station_list_2025.pdf
(source: https://dhm.gov.np/assets/vendors/fileman/Uploads/Updated_Published_Station_List_2023%20.pdf):

  - Station 681: Sunkoshi River at Hampachuwar -- 26 55'39.86"N, 87 08'35.05"E,
    elev. 116 m, drainage area 18,164.18 km^2, published data 1991-2019.
    This is the exact station the original 2014 thesis refers to as
    "station 681, Hampuachuwa."
  - Station 695: Saptakoshi River at Chatara -- 26 51'19.98"N, 87 08'58.72"E,
    elev. 106 m, drainage area 53,685.53 km^2, published data 1977-2023.
    This is the main Koshi Basin outlet gauge (matches the thesis's
    "Chatara" rainfall-station reference) and has a longer, more recent
    record than station 681 -- prefer this one if obtaining digital
    records from DHM for both is not possible.

Note: "published" in DHM's station table does not mean freely downloadable.
Obtaining either station's digital daily series requires a formal data
request through DHM's "Request Data" portal
(dhm.gov.np/hydrology/hms-Single/207). The pipeline therefore defaults to
GloFAS reanalysis discharge; see 03b_import_gauge_csv.py for the path to
substitute a real gauge record once obtained.
"""

from pathlib import Path

# --- Paths -------------------------------------------------------------
ANALYSIS_DIR = Path(__file__).resolve().parent
DATA_RAW = ANALYSIS_DIR / "data" / "raw"
DATA_PROCESSED = ANALYSIS_DIR / "data" / "processed"
OUTPUTS = ANALYSIS_DIR / "outputs"

for _d in (DATA_RAW, DATA_PROCESSED, OUTPUTS):
    _d.mkdir(parents=True, exist_ok=True)

# --- Area of interest ---------------------------------------------------
# Bounding box (WGS84) covering both confirmed gauge stations (Hampachuwar and
# Chatara) plus a buffer upstream/downstream for a meaningful HAND-based
# inundation demo.
BASIN_BBOX = {
    "south": 26.70,
    "north": 27.00,
    "west": 87.00,
    "east": 87.40,
}

# Confirmed DHM station coordinates (see module docstring). Default to
# Chatara (695) as the primary point for discharge/mapping since it has the
# longer record and is the basin's main outlet gauge; Hampachuwar (681) is
# kept for direct comparability with the original thesis's results.
STATIONS = {
    "681_hampachuwar": {"river": "Sunkoshi", "lat": 26.9277, "lon": 87.1431, "elev_m": 116, "drainage_km2": 18164.18, "data_from": 1991, "data_to": 2019},
    "695_chatara": {"river": "Saptakoshi", "lat": 26.8555, "lon": 87.1496, "elev_m": 106, "drainage_km2": 53685.53, "data_from": 1977, "data_to": 2023},
}
BASIN_POINT = {"lat": STATIONS["695_chatara"]["lat"], "lon": STATIONS["695_chatara"]["lon"]}

# --- Flood frequency analysis --------------------------------------------
# Same return periods (in years) used in the original 2014 thesis, for direct
# comparability of results.
RETURN_PERIODS_YEARS = [1.01, 2, 5, 10, 25, 50, 100, 200]

# --- Rainfall period of interest (GPM IMERG) -----------------------------
# GPM IMERG coverage starts June 2000. Pick a period long enough for a
# meaningful satellite-rainfall comparison; adjust once real discharge period
# of record is finalized (should overlap the discharge record where possible).
IMERG_START_DATE = "2001-01-01"
IMERG_END_DATE = "2025-12-31"

# --- Manning's-equation synthetic rating curve (HAND flood mapping) -------
# Used only if no surveyed cross-section is available (this replaces the
# original system's HEC-RAS surveyed cross-sections). Hydraulic-geometry
# power-law coefficients (width = a * Q^b, depth = c * Q^d) are generic
# placeholders following Leopold & Maddock (1953)-style regional hydraulic
# geometry relations -- TODO(verify): replace with Nepal/Himalayan-specific
# coefficients if a regional regression is found in the literature (e.g. the
# West Rapti Basin paper or a DHM regional study) rather than relying on the
# generic literature defaults below.
HYDRAULIC_GEOMETRY = {
    "width_a": 5.0, "width_b": 0.4,   # channel top width (m) vs discharge (m3/s)
    "depth_c": 0.3, "depth_d": 0.3,   # mean channel depth (m) vs discharge (m3/s)
    "mannings_n": 0.035,              # natural channel, moderate roughness
}
