from pathlib import Path
root = Path(__file__).resolve().parents[1]
required = [
    'backend/src/main/resources/db/migration/V0007__persistencia_operacional_sqlserver.sql',
    'database/sqlserver/001_schema_operacional.sql',
    'database/sqlserver/002_permissoes_readwrite_restritas.sql',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/persistencia/sqlserver/AprovacaoHumanaSqlServerAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/persistencia/sqlserver/HistoricoConversacionalSqlServerAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/persistencia/sqlserver/AuditoriaConsultavelSqlServerAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/persistencia/sqlserver/CatalogoAdminSqlServerAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/infraestrutura/adapter/persistencia/sqlserver/FilaConsultaSqlServerAdapter.java',
    'backend/src/main/java/br/com/reqsys/govbi/dominio/porta/FilaConsultaPort.java',
    'backend/src/main/java/br/com/reqsys/govbi/api/controller/FilaConsultaController.java',
]
missing = [p for p in required if not (root / p).exists()]
if missing:
    raise SystemExit('MISSING ' + ', '.join(missing))
sql = (root / 'database/sqlserver/001_schema_operacional.sql').read_text(encoding='utf-8')
for token in ['aprovacao_consulta','historico_conversa','auditoria_consulta','catalogo_alteracao','fila_consulta','ISJSON','SYSUTCDATETIME']:
    if token not in sql:
        raise SystemExit(f'MISSING_SQL_TOKEN {token}')
print('persistence_increment=OK')
