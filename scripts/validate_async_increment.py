from pathlib import Path
import sys

root = Path(__file__).resolve().parents[1]
required = [
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/worker/FilaConsultaWorker.java',
    'backend/src/main/java/br/com/reqsys/govbi/aplicacao/caso_uso/ProcessarFilaConsultaUseCase.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/ResultadoConsultaPersistidaPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/NotificacaoOperacionalPort.java',
    'backend/src/main/resources/db/migration/V0008__operacao_assincrona_resultados_notificacoes.sql',
    'docs/ADR-018-operacao-assincrona-real.md',
    'docs/RUN_OPERACAO_ASSINCRONA.md',
]
missing = [p for p in required if not (root / p).exists()]
if missing:
    print('async_increment=FAIL')
    for p in missing: print('missing', p)
    sys.exit(1)

sql = (root/'backend/src/main/resources/db/migration/V0008__operacao_assincrona_resultados_notificacoes.sql').read_text(encoding='utf-8')
for token in ['resultado_consulta', 'notificacao_operacional', 'status_retencao', 'ISJSON']:
    if token not in sql:
        print('async_increment=FAIL missing_sql_token', token)
        sys.exit(1)
print('async_increment=OK')
