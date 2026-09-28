# FFEWS re-run pipeline

Modernized, open-data-only re-run of the original 2014 FFEWS thesis project's
three analysis components (satellite rainfall, flood frequency analysis,
flood-map generation), for the Koshi Basin, Nepal, using free/open data and
open-source tools instead of the original's licensed ArcGIS/ArcObjects/HEC-RAS
desktop stack. This feeds the "modernized" results reported in `../paper/draft.md`.

## Setup

```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
cp .env.example .env   # then fill in the three free API credentials below
```

You need three free accounts (none require payment):

| Credential | Used for | Get it at |
|---|---|---|
| `OPENTOPOGRAPHY_API_KEY` | Copernicus GLO-30 DEM | opentopography.org -> myOpenTopo -> Request API key |
| `EARTHDATA_USERNAME` / `EARTHDATA_PASSWORD` | GPM IMERG rainfall | urs.earthdata.nasa.gov |
| `CDS_URL` / `CDS_KEY` | GloFAS discharge (fallback only) | **ewds.climate.copernicus.eu** (NOT the main CDS site -- GloFAS moved to the separate CEMS Early Warning Data Store) -> how-to-api page -> API key. Also open the "CEMS-GloFAS historical" dataset page there and accept its license once. |

## Run order

1. `python scripts/01_download_dem.py` -- pulls the Copernicus GLO-30 DEM for the Koshi Basin AOI (config.py `BASIN_BBOX`).
2. `python scripts/02_download_rainfall.py` -- pulls GPM IMERG monthly precipitation for the same AOI/period.
3. **Discharge** (pick ONE, in order of preference):
   - `python scripts/03b_import_gauge_csv.py` -- **preferred**. Use this if a real observed gauge record was obtained from GRDC, ICIMOD RDS, or DHM. You must manually save the raw export to `data/raw/` and adjust the column-name constants at the top of the script to match its actual format first (GRDC and ICIMOD/DHM exports use different layouts).
   - `python scripts/03_download_discharge_glofas.py` then `python scripts/03c_extract_glofas_point.py` -- **fallback**. Uses GloFAS reanalysis discharge (model output, not an observed record) if no real gauge data could be found. This distinction must be stated plainly in the paper if used.
4. `python scripts/04_flood_frequency_analysis.py` -- Gumbel's method + Log-Pearson Type III on the annual-maximum series, with goodness-of-fit stats (KS test + RMSE) the original thesis never computed.
5. `python scripts/05_hand_flood_map.py` -- HAND-based inundation mapping (replaces ArcGIS/HEC-RAS) using `pysheds`, driven by the FFA return-period discharge estimates and an empirical hydraulic-geometry depth relation (since we have no surveyed cross-section).
6. `python scripts/06_rainfall_discharge_comparison.py` -- cross-checks basin-mean IMERG rainfall against monthly-mean GloFAS discharge over their period of overlap (paper Section 5.2).
7. `python scripts/07_download_fabdem.py`, then re-run `python scripts/05_hand_flood_map.py --dem fabdem`, then `python scripts/08_fabdem_comparison_plot.py` -- DEM sensitivity test, raw Copernicus vs. FABDEM (paper Section 5.4).
8. `python scripts/09_fetch_bipad_stage.py` then `python scripts/10_bipad_glofas_event_check.py` -- retrieves observed gauge stage from Nepal's BIPAD portal (no credentials needed) and checks it against the GloFAS series for event timing (paper Section 5.1).

## Troubleshooting: the GloFAS/EWDS API schema

The EWDS web form's "Show API request" code panel is **not reliable** -- as of 2026-09-11 it generated a stale/example snippet (`htessel_lisflood`, `mean_discharge_in_the_last_24_hours`, `hyear`/`hmonth`/`hday`) that the API rejects outright with a 400 "invalid combination of values" error, with none of the offending fields identified. If `03_download_discharge_glofas.py` starts failing again, get the real schema straight from the dataset's own process-description endpoint instead of trusting the form:

```bash
python3 -c "
import os, requests
from dotenv import load_dotenv
load_dotenv('.env')
url = os.environ['CDS_URL'].rstrip('/')
r = requests.get(f'{url}/retrieve/v1/processes/cems-glofas-historical',
                  headers={'PRIVATE-TOKEN': os.environ['CDS_KEY']})
for k, v in r.json()['inputs'].items():
    items = v.get('schema', {}).get('items', v.get('schema', {}))
    print(k, '->', items.get('enum', items))
"
```

This prints every field's real valid enum values directly. Also note: if the API request succeeds but the actual file download then fails with `SSL_CERT_VERIFY_FAILED`, that's a local Homebrew-Python certificate issue, not an API problem -- `pip install --upgrade certifi` and set `SSL_CERT_FILE`/`REQUESTS_CA_BUNDLE` to `certifi.where()` (already baked into the script itself).

## Known limitations

These are real constraints on what the results mean, stated here as well as in
the paper:

- **Discharge is GloFAS reanalysis, not an observed gauge record.** Neither
  station 681 (Sunkoshi at Hampachuwar) nor 695 (Saptakoshi at Chatara) has a
  freely downloadable digital daily series; both require a formal request
  through DHM's "Request Data" portal. Checked against a published gauge figure
  for the Koshi at Chatara, the GloFAS-derived series runs about 76% high in the
  one directly comparable year (2008). Return-period values should be read as
  illustrating what this workflow produces, not as validated design floods. Use
  `03b_import_gauge_csv.py` to substitute a real record once obtained.
- **Hydraulic-geometry coefficients** (`config.HYDRAULIC_GEOMETRY`) are generic
  literature defaults, not fitted to the Koshi or any Himalayan river, and are
  applied as a single basin-wide depth rather than varying with local
  contributing area. This is the largest methodological simplification relative
  to a surveyed cross-section and a distributed hydraulic solve.
- **The stream-delineation threshold** (1,000 contributing cells) is an
  untuned default, not calibrated against an independent channel-extent
  reference for this basin.
- **Rainfall is monthly**, adequate for climatological comparison but not for
  sub-daily rainfall-runoff forecasting.
- **BIPAD data is stage (m), not discharge.** Without a stage-discharge rating
  curve it supports event-timing checks only, not magnitude validation.
