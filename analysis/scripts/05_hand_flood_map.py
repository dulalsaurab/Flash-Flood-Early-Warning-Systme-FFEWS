"""
Open-source replacement for the original system's ArcGIS/ArcObjects + HEC-RAS
flood-mapping pipeline: a HAND (Height Above Nearest Drainage) based
inundation map, computed entirely from the open Copernicus GLO-30 DEM with
`pysheds`.

Method (replaces the original's 4-phase ArcMap/HEC-RAS/RAS-mapping pipeline):
  1. Condition the DEM (fill pits/depressions, resolve flats) and compute flow
     direction + accumulation.
  2. Derive a stream network mask from accumulation, and compute HAND: for
     every DEM cell, its vertical height above the nearest stream cell.
  3. Because we do not have a surveyed river cross-section (the original
     system's RAS geometry step), estimate channel depth at a given
     return-period discharge Q_T using an empirical hydraulic-geometry
     power-law relation (Leopold & Maddock, 1953-style: depth = c * Q^d --
     see config.HYDRAULIC_GEOMETRY). This is the key methodological
     substitution versus the original HEC-RAS steady-flow solve, and must be
     stated plainly as a simplification in the paper (Section IV/VI) --
     TODO(verify): replace the generic coefficients with a Nepal/Himalayan
     regional regression if one can be found in the literature.
  4. Threshold the HAND raster at that depth to produce a binary inundation
     map for each return period.

Also used for a direct DEM-source comparison: pass `--dem fabdem` to run the
identical pipeline against FABDEM (Forest And Buildings removed DEM,
07_download_fabdem.py) instead of the raw Copernicus GLO-30 DEM, with outputs
under a separate `_fabdem` suffix so both results are kept side by side. This
tests whether Copernicus's forest/building canopy bias (which inflates
apparent elevation, and so HAND, over vegetated terrain) is actually moving
the flood-extent result for this AOI.

Input:
    data/raw/dem_koshi_glo30.tif OR data/raw/dem_koshi_fabdem.tif
    outputs/ffa_return_period_table.csv   (from 04_flood_frequency_analysis.py)

Output:
    outputs/hand[_fabdem].tif
    outputs/inundation_T{return_period}[_fabdem].tif
    outputs/inundation_map_T{return_period}[_fabdem].png

Usage:
    python 05_hand_flood_map.py               # raw Copernicus GLO-30 DEM
    python 05_hand_flood_map.py --dem fabdem  # FABDEM comparison run
"""

import argparse
import sys
from pathlib import Path

import numpy as np

# pysheds 0.5 calls the removed numpy.in1d (dropped in NumPy 2.x, replaced by
# numpy.isin). Monkey-patch the alias rather than downgrading NumPy -- the two
# are functionally equivalent for pysheds' membership-test usage here.
if not hasattr(np, "in1d"):
    np.in1d = np.isin

import matplotlib.pyplot as plt
import pandas as pd
import rasterio
from pysheds.grid import Grid

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import DATA_RAW, OUTPUTS, HYDRAULIC_GEOMETRY, RETURN_PERIODS_YEARS  # noqa: E402

DEM_SOURCES = {
    "copernicus": (DATA_RAW / "dem_koshi_glo30.tif", ""),
    "fabdem": (DATA_RAW / "dem_koshi_fabdem.tif", "_fabdem"),
}
FFA_TABLE_PATH = OUTPUTS / "ffa_return_period_table.csv"

# Which FFA method's discharge estimates to map. The West Rapti Basin (2025)
# comparator paper used Gumbel's method for its HEC-RAS mapping, so default
# to the same for comparability.
DISCHARGE_COLUMN = "gumbel_discharge_m3s"

# Return periods (years) to actually generate maps for -- mapping all 8 from
# the FFA table is unnecessary; these three cover the low/medium/high
# probability scenarios described in the thesis's own flood-map literature
# review (Ch. 2.3).
MAP_RETURN_PERIODS = [10, 100, 200]

# Flow accumulation threshold (# of contributing cells) used to delineate the
# stream network from the DEM. Tune this if the resulting stream network
# looks too sparse/dense for the AOI at hand.
STREAM_ACCUMULATION_THRESHOLD = 1000


def condition_and_flow(grid: Grid, dem):
    pit_filled = grid.fill_pits(dem)
    flooded = grid.fill_depressions(pit_filled)
    inflated = grid.resolve_flats(flooded)
    fdir = grid.flowdir(inflated)
    acc = grid.accumulation(fdir)
    return inflated, fdir, acc


def depth_for_discharge(q_m3s: float) -> float:
    """Empirical hydraulic-geometry depth estimate (Leopold & Maddock, 1953
    style): depth = c * Q^d. See config.HYDRAULIC_GEOMETRY docstring for the
    caveat about these being generic literature defaults, not a Nepal-fitted
    regression.
    """
    c, d = HYDRAULIC_GEOMETRY["depth_c"], HYDRAULIC_GEOMETRY["depth_d"]
    return c * (q_m3s ** d)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--dem", choices=sorted(DEM_SOURCES), default="copernicus")
    args = parser.parse_args()
    dem_path, suffix = DEM_SOURCES[args.dem]

    if not dem_path.exists():
        source_script = "01_download_dem.py" if args.dem == "copernicus" else "07_download_fabdem.py"
        raise SystemExit(f"{dem_path} not found -- run {source_script} first.")
    if not FFA_TABLE_PATH.exists():
        raise SystemExit(f"{FFA_TABLE_PATH} not found -- run 04_flood_frequency_analysis.py first.")

    ffa = pd.read_csv(FFA_TABLE_PATH)

    print(f"Loading DEM ({args.dem}) from {dem_path} ...")
    grid = Grid.from_raster(str(dem_path))
    dem = grid.read_raster(str(dem_path))

    print("Conditioning DEM and computing flow direction/accumulation ...")
    conditioned_dem, fdir, acc = condition_and_flow(grid, dem)

    stream_mask = acc > STREAM_ACCUMULATION_THRESHOLD
    print(f"Stream network: {stream_mask.sum()} cells above accumulation threshold {STREAM_ACCUMULATION_THRESHOLD}")

    print("Computing HAND (height above nearest drainage) ...")
    hand = grid.compute_hand(fdir, conditioned_dem, stream_mask)

    hand_path = OUTPUTS / f"hand{suffix}.tif"
    grid.to_raster(hand, str(hand_path))
    print(f"HAND raster -> {hand_path}")

    for T in MAP_RETURN_PERIODS:
        row = ffa.loc[ffa["return_period_years"] == T]
        if row.empty:
            print(f"WARNING: return period {T} not in FFA table, skipping.")
            continue
        q_t = float(row[DISCHARGE_COLUMN].iloc[0])
        depth = depth_for_discharge(q_t)
        inundated = (hand <= depth) & np.isfinite(hand)

        raster_path = OUTPUTS / f"inundation_T{T}{suffix}.tif"
        grid.to_raster(inundated.astype("float32"), str(raster_path), nodata=0.0, dtype="float32")

        fig, ax = plt.subplots(figsize=(7, 6))
        ax.imshow(conditioned_dem, cmap="terrain", alpha=0.6)
        ax.imshow(np.ma.masked_where(~inundated, inundated), cmap="Blues", alpha=0.8)
        ax.set_title(f"HAND-based inundation extent ({args.dem}), T={T} yr (Q={q_t:.0f} m$^3$/s, depth={depth:.2f} m)")
        ax.set_axis_off()
        fig.tight_layout()
        png_path = OUTPUTS / f"inundation_map_T{T}{suffix}.png"
        fig.savefig(png_path, dpi=150)
        plt.close(fig)

        print(f"T={T} yr: Q={q_t:.1f} m3/s -> depth={depth:.2f} m -> {inundated.sum()} inundated cells -> {raster_path}, {png_path}")


if __name__ == "__main__":
    main()
