"""
Cross-validation experiment: does basin-averaged GPM IMERG rainfall behave
consistently with the GloFAS discharge series over their overlapping period?
This uses two independently-sourced open datasets (NASA satellite rainfall,
ECMWF/Copernicus discharge reanalysis) that were downloaded for this study
but, until now, never checked against each other -- a cheap, real comparison
that strengthens confidence in both.

Method: for each IMERG monthly file, average precipitation (mm/hr) over the
basin bounding box, convert to a monthly total (mm) using hours-in-month, and
correlate the resulting monthly rainfall series against monthly-mean
discharge from the (GloFAS-derived) daily discharge series, both over their
period of overlap. Reports Pearson and Spearman correlation at lag 0, 1, and
2 months (discharge in a large basin lags rainfall by days-to-weeks; at
monthly resolution a 1-month lag is the finest lag this data can resolve).

Input:
    data/raw/imerg/*.HDF5
    data/processed/discharge_daily.csv

Output:
    outputs/rainfall_discharge_monthly.csv
    outputs/rainfall_discharge_comparison_plot.png
    outputs/rainfall_discharge_correlation.csv

Usage:
    python 06_rainfall_discharge_comparison.py
"""

import calendar
import re
import sys
from pathlib import Path

import h5py
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import stats

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_BBOX, DATA_RAW, DATA_PROCESSED, OUTPUTS  # noqa: E402

IMERG_DIR = DATA_RAW / "imerg"
DISCHARGE_PATH = DATA_PROCESSED / "discharge_daily.csv"

FNAME_RE = re.compile(r"3IMERG\.(\d{8})-")


def basin_mean_monthly_rainfall_mm(path: Path) -> float:
    """Basin-mean precipitation rate (mm/hr) -> monthly total (mm)."""
    with h5py.File(path, "r") as f:
        lat = f["Grid/lat"][:]
        lon = f["Grid/lon"][:]
        precip = f["Grid/precipitation"][0]  # dims: (lon, lat)

    lat_mask = (lat >= BASIN_BBOX["south"]) & (lat <= BASIN_BBOX["north"])
    lon_mask = (lon >= BASIN_BBOX["west"]) & (lon <= BASIN_BBOX["east"])
    subset = precip[np.ix_(lon_mask, lat_mask)]
    subset = np.where(subset < -9999, np.nan, subset)  # fill value is -9999.9
    mean_rate_mm_per_hr = np.nanmean(subset)
    return mean_rate_mm_per_hr


def main():
    files = sorted(IMERG_DIR.glob("3B-MO.MS.MRG.3IMERG.*.HDF5"))
    if not files:
        raise SystemExit(f"No IMERG files found in {IMERG_DIR} -- run 02_download_rainfall.py first.")
    if not DISCHARGE_PATH.exists():
        raise SystemExit(f"{DISCHARGE_PATH} not found -- run the discharge extraction script first.")

    print(f"Processing {len(files)} IMERG monthly files ...")
    records = []
    for path in files:
        m = FNAME_RE.search(path.name)
        if not m:
            continue
        date_str = m.group(1)
        year, month = int(date_str[:4]), int(date_str[4:6])
        rate = basin_mean_monthly_rainfall_mm(path)
        hours_in_month = calendar.monthrange(year, month)[1] * 24
        monthly_total_mm = rate * hours_in_month
        records.append({"year": year, "month": month, "rainfall_mm": monthly_total_mm})

    rainfall = pd.DataFrame(records).sort_values(["year", "month"]).reset_index(drop=True)

    discharge = pd.read_csv(DISCHARGE_PATH, parse_dates=["date"])
    discharge["year"] = discharge["date"].dt.year
    discharge["month"] = discharge["date"].dt.month
    discharge_monthly = discharge.groupby(["year", "month"])["discharge_m3s"].mean().reset_index()
    discharge_monthly = discharge_monthly.rename(columns={"discharge_m3s": "discharge_mean_m3s"})

    merged = rainfall.merge(discharge_monthly, on=["year", "month"], how="inner")
    merged = merged.sort_values(["year", "month"]).reset_index(drop=True)

    out_path = OUTPUTS / "rainfall_discharge_monthly.csv"
    merged.to_csv(out_path, index=False)
    print(f"Merged monthly series ({len(merged)} months of overlap) -> {out_path}")

    if len(merged) < 12:
        print("WARNING: fewer than 12 overlapping months -- correlation results will be weak/unreliable.")

    corr_rows = []
    for lag in (0, 1, 2):
        # Discharge lagged behind rainfall by `lag` months: rainfall[t] vs discharge[t+lag].
        r = merged["rainfall_mm"].iloc[: len(merged) - lag if lag else len(merged)]
        d = merged["discharge_mean_m3s"].shift(-lag).dropna()
        n = min(len(r), len(d))
        r, d = r.iloc[:n], d.iloc[:n]
        if n < 3:
            continue
        pearson_r, pearson_p = stats.pearsonr(r, d)
        spearman_r, spearman_p = stats.spearmanr(r, d)
        corr_rows.append(
            {
                "lag_months": lag,
                "n_months": n,
                "pearson_r": pearson_r,
                "pearson_p": pearson_p,
                "spearman_r": spearman_r,
                "spearman_p": spearman_p,
            }
        )
    corr_df = pd.DataFrame(corr_rows)
    corr_path = OUTPUTS / "rainfall_discharge_correlation.csv"
    corr_df.to_csv(corr_path, index=False)
    print(f"\nRainfall-discharge correlation by lag -> {corr_path}")
    print(corr_df.to_string(index=False))

    fig, axes = plt.subplots(2, 1, figsize=(10, 7), sharex=True)
    t = pd.to_datetime(merged["year"].astype(str) + "-" + merged["month"].astype(str) + "-01")
    axes[0].bar(t, merged["rainfall_mm"], width=20, color="tab:blue", label="Basin-mean rainfall (IMERG)")
    axes[0].set_ylabel("Rainfall (mm/month)")
    axes[0].legend(loc="upper right")
    axes[1].plot(t, merged["discharge_mean_m3s"], color="tab:orange", label="Monthly-mean discharge (GloFAS)")
    axes[1].set_ylabel("Discharge (m$^3$/s)")
    axes[1].set_xlabel("Date")
    axes[1].legend(loc="upper right")
    fig.suptitle("Basin-mean rainfall vs. monthly-mean discharge, Koshi Basin")
    fig.tight_layout()
    plot_path = OUTPUTS / "rainfall_discharge_comparison_plot.png"
    fig.savefig(plot_path, dpi=150)
    print(f"\nComparison plot -> {plot_path}")


if __name__ == "__main__":
    main()
