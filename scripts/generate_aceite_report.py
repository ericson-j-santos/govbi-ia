#!/usr/bin/env python3
import argparse, json, time
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
REPORT_DIR = ROOT / 'reports' / 'homologacao'
REPORT_DIR.mkdir(parents=True, exist_ok=True)

def exists(rel):
    p = ROOT / rel
    return p.exists() and p.stat().st_size > 0

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--ambiente', default='hom')
    ap.add_argument('--decisao', default='pendente', choices=['pendente','aprovado','aprovado-com-ressalvas','reprovado'])
    args = ap.parse_args()
    evidencias = [
        'reports/homologacao/smoke-v1.0.1.json',
        'docs/ACEITE_FORMAL_V101.md',
        'docs/PLANO_ROLLBACK_V101.md',
        'release/homologacao-manifest-v1.0.1.json',
        'backend/src/main/resources/release/checklist-producao-v1.0.1.json',
    ]
    report = {
        'produto':'GovBI IA',
        'versao':'1.0.1',
        'ambiente':args.ambiente,
        'decisao':args.decisao,
        'geradoEmEpoch':int(time.time()),
        'evidencias':[{ 'arquivo': e, 'presente': exists(e)} for e in evidencias],
        'pendencias':[e for e in evidencias if not exists(e)],
        'observacao':'Relatório inicial de aceite. A decisão final deve ser formalizada pelos aprovadores.'
    }
    out = REPORT_DIR / 'aceite-v1.0.1.json'
    out.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    print('aceite_report=OK' if not report['pendencias'] else 'aceite_report=PENDENTE_EVIDENCIAS')
    print('report=' + str(out))
if __name__ == '__main__':
    main()
