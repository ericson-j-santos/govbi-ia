# Aceite formal — GovBI IA v1.0.1

## Identificação
- Produto: GovBI IA — BI Conversacional Governado.
- Versão: 1.0.1.
- Tipo: Homologação operacional.
- Data-base: 2026-06-14.

## Aprovadores recomendados
| Papel | Responsabilidade | Aceite |
|---|---|---|
| Dono de Dados | Validar métricas e catálogo semântico | Pendente |
| Segurança/LGPD | Validar PII, aprovação humana, auditoria e retenção | Pendente |
| Arquitetura | Validar arquitetura, integração e deploy | Pendente |
| Operação | Validar observabilidade, runbook e rollback | Pendente |
| Área usuária | Validar perguntas, relatórios e usabilidade | Pendente |

## Checklist de aceite
- [ ] Smoke tests executados.
- [ ] Readiness consultado.
- [ ] OIDC validado.
- [ ] RBAC/RLS validado.
- [ ] Consulta não sensível validada.
- [ ] Consulta sensível entrou em aprovação.
- [ ] Aprovação humana validada.
- [ ] Fila/worker validado.
- [ ] Resultado persistido validado.
- [ ] Download controlado validado.
- [ ] Auditoria consultável validada.
- [ ] Prometheus/health validado.
- [ ] Rollback validado.

## Decisão final
- [ ] Aprovado para produção assistida.
- [ ] Aprovado com ressalvas.
- [ ] Reprovado.

## Observações
Registrar evidências, prints, `correlation_id`, horário, usuário e ambiente.
