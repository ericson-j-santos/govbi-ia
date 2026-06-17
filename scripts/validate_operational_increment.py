from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
required = [
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/AprovacaoHumanaPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/HistoricoConversacionalPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/AuditoriaConsultavelPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/ExportadorResultadoPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/CatalogoAdminPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/AprovacaoController.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/HistoricoController.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/AuditoriaConsultaController.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/ExportacaoController.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/CatalogoAdminController.java',
    'docs/ADR-016-produto-corporativo-operacional.md',
    'docs/openapi/govbi-ia-v0.6.0.yaml',
    'deploy/docker/Dockerfile',
    'deploy/k8s/deployment.yml',
    'demo/govbi-ia-demo.html',
]
missing = [p for p in required if not (ROOT/p).exists() or (ROOT/p).stat().st_size == 0]
if missing:
    print('operational_increment=FAIL')
    for p in missing:
        print('MISSING', p)
    raise SystemExit(2)
print('operational_increment=OK')
print('required_files=', len(required))
