# DLQ e reprocessamento seguro

A DLQ recebe itens que ultrapassaram o limite de tentativas do worker. Cada registro contém:

- `fila_id`;
- `aprovacao_id`;
- `correlation_id`;
- métrica;
- motivo de falha sanitizado;
- payload JSON;
- tentativas originais e tentativas de reprocessamento.

Endpoints:

```http
GET  /api/v1/dlq-consultas/recentes
GET  /api/v1/dlq-consultas/{id}
POST /api/v1/dlq-consultas/{id}/marcar-reprocessamento-solicitado
```

Regra operacional: itens de DLQ não são reexecutados automaticamente sem decisão operacional explícita.
