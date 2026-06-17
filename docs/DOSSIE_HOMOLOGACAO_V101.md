# Dossiê de homologação — GovBI IA v1.0.1

## Conteúdo mínimo
- Manifesto de homologação.
- Resultado do quality gate.
- Resultado do smoke test.
- Relatório de aceite.
- Evidências de OIDC/RBAC/RLS.
- Evidências de aprovação humana.
- Evidências de auditoria e download.
- Evidências de observabilidade.
- Plano de rollback assinado.

## Evidências recomendadas
| Evidência | Obrigatória | Observação |
|---|---:|---|
| `reports/homologacao/aceite-v1.0.1.json` | Sim | Gerado por script |
| Print do readiness | Sim | Sem bloqueios críticos |
| Correlation IDs de testes | Sim | Um por cenário crítico |
| Log de smoke test | Sim | Sem PII bruta |
| Checklist assinado | Sim | Área, segurança e operação |
