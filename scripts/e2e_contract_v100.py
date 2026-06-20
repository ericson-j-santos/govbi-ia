#!/usr/bin/env python3
from pathlib import Path
import argparse, json, sys

ROOT = Path(__file__).resolve().parents[1]

def offline():
    dataset = json.loads((ROOT / 'backend/src/main/resources/evaluation/golden-dataset.json').read_text(encoding='utf-8'))
    ids = {c['id'] for c in dataset.get('casos', [])}
    required = {'GOVBI-GOLD-001', 'GOVBI-GOLD-002', 'GOVBI-GOLD-003', 'GOVBI-GOLD-004', 'GOVBI-GOLD-005', 'GOVBI-GOLD-006'}
    missing = required - ids
    if dataset.get('versao') not in ('1.0.0','1.0.1') or missing:
        print('FAIL e2e_contract offline missing=' + ','.join(sorted(missing)))
        return 2
    openapi = (ROOT / 'docs/openapi/govbi-ia-v1.0.1.yaml').read_text(encoding='utf-8')
    for path in ['/api/v1/perguntas', '/api/v1/release/readiness', '/api/v1/downloads/resultados/{resultadoId}']:
        if path not in openapi:
            print('FAIL openapi missing ' + path)
            return 2
    print('e2e_contract_v100=OK')
    return 0

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--offline', action='store_true')
    args = parser.parse_args()
    sys.exit(offline())
