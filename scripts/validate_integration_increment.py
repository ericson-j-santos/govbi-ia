from pathlib import Path
root = Path(__file__).resolve().parents[1]
required = [
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/CanalNotificacaoPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/notificacao/CanalNotificacaoTeamsWebhookAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/notificacao/CanalNotificacaoEmailSmtpAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/DeadLetterConsultaPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/LockDistribuidoPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/DownloadResultadoPort.java',
    'backend/src/main/resources/db/migration/V0009__notificacoes_reais_dlq_lock_download.sql',
    'frontend-angular/src/app/operacional/operacional-enterprise-v09.component.ts',
    'docs/ADR-019-integracao-corporativa-notificacoes-dlq-lock-download.md',
]
missing = [p for p in required if not (root / p).exists()]
if missing:
    raise SystemExit('missing=' + ','.join(missing))
app = (root / 'backend/src/main/resources/application.yml').read_text(encoding='utf-8')
for marker in ['integracao-corporativa:', 'teams:', 'email:', 'dlq:', 'download:']:
    if marker not in app:
        raise SystemExit(f'marker ausente: {marker}')
print('integration_increment=OK')
