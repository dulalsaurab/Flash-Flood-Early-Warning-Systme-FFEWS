"""
Extract a single-point daily discharge time series from the downloaded GloFAS
NetCDF chunks, and standardize it into the same schema the FFA script
expects: data/processed/discharge_daily.csv with columns `date` and
`discharge_m3s`.

IMPORTANT: plain nearest-neighbor lat/lon lookup is WRONG for river
discharge grids. GloFAS is ~0.05 degree (~5 km) resolution and a river
channel is often only 1 pixel wide -- the literal nearest grid cell to a
gauge's lat/lon can land on an adjacent hillslope/tributary cell instead of
the main channel, giving discharge 100-1000x too small (this happened on
the first extraction attempt here: ~1-24 m3/s instead of the expected
thousands of m3/s for the Saptakoshi at Chatara, a 53,686 km2 basin). This
script instead searches a small window around BASIN_POINT and picks the
cell with the highest *mean* discharge over the whole record -- the main
channel pixel is distinctly higher than surrounding land cells, so this is
a robust (if crude) way to find it without a separate flow-accumulation/
upstream-area layer.

Only needed if using the GloFAS fallback (03_download_discharge_glofas.py,
which saves one file per date-range chunk under data/raw/glofas/). If a real
gauge CSV was obtained instead, use 03b_import_gauge_csv.py.

Usage:
    python 03c_extract_glofas_point.py
"""

import sys
from pathlib import Path

import pandas as pd
import xarray as xr

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_POINT, DATA_RAW, DATA_PROCESSED  # noqa: E402

IN_DIR = DATA_RAW / "glofas"
OUT_PATH = DATA_PROCESSED / "discharge_daily.csv"

# Half-width of the search window in degrees. GloFAS here is ~0.05 deg
# resolution, so 0.15 deg covers a ~7x7 pixel neighborhood around
# BASIN_POINT -- wide enough to catch the main channel even if it's a
# couple of pixels off from the gauge's literal coordinates.
SEARCH_WINDOW_DEG = 0.15


def main():
    files = sorted(IN_DIR.glob("glofas_*.nc"))
    if not files:
        raise SystemExit(
            f"No files found in {IN_DIR} -- run 03_download_discharge_glofas.py first "
            "(if you downloaded GRIB instead of NetCDF, adjust the glob pattern above "
            "to glofas_*.grib and `pip install cfgrib` first)."
        )

    print(f"Opening {len(files)} GloFAS chunk file(s) from {IN_DIR} ...")
    ds = xr.open_mfdataset(files, combine="by_coords")

    lat_name = "latitude" if "latitude" in ds.coords else "lat"
    lon_name = "longitude" if "longitude" in ds.coords else "lon"
    var_name = "avg_dis" if "avg_dis" in ds.data_vars else (
        "dis24" if "dis24" in ds.data_vars else list(ds.data_vars)[0]
    )

    window = ds.sel(
        {
            lat_name: slice(BASIN_POINT["lat"] + SEARCH_WINDOW_DEG, BASIN_POINT["lat"] - SEARCH_WINDOW_DEG),
            lon_name: slice(BASIN_POINT["lon"] - SEARCH_WINDOW_DEG, BASIN_POINT["lon"] + SEARCH_WINDOW_DEG),
        }
    )
    mean_discharge = window[var_name].mean(dim=[d for d in window[var_name].dims if d not in (lat_name, lon_name)])
    mean_discharge = mean_discharge.compute()
    argmax = mean_discharge.argmax(dim=[lat_name, lon_name])
    best_lat = float(mean_discharge[lat_name][argmax[lat_name]])
    best_lon = float(mean_discharge[lon_name][argmax[lon_name]])
    print(
        f"Selected channel cell at ({best_lat:.3f}, {best_lon:.3f}) -- "
        f"mean discharge {float(mean_discharge.max()):.1f} m3/s -- "
        f"vs. literal-nearest cell would have been ({BASIN_POINT['lat']:.3f}, {BASIN_POINT['lon']:.3f})"
    )

    point = ds.sel({lat_name: best_lat, lon_name: best_lon}, method="nearest")
    series = point[var_name].to_series()

    df = series.reset_index()
    df.columns = ["date", "discharge_m3s"]
    df = df.drop_duplicates(subset="date").sort_values("date")

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    df.to_csv(OUT_PATH, index=False)
    print(f"Extracted {len(df)} daily values -> {OUT_PATH}")
    print(f"Period of record: {df['date'].min()} to {df['date'].max()}")
    print(f"Discharge range: {df['discharge_m3s'].min():.1f} to {df['discharge_m3s'].max():.1f} m3/s")
    print(
        "NOTE: this is GloFAS model reanalysis discharge, not an observed gauge "
        "record -- state this plainly in the paper's Data section."
    )


if __name__ == "__main__":
    main()
