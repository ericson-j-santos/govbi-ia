#!/usr/bin/env python3
from pathlib import Path
import json, sys
ROOT = Path(__file__).resolve().parents[1]
required = [
    'docs/ADR-021-homologacao-promocao-controlada-v101.md',
    'docs/HOMOLOGACAO_V101.md',
    'docs/ACEITE_FORMAL_V101.md',
    'docs/RUNBOOK_GO_LIVE_V101.md',
    'docs/PLANO_ROLLBACK_V101.md',
    'docs/PLANO_SMOKE_TESTS_HOMOLOG_PROD_V101.md',
    'docs/MATRIZ_CONFIG_AMBIENTES_V101.md',
    'docs/DOSSIE_HOMOLOGACAO_V101.md',
    'docs/RELEASE_NOTES_V101.md',
    'deploy/env/.env.hom.example',
    'deploy/env/.env.prod.example',
    'database/sqlserver/003_smoke_queries_homologacao.sql',
    'backend/src/main/resources/release/checklist-producao-v1.0.1.json',
    'backend/src/main/resources/release/matriz-rastreabilidade-v1.0.1.json',
    'release/homologacao-manifest-v1.0.1.json',
    'scripts/run_homologation_smoke.py',
    'scripts/generate_aceite_report.py',
]
missing = [p for p in required if not (ROOT/p).exists() or (ROOT/p).stat().st_size == 0]
if missing:
    print('FAIL missing=' + ','.join(missing))
    sys.exit(2)
texts = {
    'pom': (ROOT/'backend/pom.xml').read_text(encoding='utf-8'),
    'app': (ROOT/'backend/src/main/resources/application.yml').read_text(encoding='utf-8'),
    'catalogo': (ROOT/'backend/src/main/resources/catalogo-semantico.yml').read_text(encoding='utf-8'),
    'readme': (ROOT/'README.md').read_text(encoding='utf-8'),
    'changelog': (ROOT/'CHANGELOG.md').read_text(encoding='utf-8'),
}
checks = {
    'pom_version_101': '<version>1.0.1</version>' in texts['pom'],
    'app_version_101': 'GOVBI_VERSAO:1.0.1' in texts['app'] or 'versao: ${GOVBI_VERSAO:1.0.1}' in texts['app'],
    'release_mode_homologacao': 'modo: ${GOVBI_RELEASE_MODO:homologacao}' in texts['app'],
    'catalog_version_101': 'versao: "1.0.1"' in texts['catalogo'] and 'compatibilidadeMinimaBackend: "1.0.1"' in texts['catalogo'],
    'homologacao_block': 'homologacaoV101:' in texts['catalogo'],
    'readme_101': 'v1.0.1' in texts['readme'] and 'homologação' in texts['readme'].lower(),
    'changelog_101': '[1.0.1]' in texts['changelog'],
}
for name, ok in checks.items():
    print(('OK ' if ok else 'FAIL ') + name)
if not all(checks.values()):
    sys.exit(2)
manifest = json.loads((ROOT/'release/homologacao-manifest-v1.0.1.json').read_text(encoding='utf-8'))
if manifest.get('versao') != '1.0.1' or manifest.get('status') != 'homologacao-operacional':
    print('FAIL manifest_v101')
    sys.exit(2)
print('homologation_v101=OK')
