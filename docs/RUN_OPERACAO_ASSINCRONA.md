# Execução — Operação assíncrona v0.8.0

## Configuração mínima local

```bash
export GOVBI_WORKER_HABILITADO=true
export GOVBI_WORKER_INTERVALO_MS=30000
export GOVBI_WORKER_LOTE=5
export GOVBI_RESULTADO_RETENCAO_DIAS=30
export GOVBI_SLA_EXPIRAR_APROVACOES_HABILITADO=true
```

## Fluxo operacional

1. Usuário faz pergunta sensível.
2. GovBI IA cria aprovação `PENDENTE`.
3. Aprovador decide `APROVADA`.
4. Operador chama reprocessamento.
5. Worker processa fila.
6. Resultado é persistido em `/api/v1/resultados`.
7. Notificação operacional é registrada.
8. SLA expira aprovações vencidas e resultados antigos.

## Comandos úteis

```bash
curl -X POST http://localhost:8080/api/v1/fila-consultas/processar-pendentes?limite=5
curl -X GET  http://localhost:8080/api/v1/resultados/recentes
curl -X GET  http://localhost:8080/api/v1/notificacoes/recentes
curl -X POST http://localhost:8080/api/v1/sla/executar-expiracao
```
