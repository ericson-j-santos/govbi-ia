# Auditoria consultável

A auditoria operacional expõe eventos sem PII bruta:

- usuário em hash;
- SQL em hash;
- status;
- métrica;
- perfil;
- escopo;
- linhas e colunas.

Endpoints:

- `GET /api/v1/auditoria/recentes`
- `GET /api/v1/auditoria/{correlationId}`
