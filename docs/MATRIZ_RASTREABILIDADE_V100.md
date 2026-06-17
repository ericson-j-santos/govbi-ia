# Matriz de rastreabilidade — GovBI IA v1.0.0

| Requisito | Descrição | Implementação | Evidência/Teste |
|---|---|---|---|
| REQ-GOVBI-001 | Consulta por linguagem natural | `PerguntaAnaliticaController`, `ResponderPerguntaAnaliticaUseCase` | `GOVBI-GOLD-001`, `GOVBI-GOLD-002` |
| REQ-GOVBI-002 | Catálogo semântico governado | `CatalogoSemanticoPort`, `CatalogoSemanticoYamlAdapter` | `validate_catalog_version.py` |
| REQ-GOVBI-003 | SQL seguro e read-only | `ValidadorSqlSeguroAdapter`, `ExecutorConsultaPort` | `quality_check.py`, `GOVBI-GOLD-006` |
| REQ-GOVBI-004 | RBAC/RLS | `PoliticaAcessoRbacRlsAdapter` | Testes de política e E2E com escopo de unidade |
| REQ-GOVBI-005 | Aprovação humana para PII | `AprovacaoHumanaPort`, `AprovacaoController` | `GOVBI-GOLD-003` |
| REQ-GOVBI-006 | Auditoria e histórico | `AuditoriaConsultavelPort`, `HistoricoConversacionalPort` | Controllers `/auditoria` e `/historico` |
| REQ-GOVBI-007 | Operação assíncrona | `FilaConsultaWorker`, `FilaConsultaPort` | `validate_async_increment.py` |
| REQ-GOVBI-008 | DLQ e reprocessamento | `DeadLetterConsultaPort` | `validate_integration_increment.py` |
| REQ-GOVBI-009 | Download controlado | `DownloadResultadoPort` | Endpoint `/api/v1/downloads/resultados/{id}` |
| REQ-GOVBI-010 | Observabilidade | `ObservabilidadePort`, Prometheus/Grafana | `/actuator/prometheus` |
| REQ-GOVBI-011 | Readiness release | `ReleaseReadinessController` | `validate_release_v100.py` |
