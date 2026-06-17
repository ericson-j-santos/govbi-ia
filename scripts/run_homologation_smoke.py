#!/usr/bin/env python3
import argparse, json, sys, time, urllib.request, urllib.error
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REPORT_DIR = ROOT / 'reports' / 'homologacao'
REPORT_DIR.mkdir(parents=True, exist_ok=True)

def request(base_url, method, path, body=None, timeout=10):
    data = None
    headers = {
        'Content-Type': 'application/json',
        'X-Usuario': 'homologador.govbi',
        'X-Perfil': 'ANALISTA,BI_GOVERNADO',
        'X-Escopo-Unidade': 'GERAL'
    }
    if body is not None:
        data = json.dumps(body).encode('utf-8')
    req = urllib.request.Request(base_url.rstrip('/') + path, data=data, method=method, headers=headers)
    with urllib.request.urlopen(req, timeout=timeout) as resp:
        content = resp.read().decode('utf-8', errors='replace')
        try:
            parsed = json.loads(content) if content else {}
        except Exception:
            parsed = {'raw': content[:500]}
        return {'status': resp.status, 'body': parsed}

def offline_case(name, ok=True, detalhe='offline'):
    return {'nome': name, 'status': 'OK' if ok else 'FAIL', 'detalhe': detalhe}

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--base-url', default='http://localhost:8080')
    ap.add_argument('--modo', default='offline', choices=['offline','hom','prod'])
    ap.add_argument('--no-write', action='store_true')
    args = ap.parse_args()

    cases = []
    if args.modo == 'offline':
        required = [
            'docs/HOMOLOGACAO_V101.md',
            'docs/ACEITE_FORMAL_V101.md',
            'docs/RUNBOOK_GO_LIVE_V101.md',
            'docs/PLANO_ROLLBACK_V101.md',
            'release/homologacao-manifest-v1.0.1.json',
            'deploy/env/.env.hom.example',
            'deploy/env/.env.prod.example',
        ]
        for item in required:
            p = ROOT / item
            cases.append(offline_case(item, p.exists() and p.stat().st_size > 0, 'arquivo obrigatório'))
    else:
        live_tests = [
            ('health', 'GET', '/actuator/health', None),
            ('readiness', 'GET', '/api/v1/release/readiness', None),
            ('consulta-governada', 'POST', '/api/v1/perguntas', {'pergunta':'Mostre propostas cadastradas por mês em 2025 por situação','formatoResposta':'tabela','exibirSql':True}),
            ('consulta-sensivel', 'POST', '/api/v1/perguntas', {'pergunta':'Liste CPF e nome dos clientes por proposta','formatoResposta':'tabela','exibirSql':True}),
            ('auditoria', 'GET', '/api/v1/auditoria/recentes', None),
            ('dlq', 'GET', '/api/v1/dlq-consultas/recentes', None),
            ('prometheus', 'GET', '/actuator/prometheus', None),
        ]
        for name, method, path, body in live_tests:
            try:
                if args.no_write and method == 'POST' and name != 'consulta-governada':
                    cases.append({'nome': name, 'status': 'SKIP', 'detalhe': 'no-write'})
                    continue
                res = request(args.base_url, method, path, body)
                ok = 200 <= res['status'] < 300
                cases.append({'nome': name, 'status': 'OK' if ok else 'FAIL', 'http_status': res['status'], 'amostra': str(res['body'])[:300]})
            except Exception as exc:
                cases.append({'nome': name, 'status': 'FAIL', 'erro': str(exc)[:300]})
    report = {
        'produto': 'GovBI IA',
        'versao': '1.0.1',
        'modo': args.modo,
        'baseUrl': args.base_url if args.modo != 'offline' else None,
        'geradoEmEpoch': int(time.time()),
        'casos': cases,
        'resultado': 'OK' if all(c['status'] in ('OK','SKIP') for c in cases) else 'FAIL'
    }
    out = REPORT_DIR / 'smoke-v1.0.1.json'
    out.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    for c in cases:
        print(f"{c['status']} {c['nome']}")
    print('smoke_homologacao=' + report['resultado'])
    print('report=' + str(out))
    return 0 if report['resultado'] == 'OK' else 2

if __name__ == '__main__':
    raise SystemExit(main())
