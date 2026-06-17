# Homologação GovBI IA v1.0.1

## Objetivo
Validar o GovBI IA em ambiente corporativo antes da promoção para produção.

## Escopo de homologação
1. Autenticação OIDC com usuário real.
2. Mapeamento de claims de perfil e unidade.
3. Catálogo semântico YAML v1.0.1.
4. Persistência operacional SQL Server.
5. Executor de dados em modo controlado.
6. Consulta analítica não sensível.
7. Consulta sensível com aprovação humana.
8. Fila assíncrona e worker.
9. DLQ e reprocessamento controlado.
10. Download CSV/JSON auditado.
11. Observabilidade via Actuator/Prometheus.
12. Readiness de release.

## Critérios mínimos de aceite
- `quality_check=OK`.
- `catalog_version=1.0.1`.
- `homologation_v101=OK`.
- `smoke_homologacao=OK`.
- Todos os casos golden offline aprovados.
- Readiness sem bloqueios críticos.
- Evidências anexadas ao dossiê de aceite.
