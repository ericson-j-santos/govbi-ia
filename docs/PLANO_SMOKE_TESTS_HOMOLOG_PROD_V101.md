# Plano de smoke tests — Homologação/Produção v1.0.1

## Testes essenciais
| ID | Teste | Endpoint | Resultado esperado |
|---|---|---|---|
| SMK-001 | Health | `/actuator/health` | UP |
| SMK-002 | Readiness | `/api/v1/release/readiness` | Sem bloqueio crítico |
| SMK-003 | Consulta governada | `/api/v1/perguntas` | Resultado mascarado/auditado |
| SMK-004 | Consulta sensível | `/api/v1/perguntas` | Pendente de aprovação |
| SMK-005 | Aprovações | `/api/v1/aprovacoes/pendentes` | Lista acessível |
| SMK-006 | Auditoria | `/api/v1/auditoria/recentes` | Eventos recentes |
| SMK-007 | Prometheus | `/actuator/prometheus` | Métricas expostas |
| SMK-008 | DLQ | `/api/v1/dlq-consultas/recentes` | Sem erro de API |

## Regras
- Em produção, smoke test não deve criar carga pesada.
- Consultas devem usar filtros temporais.
- Evidenciar `correlation_id` sempre que possível.
