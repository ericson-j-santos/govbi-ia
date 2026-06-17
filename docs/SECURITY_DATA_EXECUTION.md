# Segurança — Execução de Dados

## Princípios

1. A IA não executa SQL diretamente.
2. O SQL precisa passar pelo validador.
3. A consulta precisa estar dentro da allowlist semântica.
4. A execução real fica desabilitada por padrão.
5. O usuário de banco deve ser read-only.
6. Todo resultado sensível deve ser mascarado.
7. Toda execução deve possuir `correlation_id`.

## Proteções técnicas

- `govbi.dados.permitir-execucao-real=false` por padrão.
- Bloqueio de DDL/DML.
- Bloqueio de múltiplas instruções.
- Bloqueio de comentários SQL.
- Bloqueio de funções de risco.
- Bloqueio de objetos fora da camada Gold.
- Timeout.
- Limite de linhas.
- Dry-run.
- Sanitização de mensagens de erro.

## Requisitos de produção

- OIDC corporativo.
- RBAC por perfil.
- RLS por unidade.
- Secrets em cofre corporativo.
- Logs JSON sem PII.
- Auditoria imutável.
- Pipeline com testes de contrato e análise estática.
