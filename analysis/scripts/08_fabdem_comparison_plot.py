"""
Compare inundated area (Copernicus GLO-30 raw DEM vs. FABDEM bias-corrected
DEM) across return periods, using the cell counts already produced by
05_hand_flood_map.py (run once with --dem copernicus, once with --dem fabdem).

Output:
    outputs/fabdem_comparison.csv
    outputs/fabdem_comparison_plot.png

Usage:
    python 08_fabdem_comparison_plot.py
"""

import sys
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import rasterio

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import OUTPUTS  # noqa: E402

RETURN_PERIODS = [10, 100, 200]
CELL_AREA_KM2 = 0.0009  # ~30 m x 30 m


def inundated_cell_count(path: Path) -> int:
    with rasterio.open(path) as src:
        data = src.read(1)
    return int((data > 0).sum())


def main():
    rows = []
    for T in RETURN_PERIODS:
        cop_path = OUTPUTS / f"inundation_T{T}.tif"
        fab_path = OUTPUTS / f"inundation_T{T}_fabdem.tif"
        if not cop_path.exists() or not fab_path.exists():
            raise SystemExit(
                f"Missing {cop_path} or {fab_path} -- run "
                "`05_hand_flood_map.py` and `05_hand_flood_map.py --dem fabdem` first."
            )
        cop_cells = inundated_cell_count(cop_path)
        fab_cells = inundated_cell_count(fab_path)
        cop_km2 = cop_cells * CELL_AREA_KM2
        fab_km2 = fab_cells * CELL_AREA_KM2
        pct_change = 100 * (fab_km2 - cop_km2) / cop_km2
        rows.append(
            {
                "return_period_years": T,
                "copernicus_km2": cop_km2,
                "fabdem_km2": fab_km2,
                "pct_change": pct_change,
            }
        )

    df = pd.DataFrame(rows)
    csv_path = OUTPUTS / "fabdem_comparison.csv"
    df.to_csv(csv_path, index=False)
    print(f"FABDEM vs Copernicus comparison -> {csv_path}")
    print(df.to_string(index=False))

    fig, ax = plt.subplots(figsize=(7, 5))
    x = np.arange(len(RETURN_PERIODS))
    width = 0.35
    ax.bar(x - width / 2, df["copernicus_km2"], width, label="Copernicus GLO-30 (raw)", color="tab:blue")
    ax.bar(x + width / 2, df["fabdem_km2"], width, label="FABDEM (bias-corrected)", color="tab:green")
    for i, row in df.iterrows():
        ax.annotate(
            f"+{row['pct_change']:.1f}%",
            xy=(i + width / 2, row["fabdem_km2"]),
            xytext=(0, 4),
            textcoords="offset points",
            ha="center",
            fontsize=9,
        )
    ax.set_xticks(x)
    ax.set_xticklabels([f"T={T} yr" for T in RETURN_PERIODS])
    ax.set_ylabel("Inundated area (km$^2$)")
    ax.set_title("HAND inundation extent: raw Copernicus DEM vs. FABDEM")
    ax.legend()
    ax.grid(True, axis="y", alpha=0.3)
    fig.tight_layout()
    plot_path = OUTPUTS / "fabdem_comparison_plot.png"
    fig.savefig(plot_path, dpi=150)
    print(f"\nComparison plot -> {plot_path}")


if __name__ == "__main__":
    main()
