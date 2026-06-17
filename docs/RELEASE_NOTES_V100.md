# Release notes — GovBI IA v1.0.0

## Destaques
- Primeira versão consolidada como release candidate corporativo.
- Núcleo NL→SQL governado estabilizado.
- Contrato semântico versionado em v1.0.0.
- Operação assíncrona com fila, worker, lock, DLQ e retenção.
- Notificações Teams/e-mail e download controlado.
- Readiness de release exposto por API.
- Documentação executiva, checklist produção e matriz de rastreabilidade.

## Compatibilidade
- Mantém modo local em memória para demonstração.
- Mantém executores `mock`, `sqlserver` e `databricks`.
- Mantém IA em `mock-rag`, `openai` e `azure-openai`.
- Em produção, recomenda-se OIDC + SQL Server operacional + lock SQL Server.
