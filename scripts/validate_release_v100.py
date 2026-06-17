#!/usr/bin/env python3
from pathlib import Path
import json, re, sys

ROOT = Path(__file__).resolve().parents[1]
required = [
    'docs/ADR-020-consolidacao-release-corporativa-v100.md',
    'docs/DOCUMENTACAO_EXECUTIVA_V100.md',
    'docs/CHECKLIST_PRODUCAO_V100.md',
    'docs/MATRIZ_RASTREABILIDADE_V100.md',
    'docs/GUIA_IMPLANTACAO_HOMOLOG_PROD_V100.md',
    'docs/HARDENING_SEGURANCA_V100.md',
    'docs/TEST_PLAN_E2E_V100.md',
    'docs/RELEASE_NOTES_V100.md',
    'docs/openapi/govbi-ia-v1.0.0.yaml',
    'backend/src/main/resources/application-hom.yml',
    'backend/src/main/resources/application-prod.yml',
    'backend/src/main/resources/release/checklist-producao-v1.0.0.json',
    'backend/src/main/resources/release/matriz-rastreabilidade-v1.0.0.json',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/ReleaseReadinessController.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/ReleaseReadinessPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/modelo/ReleaseReadinessStatus.java',
    'backend/src/main/resources/db/migration/V0010__release_corporativa_v100.sql',
    'release/release-manifest-v1.0.0.json',
]
missing = [p for p in required if not (ROOT / p).exists() or (ROOT / p).stat().st_size == 0]
if missing:
    print('FAIL missing=' + ','.join(missing))
    sys.exit(2)

pom = (ROOT / 'backend/pom.xml').read_text(encoding='utf-8')
app = (ROOT / 'backend/src/main/resources/application.yml').read_text(encoding='utf-8')
cat = (ROOT / 'backend/src/main/resources/catalogo-semantico.yml').read_text(encoding='utf-8')
readme = (ROOT / 'README.md').read_text(encoding='utf-8')
changelog = (ROOT / 'CHANGELOG.md').read_text(encoding='utf-8')
checks = {
    'pom_version_10x': '<version>1.0.0</version>' in pom or '<version>1.0.1</version>' in pom,
    'app_version_10x': 'GOVBI_VERSAO:1.0.0' in app or 'versao: ${GOVBI_VERSAO:1.0.0}' in app or 'GOVBI_VERSAO:1.0.1' in app or 'versao: ${GOVBI_VERSAO:1.0.1}' in app,
    'catalog_version_10x': ('versao: "1.0.0"' in cat and 'compatibilidadeMinimaBackend: "1.0.0"' in cat) or ('versao: "1.0.1"' in cat and 'compatibilidadeMinimaBackend: "1.0.1"' in cat),
    'readme_10x': 'v1.0.0' in readme and ('release candidate' in readme.lower() or 'homologação' in readme.lower()),
    'changelog_10x': '[1.0.0]' in changelog and ('[1.0.1]' in changelog or True),
    'release_yaml': 'release:' in app and 'hardening-obrigatorio' in app,
    'readiness_endpoint_doc': '/api/v1/release/readiness' in readme or '/api/v1/release/readiness' in (ROOT / 'docs/openapi/govbi-ia-v1.0.0.yaml').read_text(encoding='utf-8'),
}
for name, ok in checks.items():
    print(('OK ' if ok else 'FAIL ') + name)
if not all(checks.values()):
    sys.exit(2)
manifest = json.loads((ROOT / 'release/release-manifest-v1.0.0.json').read_text(encoding='utf-8'))
if manifest.get('versao') != '1.0.0':
    print('FAIL manifest version')
    sys.exit(2)
print('release_v100=OK')
