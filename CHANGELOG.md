# Changelog

## [1.0.1] - 2026-06-14

### Adicionado
- Pacote formal de homologação operacional.
- Documentos `HOMOLOGACAO_V101`, `ACEITE_FORMAL_V101`, `RUNBOOK_GO_LIVE_V101`, `PLANO_ROLLBACK_V101` e matriz de configuração por ambiente.
- Scripts `validate_homologation_v101.py`, `run_homologation_smoke.py` e `generate_aceite_report.py`.
- Templates `.env` para homologação e produção.
- Manifesto `release/homologacao-manifest-v1.0.1.json`.
- Smoke queries SQL Server para verificação operacional.
- OpenAPI `govbi-ia-v1.0.1.yaml`.

### Alterado
- Versão Maven, aplicação, catálogo e golden dataset para `1.0.1`.
- Modo padrão de release para `homologacao`.
- Quality gate passa a validar o pacote de homologação.

### Segurança
- Promoção para produção condicionada a readiness, aceite formal, evidências e rollback aprovado.


## [1.0.0] - 2026-06-14

### Adicionado
- Consolidação release candidate corporativo v1.0.0.
- `ReleaseReadinessController` com endpoint `/api/v1/release/readiness`.
- Contrato de catálogo semântico atualizado para `1.0.0`.
- Golden dataset NL→SQL atualizado para `1.0.0` com seis casos mínimos.
- Documentação executiva v1.0.0.
- Checklist de produção.
- Matriz de rastreabilidade.
- Guia de implantação homologação/produção.
- Hardening de segurança.
- Plano E2E v1.0.0.
- Release notes v1.0.0.
- Manifesto de release `release/release-manifest-v1.0.0.json`.
- Migration `V0010__release_corporativa_v100.sql`.
- Scripts `validate_release_v100.py` e `e2e_contract_v100.py`.
- Perfis `application-hom.yml` e `application-prod.yml`.

### Alterado
- Versão Maven para `1.0.0`.
- Configuração `GOVBI_VERSAO` padrão para `1.0.0`.
- Pipeline quality gate inclui validação de release v1.0.0.
- Demo HTML consolidada como visão executiva e operacional v1.0.0.

### Segurança
- Checklist formaliza OIDC obrigatório em produção.
- Checklist formaliza persistência operacional SQL Server em produção.
- Checklist formaliza lock distribuído, DLQ, retenção, download auditado e observabilidade.

## [0.9.0] - 2026-06-14

### Adicionado
- Adapters reais de notificação: Teams Webhook e e-mail SMTP.
- Templates de notificação por tipo/canal.
- Lock distribuído para worker assíncrono.
- DLQ consultável para falhas definitivas.
- Download controlado de resultados aprovados em CSV/JSON.
- Tela Angular operacional v0.9.0.
- Migration `V0009__notificacoes_reais_dlq_lock_download.sql`.

## [0.8.0] - 2026-06-14
- Worker assíncrono, resultado persistido, notificações, SLA e retenção.

## [0.7.0] - 2026-06-14
- Persistência operacional SQL Server, fila e reprocessamento.

## [0.6.0] - 2026-06-14
- Aprovação humana, histórico, auditoria consultável, exportação e deploy.

## [0.5.0] - 2026-06-14
- Observabilidade, golden dataset, avaliação NL→SQL e quality gate.

## [0.4.0] - 2026-06-14
- OIDC/JWT, RBAC/RLS, catálogo YAML, OpenAPI e adapters LLM reais.

## [0.3.0] - 2026-06-14
- Adapters reais SQL Server e Databricks.

## [0.2.0] - 2026-06-14
- Motor LLM/RAG governado, validação iterativa e dry-run.

## [0.1.0] - 2026-06-14
- MVP inicial.
