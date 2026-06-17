# GovBI IA — BI Conversacional Governado v1.0.1

## Status
Pacote de homologação operacional v1.0.1, baseado na release candidate corporativa v1.0.0.

O GovBI IA é uma plataforma de BI conversacional com IA para transformar perguntas em linguagem natural em consultas, relatórios e insights auditáveis sobre bases corporativas. A solução usa arquitetura hexagonal, catálogo semântico, RAG, validação SQL, RBAC/RLS, aprovação humana, execução read-only, observabilidade, operação assíncrona, DLQ, notificações e download controlado.

## Decisão arquitetural

A IA não acessa dados diretamente. O fluxo de produção é:

```text
usuário → API → identidade/RBAC/RLS → catálogo semântico → RAG/LLM → plano governado → SQL validado → dry-run → executor read-only → resultado mascarado → auditoria
```

## Escopo consolidado v1.0.1

- Chat analítico por linguagem natural.
- Catálogo semântico YAML versionado.
- LLM/RAG substituível: mock, OpenAI ou Azure OpenAI.
- Executor de dados: mock, SQL Server ou Databricks.
- Persistência operacional SQL Server.
- Aprovação humana para PII e consultas sensíveis.
- Histórico, auditoria consultável e exportação controlada.
- Fila assíncrona, worker, lock distribuído e DLQ.
- Resultado persistido com retenção.
- Notificações por log, Teams Webhook e SMTP.
- Download controlado CSV/JSON.
- Observabilidade com Actuator, Prometheus, Grafana e OTLP.
- Checklist produção, matriz de rastreabilidade e readiness de release.

## Execução local

```bash
cd backend
mvn test
mvn spring-boot:run
```

## Endpoints principais

- `POST /api/v1/perguntas`
- `GET /api/v1/release/readiness`
- `GET /api/v1/aprovacoes/pendentes`
- `POST /api/v1/aprovacoes/{id}/decisao`
- `POST /api/v1/fila-consultas/processar-pendentes`
- `GET /api/v1/resultados/recentes`
- `GET /api/v1/dlq-consultas/recentes`
- `GET /api/v1/downloads/resultados/{resultadoId}?formato=csv`
- `GET /api/v1/auditoria/recentes`
- `GET /actuator/prometheus`

## Homologação

```bash
export SPRING_PROFILES_ACTIVE=hom
export GOVBI_OIDC_HABILITADO=true
export GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver
export GOVBI_DADOS_EXECUTOR=sqlserver
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=false
export GOVBI_LOCK_TIPO=sqlserver
```

## Produção

```bash
export SPRING_PROFILES_ACTIVE=prod
export GOVBI_OIDC_HABILITADO=true
export GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver
export GOVBI_DADOS_EXECUTOR=sqlserver
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true
export GOVBI_LOCK_TIPO=sqlserver
export GOVBI_WORKER_HABILITADO=true
```

## Quality gate

```bash
python scripts/quality_check.py
python scripts/validate_catalog_version.py
python scripts/evaluate_nl_sql.py --offline
python scripts/validate_release_v100.py
python scripts/e2e_contract_v100.py --offline
python scripts/validate_homologation_v101.py
python scripts/run_homologation_smoke.py --modo offline
python scripts/generate_aceite_report.py --ambiente hom
```

## Documentos principais

- `docs/DOCUMENTACAO_EXECUTIVA_V100.md`
- `docs/CHECKLIST_PRODUCAO_V100.md`
- `docs/MATRIZ_RASTREABILIDADE_V100.md`
- `docs/GUIA_IMPLANTACAO_HOMOLOG_PROD_V100.md`
- `docs/HARDENING_SEGURANCA_V100.md`
- `docs/RELEASE_NOTES_V100.md`
- `release/homologacao-manifest-v1.0.1.json`


## Homologação operacional v1.0.1

Comandos principais:

```bash
python scripts/validate_homologation_v101.py
python scripts/run_homologation_smoke.py --modo offline
python scripts/generate_aceite_report.py --ambiente hom
```

Documentos principais:

- `docs/HOMOLOGACAO_V101.md`
- `docs/ACEITE_FORMAL_V101.md`
- `docs/RUNBOOK_GO_LIVE_V101.md`
- `docs/PLANO_ROLLBACK_V101.md`
- `docs/MATRIZ_CONFIG_AMBIENTES_V101.md`
- `release/homologacao-manifest-v1.0.1.json`
