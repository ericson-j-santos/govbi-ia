# ADR-014 — Governança corporativa completa para BI Conversacional

## Status
Aceita — v0.4.0.

## Contexto
O GovBI IA precisava sair de um MVP governado de execução real para uma base corporativa com identidade, autorização, catálogo versionável, OpenAPI e adapter real de LLM.

## Decisão
Implementar o Incremento 4 com:

1. Catálogo semântico em YAML versionável.
2. OIDC/JWT opcional via Spring Security Resource Server.
3. RBAC por perfis analíticos e aprovadores de PII.
4. RLS por escopo de unidade aplicado no SQL antes da validação.
5. Logs JSON estruturados com hash SHA-256 de pergunta e SQL.
6. OpenAPI 3.0 via springdoc e contrato estático em `docs/openapi`.
7. Adapter real OpenAI/Azure OpenAI compatível, mantendo mock determinístico como padrão.

## Consequências
- A execução local continua simples: OIDC e execução real vêm desabilitados por padrão.
- Produção exige variáveis de ambiente e issuer/JWK corporativo.
- O SQL final passa a refletir RLS quando o escopo da unidade não for `GERAL`.
- O LLM real só gera plano estruturado; SQL segue gerado pelo motor governado.

## Controles obrigatórios
- Nunca registrar pergunta bruta com PII em logs.
- Nunca registrar SQL completo em auditoria operacional; usar hash.
- Não permitir DDL/DML.
- Não executar consulta sensível individual sem aprovação humana.
- Não consultar fora da camada Gold/catálogo semântico.
