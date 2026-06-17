# Exportação controlada

A exportação aceita `csv` e `json`, respeitando:

- perfil autorizado;
- limite máximo de linhas;
- resultado já mascarado;
- `correlation_id` no nome do arquivo;
- bloqueio de usuários sem perfil analítico.

Endpoint:

```http
POST /api/v1/exportacoes
```
