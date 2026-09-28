"""
Flood Frequency Analysis (FFA): Gumbel's method, Log-Pearson Type III, and
the Generalized Extreme Value (GEV) distribution, with:
  - a closed-form Wilson-Hilferty approximation for the Log-Pearson III
    frequency factor (avoiding a manual frequency-factor table lookup),
    following USGS Bulletin 17B practice,
  - GEV fit by maximum likelihood (scipy) as a three-parameter comparison
    method -- Gumbel is GEV's special case with zero shape parameter, so
    this also serves as a check on whether the extra shape parameter is
    actually warranted by the data, and
  - goodness-of-fit statistics (Kolmogorov-Smirnov test + RMSE against the
    empirical plotting-position discharge) for all three methods.

Input:
    data/processed/discharge_daily.csv  (columns: date, discharge_m3s)
    -- produced by either 03b_import_gauge_csv.py (real gauge, preferred)
    or 03c_extract_glofas_point.py (GloFAS reanalysis fallback).

Output:
    outputs/ffa_return_period_table.csv
    outputs/ffa_comparison_plot.png
    outputs/ffa_goodness_of_fit.csv

Usage:
    python 04_flood_frequency_analysis.py
"""

import sys
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import stats

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import DATA_PROCESSED, OUTPUTS, RETURN_PERIODS_YEARS  # noqa: E402

IN_PATH = DATA_PROCESSED / "discharge_daily.csv"


def annual_maxima(df: pd.DataFrame) -> pd.Series:
    """Annual-maximum-series of daily discharge, one value per calendar year."""
    df = df.copy()
    df["year"] = df["date"].dt.year
    return df.groupby("year")["discharge_m3s"].max().dropna()


def gumbel_return_period_discharge(am: pd.Series, return_periods):
    """Gumbel's method, following the same formulation as the original thesis
    (and standard hydrology practice): x_T = xbar + K * sigma, K = (y_T - 0.5772) / 1.2825.
    """
    xbar = am.mean()
    sigma = am.std(ddof=1)
    results = {}
    for T in return_periods:
        y_t = -np.log(np.log(T / (T - 1))) if T > 1 else np.nan
        K = (y_t - 0.5772) / 1.2825
        results[T] = xbar + K * sigma
    return results


def log_pearson3_return_period_discharge(am: pd.Series, return_periods):
    """Log-Pearson Type III, using the Wilson-Hilferty approximation for the
    frequency factor K_T (avoids the original's manual K-table lookup):
        z = standard normal variate for exceedance probability 1/T
        K_T = (2/Cs) * ([1 + Cs*z/6 - Cs^2/36]^3 - 1)   (Cs != 0; else K_T = z)
    """
    logq = np.log10(am.values)
    mean_logq = logq.mean()
    std_logq = logq.std(ddof=1)
    n = len(logq)
    cs = (n * np.sum((logq - mean_logq) ** 3)) / ((n - 1) * (n - 2) * std_logq ** 3)

    results = {}
    for T in return_periods:
        p_exceed = 1.0 / T
        z = stats.norm.ppf(1 - p_exceed)
        if abs(cs) < 1e-8:
            k_t = z
        else:
            k_t = (2 / cs) * ((1 + cs * z / 6 - cs ** 2 / 36) ** 3 - 1)
        log_q_t = mean_logq + k_t * std_logq
        results[T] = 10 ** log_q_t
    return results, cs


def gev_return_period_discharge(am: pd.Series, return_periods):
    """Generalized Extreme Value distribution, fit by maximum likelihood.
    Return-period discharge is the (1 - 1/T) quantile of the fitted
    distribution. scipy's genextreme uses a shape convention that is the
    negative of the usual hydrological xi (so a positive scipy `c` means a
    bounded-upper-tail / Weibull-type GEV, negative `c` means a heavy-tailed
    Frechet-type GEV) -- we report the fitted shape as-is and let the reader
    map convention if needed.
    """
    shape, loc, scale = stats.genextreme.fit(am.values)
    results = {}
    for T in return_periods:
        p_exceed = 1.0 / T
        results[T] = stats.genextreme.ppf(1 - p_exceed, shape, loc=loc, scale=scale)
    return results, shape


def goodness_of_fit(am: pd.Series, gumbel_results: dict, lp3_results: dict, gev_results: dict, gev_shape: float):
    """KS test of the annual-maxima sample against the fitted Gumbel and
    Log-Pearson III distributions, plus RMSE of fitted vs. empirical
    (Weibull plotting-position) discharge at matching exceedance
    probabilities -- neither of these was computed in the original thesis.
    """
    xbar, sigma = am.mean(), am.std(ddof=1)
    # Gumbel: loc/scale parameterization for scipy's gumbel_r.
    beta = sigma * np.sqrt(6) / np.pi
    mu = xbar - 0.5772 * beta
    ks_gumbel = stats.kstest(am.values, "gumbel_r", args=(mu, beta))

    logq = np.log10(am.values)
    skew = stats.skew(logq, bias=False)
    ks_lp3 = stats.kstest(
        logq, "pearson3", args=(skew, logq.mean(), logq.std(ddof=1))
    )

    gev_shape_fit, gev_loc, gev_scale = stats.genextreme.fit(am.values)
    ks_gev = stats.kstest(am.values, "genextreme", args=(gev_shape_fit, gev_loc, gev_scale))

    # Empirical plotting position (Weibull) for RMSE check.
    n = len(am)
    ranked = np.sort(am.values)[::-1]
    empirical_T = (n + 1) / np.arange(1, n + 1)

    def interp_fitted(results: dict, T_query):
        Ts = np.array(sorted(results.keys()))
        Qs = np.array([results[T] for T in Ts])
        return np.interp(T_query, Ts, Qs)

    rmse_gumbel = np.sqrt(np.mean((interp_fitted(gumbel_results, empirical_T) - ranked) ** 2))
    rmse_lp3 = np.sqrt(np.mean((interp_fitted(lp3_results, empirical_T) - ranked) ** 2))
    rmse_gev = np.sqrt(np.mean((interp_fitted(gev_results, empirical_T) - ranked) ** 2))

    return pd.DataFrame(
        [
            {"method": "Gumbel", "ks_stat": ks_gumbel.statistic, "ks_pvalue": ks_gumbel.pvalue, "rmse": rmse_gumbel},
            {"method": "Log-Pearson III", "ks_stat": ks_lp3.statistic, "ks_pvalue": ks_lp3.pvalue, "rmse": rmse_lp3},
            {"method": "GEV", "ks_stat": ks_gev.statistic, "ks_pvalue": ks_gev.pvalue, "rmse": rmse_gev},
        ]
    )


def main():
    if not IN_PATH.exists():
        raise SystemExit(
            f"{IN_PATH} not found. Run 03b_import_gauge_csv.py (real gauge) or "
            "03_download_discharge_glofas.py + 03c_extract_glofas_point.py "
            "(reanalysis fallback) first."
        )

    df = pd.read_csv(IN_PATH, parse_dates=["date"])
    am = annual_maxima(df)
    if len(am) < 10:
        print(
            f"WARNING: only {len(am)} annual maxima available -- FFA parameter "
            "estimates (especially skew for Log-Pearson III) will be unstable "
            "with this little data. The original thesis used ~10 years; more "
            "is better if the record allows it."
        )

    gumbel_results = gumbel_return_period_discharge(am, RETURN_PERIODS_YEARS)
    lp3_results, skew = log_pearson3_return_period_discharge(am, RETURN_PERIODS_YEARS)
    gev_results, gev_shape = gev_return_period_discharge(am, RETURN_PERIODS_YEARS)

    table = pd.DataFrame(
        {
            "return_period_years": RETURN_PERIODS_YEARS,
            "gumbel_discharge_m3s": [gumbel_results[T] for T in RETURN_PERIODS_YEARS],
            "log_pearson3_discharge_m3s": [lp3_results[T] for T in RETURN_PERIODS_YEARS],
            "gev_discharge_m3s": [gev_results[T] for T in RETURN_PERIODS_YEARS],
        }
    )
    table_path = OUTPUTS / "ffa_return_period_table.csv"
    table.to_csv(table_path, index=False)
    print(f"Return-period discharge table -> {table_path}")
    print(table.to_string(index=False))
    print(f"\nLog-Pearson III skew coefficient: {skew:.4f}")
    print(f"GEV shape parameter (scipy convention): {gev_shape:.4f}")

    gof = goodness_of_fit(am, gumbel_results, lp3_results, gev_results, gev_shape)
    gof_path = OUTPUTS / "ffa_goodness_of_fit.csv"
    gof.to_csv(gof_path, index=False)
    print(f"\nGoodness-of-fit -> {gof_path}")
    print(gof.to_string(index=False))

    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(RETURN_PERIODS_YEARS, table["gumbel_discharge_m3s"], "o-", label="Gumbel")
    ax.plot(RETURN_PERIODS_YEARS, table["log_pearson3_discharge_m3s"], "s-", label="Log-Pearson III")
    # Only plot GEV if its estimates stay within a sane range of the other two
    # methods -- with a short record its MLE fit can be degenerate (see
    # gev_return_period_discharge docstring), and an unbounded curve wrecks
    # the plot's scale without adding any real information. The degenerate
    # case is still reported in the table/goodness-of-fit CSVs and called out
    # in the paper text; it just isn't worth plotting.
    other_max = max(table["gumbel_discharge_m3s"].max(), table["log_pearson3_discharge_m3s"].max())
    if table["gev_discharge_m3s"].max() <= 5 * other_max:
        ax.plot(RETURN_PERIODS_YEARS, table["gev_discharge_m3s"], "^-", label="GEV")
    else:
        print(
            "\nNOTE: GEV curve excluded from plot -- its fit is degenerate "
            f"(max estimate {table['gev_discharge_m3s'].max():.2e} m3/s vs. "
            f"~{other_max:.0f} m3/s for Gumbel/Log-Pearson III). See table/CSV "
            "for the raw (unstable) values."
        )
    ax.set_xscale("log")
    ax.set_xlabel("Return period (years)")
    ax.set_ylabel("Discharge (m$^3$/s)")
    ax.set_title("Flood Frequency Analysis -- Koshi Basin")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    fig.tight_layout()
    plot_path = OUTPUTS / "ffa_comparison_plot.png"
    fig.savefig(plot_path, dpi=150)
    print(f"\nComparison plot -> {plot_path}")


if __name__ == "__main__":
    main()
