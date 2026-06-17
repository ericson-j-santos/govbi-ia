# Download controlado de resultados aprovados

Endpoint:

```http
GET /api/v1/downloads/resultados/{resultadoId}?formato=csv
GET /api/v1/downloads/resultados/{resultadoId}?formato=json
```

Controles:

- somente resultados com retenção ativa;
- limite de linhas;
- saída já mascarada;
- filename com `correlation_id`;
- autorização por perfil analítico;
- auditoria por `correlation_id`.
