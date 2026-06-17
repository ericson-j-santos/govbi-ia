# Hardening de segurança — GovBI IA v1.0.0

## Controles obrigatórios
- API protegida por OIDC/JWT em homologação e produção.
- Execução real desabilitada por padrão.
- Usuário SQL apenas leitura para dados analíticos.
- Usuário SQL operacional sem DDL em runtime.
- Allowlist de objetos por catálogo semântico.
- Bloqueio de DDL, DML, `SELECT *`, múltiplas instruções e comentários SQL.
- Dry-run antes da execução real.
- Timeout, limite de linhas e limite de custo.
- Mascaramento de PII no JDBC e nos downloads.
- Aprovação humana para consulta sensível.
- Logs com hashes, sem pergunta bruta, SQL completo ou PII.
- Retenção de resultados com expiração automática.
- DLQ sem payload sensível em texto claro.

## Segredos
Devem ficar em cofre corporativo ou variáveis de ambiente seguras:
- tokens LLM;
- senha SQL Server;
- token Databricks;
- webhook Teams;
- credenciais SMTP;
- issuer/JWK do OIDC.
