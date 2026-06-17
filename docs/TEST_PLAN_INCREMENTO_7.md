# Plano de testes — Incremento 7

## Testes obrigatórios

1. `GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=memoria` deve continuar subindo sem SQL Server.
2. `GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver` deve exigir URL e usuário.
3. Aprovação pendente deve ser persistida em `govbi.aprovacao_consulta`.
4. Histórico deve ser persistido em `govbi.historico_conversa`.
5. Auditoria deve ser persistida em `govbi.auditoria_consulta`.
6. Proposta de catálogo deve ser persistida em `govbi.catalogo_alteracao`.
7. Aprovação aprovada deve poder gerar item em `govbi.fila_consulta`.
8. Item de fila deve transitar por `PENDENTE → EM_PROCESSAMENTO → CONCLUIDA` ou `FALHA`.
9. Nenhum log ou tabela deve armazenar pergunta bruta quando o campo exige hash.
10. Usuário operacional não deve possuir permissão DDL.
