"""
Fetch gauge water-level (stage) history from Nepal's official BIPAD disaster
portal (bipadportal.gov.np), sourced from hydrology.gov.np, for the two study
stations. This is stage (m), not discharge (m3/s) -- there is no rating curve
available to convert it -- so it is used only as an independent, real-observed
check of *when* extreme events occurred on this basin (see
10_bipad_glofas_event_check.py), not as a discharge source.

BIPAD's `/api/v1/river/` endpoint returns a paginated, cumulative log of
station readings (not just the current value); paginating through it with a
per-station filter recovers the full archived history for that station.

Input:
    https://bipadportal.gov.np/api/v1/river/ (public API, no auth required)

Output:
    data/raw/bipad/{name}_raw.json           -- full raw API records
    data/raw/bipad/{name}_waterlevel.csv      -- flat per-reading CSV
    data/processed/bipad_stage_daily.csv      -- daily max per station (both)

Usage:
    python 09_fetch_bipad_stage.py
"""

import json
import sys
import time
import urllib.request
from pathlib import Path

import pandas as pd

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from config import DATA_PROCESSED, DATA_RAW  # noqa: E402

BASE_URL = "https://bipadportal.gov.np/api/v1/river/"
PAGE_LIMIT = 1000

# BIPAD internal station ids (confirmed by matching coordinates against the
# DHM-published station list in config.py: both are within a few meters of
# the 681/695 coordinates already used for the FFA/HAND re-run).
STATIONS = {
    "hampachuwar_31": {"bipad_station_id": 31, "dhm_station": "681"},
    "chatara_58": {"bipad_station_id": 58, "dhm_station": "695"},
}

BIPAD_DIR = DATA_RAW / "bipad"
BIPAD_DIR.mkdir(parents=True, exist_ok=True)

# Known sensor artifact: Chatara's single highest raw reading (15.837 m,
# 2020-12-26) occurs in the dry season, when discharge -- and therefore
# stage -- should be near its annual minimum, not ~2x the danger level
# (7.0 m). No corresponding event appears in any news/DHM source for this
# date. Excluded as a sensor glitch rather than fed into any generic
# outlier filter.
KNOWN_ARTIFACTS = {
    "chatara_58": [pd.Timestamp("2020-12-26", tz="+05:45")],
}


def fetch_station_history(station_id: int) -> list[dict]:
    records = []
    offset = 0
    while True:
        url = f"{BASE_URL}?format=json&station={station_id}&limit={PAGE_LIMIT}&offset={offset}"
        with urllib.request.urlopen(url, timeout=30) as resp:
            data = json.load(resp)
        results = data.get("results", [])
        if not results:
            break
        records.extend(results)
        offset += PAGE_LIMIT
        if len(results) < PAGE_LIMIT:
            break
        time.sleep(0.1)  # be polite to a public government API
    return records


def main():
    daily_frames = []

    for name, info in STATIONS.items():
        station_id = info["bipad_station_id"]
        print(f"Fetching BIPAD station {station_id} ({name}) ...")
        records = fetch_station_history(station_id)
        if not records:
            raise SystemExit(f"No records returned for station {station_id} -- API may have changed.")

        raw_path = BIPAD_DIR / f"{name}_raw.json"
        with open(raw_path, "w") as f:
            json.dump(records, f)
        print(f"  {len(records)} raw records -> {raw_path}")

        df = pd.DataFrame(records)
        df["waterLevelOn"] = pd.to_datetime(df["waterLevelOn"])
        df = df.drop_duplicates(subset=["waterLevelOn"]).sort_values("waterLevelOn").reset_index(drop=True)

        for artifact_ts in KNOWN_ARTIFACTS.get(name, []):
            before = len(df)
            df = df[df["waterLevelOn"].dt.date != artifact_ts.date()]
            if len(df) < before:
                print(f"  excluded {before - len(df)} reading(s) on {artifact_ts.date()} (known sensor artifact)")

        csv_path = BIPAD_DIR / f"{name}_waterlevel.csv"
        df[["waterLevelOn", "waterLevel", "status", "dangerLevel", "warningLevel", "steady"]].to_csv(
            csv_path, index=False
        )
        print(f"  cleaned series ({len(df)} readings, {df['waterLevelOn'].min()} to {df['waterLevelOn'].max()}) -> {csv_path}")

        daily = df.groupby(df["waterLevelOn"].dt.date)["waterLevel"].max().reset_index()
        daily.columns = ["date", "water_level_max_m"]
        daily["station"] = name
        daily["dhm_station"] = info["dhm_station"]
        daily["danger_level_m"] = df["dangerLevel"].dropna().iloc[0] if df["dangerLevel"].notna().any() else None
        daily["warning_level_m"] = df["warningLevel"].dropna().iloc[0] if df["warningLevel"].notna().any() else None
        daily_frames.append(daily)

    combined = pd.concat(daily_frames, ignore_index=True)
    combined = combined.sort_values(["station", "date"]).reset_index(drop=True)
    out_path = DATA_PROCESSED / "bipad_stage_daily.csv"
    combined.to_csv(out_path, index=False)
    print(f"\nCombined daily-max stage (both stations) -> {out_path}")


if __name__ == "__main__":
    main()
