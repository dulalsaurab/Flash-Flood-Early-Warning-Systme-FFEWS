"""
Event-timing check: does the GloFAS-derived discharge series (the one the
FFA/HAND re-run is built on) show an anomalous spike on the same days that
BIPAD's independent gauge-stage record shows extreme/record water levels on
this basin?

This is deliberately narrow. BIPAD gives stage (m), not discharge (m3/s), and
there is no rating curve to convert one into the other, so this cannot check
discharge *magnitude* -- only whether GloFAS flags the right *day* as extreme.
That is a different, complementary kind of check to the single-year magnitude
comparison against Devkota et al. (2012) in 04_flood_frequency_analysis.py's
companion paper section.

Also reports each event's implied return period under the same Gumbel and
Log-Pearson III fits used in 04_flood_frequency_analysis.py (inverting
discharge -> T). This surfaces a real mismatch worth stating plainly: both
events rank in the top 2 discharge days of their own year, but against the
full 47-year climatology neither is a rare event by the FFA fit (T on the
order of 1.5-4.5 years) -- despite both gauges independently crossing their
danger level, in one case (Chatara, 2024-09-28) by 69%. See paper Section 5.1
for the reading of this mismatch (daily/sub-daily timescale mismatch, possible
channel/rating-curve drift, or simply an unremarkable year at the annual-max
level).

Input:
    data/processed/bipad_stage_daily.csv   -- from 09_fetch_bipad_stage.py
    data/processed/discharge_daily.csv     -- GloFAS, Chatara point

Output:
    outputs/bipad_glofas_event_check.csv
    outputs/bipad_glofas_comparison_plot.png

Usage:
    python 10_bipad_glofas_event_check.py
"""

import sys
from pathlib import Path

import matplotlib.dates as mdates
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import stats
from scipy.optimize import brentq

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import DATA_PROCESSED, OUTPUTS  # noqa: E402

STAGE_PATH = DATA_PROCESSED / "bipad_stage_daily.csv"
DISCHARGE_PATH = DATA_PROCESSED / "discharge_daily.csv"

# Events identified by inspecting each station's top daily-max stage days
# against its dangerLevel/warningLevel (see paper Section 5.1). On both dates,
# BOTH gauges independently crossed their danger level on the same day.
EVENTS = [
    {"date": "2024-09-28", "station": "hampachuwar_31", "dhm_station": "681", "label": "Hampachuwar"},
    {"date": "2024-09-28", "station": "chatara_58", "dhm_station": "695", "label": "Chatara"},
    {"date": "2025-10-05", "station": "hampachuwar_31", "dhm_station": "681", "label": "Hampachuwar"},
    {"date": "2025-10-05", "station": "chatara_58", "dhm_station": "695", "label": "Chatara"},
]


def fit_gumbel_lp3(discharge: pd.DataFrame):
    """Fit Gumbel and Log-Pearson III to the annual-maxima series, same
    formulation as 04_flood_frequency_analysis.py, and return the fitted
    parameters needed to invert discharge -> implied return period.
    """
    df = discharge.copy()
    df["year"] = df["date"].dt.year
    am = df.groupby("year")["discharge_m3s"].max().dropna()
    xbar, sigma = am.mean(), am.std(ddof=1)

    logq = np.log10(am.values)
    mean_logq, std_logq = logq.mean(), logq.std(ddof=1)
    n = len(logq)
    cs = (n * np.sum((logq - mean_logq) ** 3)) / ((n - 1) * (n - 2) * std_logq ** 3)
    return {"xbar": xbar, "sigma": sigma, "mean_logq": mean_logq, "std_logq": std_logq, "cs": cs}


def gumbel_return_period_for_discharge(x: float, params: dict) -> float:
    """Invert x_T = xbar + K*sigma, K = (Y_T - 0.5772)/1.2825,
    Y_T = -ln(ln(T/(T-1)))  =>  T/(T-1) = exp(exp(-Y_T)).
    """
    k = (x - params["xbar"]) / params["sigma"]
    y_t = k * 1.2825 + 0.5772
    ratio = np.exp(np.exp(-y_t))
    return ratio / (ratio - 1)


def lp3_return_period_for_discharge(x: float, params: dict) -> float:
    """Invert the Wilson-Hilferty Log-Pearson III frequency-factor formula
    numerically (same formulation as 04_flood_frequency_analysis.py).
    """
    cs, mean_logq, std_logq = params["cs"], params["mean_logq"], params["std_logq"]

    def f(t):
        p_exceed = 1.0 / t
        z = stats.norm.ppf(1 - p_exceed)
        if abs(cs) < 1e-8:
            k_t = z
        else:
            k_t = (2 / cs) * ((1 + cs * z / 6 - cs ** 2 / 36) ** 3 - 1)
        return (mean_logq + k_t * std_logq) - np.log10(x)

    return brentq(f, 1.001, 1000)


def discharge_percentile_rank(discharge: pd.DataFrame, date: pd.Timestamp) -> tuple[float, int, int]:
    """Rank of `date`'s discharge within its calendar year (1 = highest)."""
    year = date.year
    year_df = discharge[discharge["date"].dt.year == year].sort_values("discharge_m3s", ascending=False).reset_index(drop=True)
    value = discharge.loc[discharge["date"] == date, "discharge_m3s"].iloc[0]
    rank = int(year_df[year_df["discharge_m3s"] == value].index[0]) + 1
    n = len(year_df)
    percentile = 100 * (1 - (rank - 1) / n)
    return percentile, rank, n


def main():
    if not STAGE_PATH.exists():
        raise SystemExit(f"{STAGE_PATH} not found -- run 09_fetch_bipad_stage.py first.")
    if not DISCHARGE_PATH.exists():
        raise SystemExit(f"{DISCHARGE_PATH} not found -- run the GloFAS discharge extraction first.")

    stage = pd.read_csv(STAGE_PATH, parse_dates=["date"])
    discharge = pd.read_csv(DISCHARGE_PATH, parse_dates=["date"])
    ffa_params = fit_gumbel_lp3(discharge)

    rows = []
    for event in EVENTS:
        date = pd.Timestamp(event["date"])
        stage_row = stage[(stage["station"] == event["station"]) & (stage["date"] == date)]
        if stage_row.empty:
            print(f"WARNING: no stage reading for {event['station']} on {date.date()} -- skipping.")
            continue
        stage_value = float(stage_row["water_level_max_m"].iloc[0])
        danger_level = stage_row["danger_level_m"].iloc[0]

        prior = stage[
            (stage["station"] == event["station"]) & (stage["date"] < date) & (stage["water_level_max_m"].notna())
        ]
        prior_max = float(prior["water_level_max_m"].max()) if not prior.empty else None

        if date not in discharge["date"].values:
            print(f"WARNING: no GloFAS discharge for {date.date()} -- skipping.")
            continue
        pct, rank, n = discharge_percentile_rank(discharge, date)
        discharge_value = float(discharge.loc[discharge["date"] == date, "discharge_m3s"].iloc[0])
        gumbel_T = gumbel_return_period_for_discharge(discharge_value, ffa_params)
        lp3_T = lp3_return_period_for_discharge(discharge_value, ffa_params)

        rows.append(
            {
                "date": date.date(),
                "dhm_station": event["dhm_station"],
                "station_label": event["label"],
                "stage_m": stage_value,
                "danger_level_m": danger_level,
                "prior_record_stage_m": prior_max,
                "glofas_discharge_m3s": discharge_value,
                "rank_in_year": rank,
                "n_days_in_year": n,
                "percentile_in_year": round(pct, 1),
                "gumbel_return_period_yr": round(gumbel_T, 2),
                "lp3_return_period_yr": round(lp3_T, 2),
            }
        )

    result = pd.DataFrame(rows)
    out_path = OUTPUTS / "bipad_glofas_event_check.csv"
    result.to_csv(out_path, index=False)
    print(f"Event check -> {out_path}")
    print(result.to_string(index=False))

    # Plot: GloFAS discharge over the BIPAD coverage window, event dates marked,
    # Hampachuwar stage overlaid on a secondary axis for context. Reindex to a
    # continuous daily range so sensor-outage gaps (up to 257 days) break the
    # line instead of being linearly interpolated across.
    window = discharge[(discharge["date"] >= "2020-10-01") & (discharge["date"] <= stage["date"].max())]
    hampachuwar = stage[stage["station"] == "hampachuwar_31"].set_index("date")
    full_range = pd.date_range(hampachuwar.index.min(), hampachuwar.index.max(), freq="D")
    hampachuwar = hampachuwar.reindex(full_range).reset_index().rename(columns={"index": "date"})

    fig, ax1 = plt.subplots(figsize=(11, 5))
    ax1.plot(window["date"], window["discharge_m3s"], color="tab:orange", lw=0.8, label="GloFAS discharge (Chatara)")
    ax1.set_ylabel("Discharge (m$^3$/s)", color="tab:orange")
    ax1.tick_params(axis="y", labelcolor="tab:orange")

    ax2 = ax1.twinx()
    ax2.plot(hampachuwar["date"], hampachuwar["water_level_max_m"], color="tab:blue", lw=0.6, alpha=0.7, label="Hampachuwar stage (BIPAD)")
    ax2.set_ylabel("Stage (m)", color="tab:blue")
    ax2.tick_params(axis="y", labelcolor="tab:blue")

    for event_date in sorted(set(pd.Timestamp(e["date"]) for e in EVENTS)):
        ax1.axvline(event_date, color="red", linestyle="--", lw=1, alpha=0.7)

    ax1.xaxis.set_major_formatter(mdates.DateFormatter("%Y-%m"))
    ax1.set_title("GloFAS discharge vs. BIPAD gauge stage, with flagged extreme-stage events")
    fig.autofmt_xdate()
    fig.tight_layout()
    plot_path = OUTPUTS / "bipad_glofas_comparison_plot.png"
    fig.savefig(plot_path, dpi=150)
    print(f"\nComparison plot -> {plot_path}")


if __name__ == "__main__":
    main()
