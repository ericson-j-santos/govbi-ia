# Checklist de produção — GovBI IA v1.0.0

## Identidade e acesso
- [ ] OIDC/JWT habilitado.
- [ ] Claims de usuário, perfis e unidade mapeados.
- [ ] Perfis analíticos e aprovadores revisados.
- [ ] RBAC validado com usuários reais.
- [ ] RLS validado por unidade.

## Dados
- [ ] Executor real definido: SQL Server ou Databricks.
- [ ] Execução real explicitamente habilitada.
- [ ] Usuário de dados é read-only.
- [ ] Tabelas/views da camada Gold revisadas.
- [ ] Dry-run, timeout e limites ativados.

## Operação
- [ ] SQL Server operacional criado.
- [ ] Migrations aplicadas.
- [ ] Worker habilitado apenas após validação.
- [ ] Lock distribuído usando SQL Server.
- [ ] DLQ monitorada.
- [ ] Retenção de resultados definida.

## Segurança/LGPD
- [ ] Mascaramento validado.
- [ ] PII exige aprovação humana.
- [ ] Logs sem pergunta bruta, SQL completo ou PII.
- [ ] Download exige perfil autorizado e auditoria.
- [ ] Segredos fora do repositório.

## Observabilidade
- [ ] `/actuator/health` monitorado.
- [ ] `/actuator/prometheus` coletado.
- [ ] Dashboard Grafana importado.
- [ ] Alertas para erro, DLQ, p95 e bloqueios configurados.

## Qualidade
- [ ] `mvn test` executado.
- [ ] `python scripts/quality_check.py` OK.
- [ ] `python scripts/validate_catalog_version.py` OK.
- [ ] `python scripts/evaluate_nl_sql.py --offline` OK.
- [ ] `python scripts/validate_release_v100.py` OK.
- [ ] E2E em homologação executado.
