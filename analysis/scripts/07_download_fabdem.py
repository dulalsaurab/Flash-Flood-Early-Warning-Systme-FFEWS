"""
Download FABDEM (Forest And Buildings removed Copernicus DEM, University of
Bristol) for the same AOI as the raw Copernicus GLO-30 DEM (01_download_dem.py).

FABDEM is a machine-learning bias correction of the exact same underlying
Copernicus DEM already used in this pipeline, specifically removing forest
canopy and building height bias -- which matters directly for HAND: over
forested terrain, a raw DSM's canopy height inflates the apparent elevation
of the land surface, which inflates HAND values and can understate flood
extent in vegetated areas. This script produces a second DEM so the HAND
flood-mapping step (05_hand_flood_map.py) can be re-run against it and
compared with the raw-DEM result -- a direct, real methodological comparison,
not just a citation of FABDEM's existence.

Source: data.bris.ac.uk (CC BY-NC-SA 4.0, non-commercial use), accessed here
via the `fabdem` PyPI package rather than manually locating/unzipping the
10x10-degree source tiles.

Output:
    data/raw/dem_koshi_fabdem.tif

Usage:
    python 07_download_fabdem.py
"""

import sys
from pathlib import Path

import fabdem

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import BASIN_BBOX, DATA_RAW  # noqa: E402

OUT_PATH = DATA_RAW / "dem_koshi_fabdem.tif"


def main():
    bounds = (BASIN_BBOX["west"], BASIN_BBOX["south"], BASIN_BBOX["east"], BASIN_BBOX["north"])
    print(f"Downloading FABDEM for bounds {bounds} ...")
    fabdem.download(bounds, str(OUT_PATH))
    print(f"Saved FABDEM to {OUT_PATH}")


if __name__ == "__main__":
    main()
