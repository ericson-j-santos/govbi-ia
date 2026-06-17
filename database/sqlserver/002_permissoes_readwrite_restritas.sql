-- Ajuste os nomes de usuários/roles conforme seu AD/AAD/SQL Login corporativo.
-- A aplicação não precisa de DDL em runtime.

CREATE ROLE govbi_operacional_app;
GO

GRANT SELECT, INSERT, UPDATE ON govbi.aprovacao_consulta TO govbi_operacional_app;
GRANT SELECT, INSERT        ON govbi.historico_conversa TO govbi_operacional_app;
GRANT SELECT, INSERT        ON govbi.auditoria_consulta TO govbi_operacional_app;
GRANT SELECT, INSERT        ON govbi.catalogo_alteracao TO govbi_operacional_app;
GRANT SELECT, INSERT, UPDATE ON govbi.fila_consulta TO govbi_operacional_app;
GO

-- Exemplo:
-- ALTER ROLE govbi_operacional_app ADD MEMBER [govbi_readwrite_operacional];


-- v0.8.0: permissões operacionais adicionais
GRANT SELECT, INSERT, UPDATE ON govbi.resultado_consulta TO govbi_operacional_app;
GRANT SELECT, INSERT, UPDATE ON govbi.notificacao_operacional TO govbi_operacional_app;

-- v0.9.0 permissões operacionais adicionais
GRANT SELECT, INSERT, UPDATE ON govbi.dlq_consulta TO govbi_operacional_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON govbi.lock_distribuido TO govbi_operacional_app;
GRANT SELECT, INSERT ON govbi.download_resultado TO govbi_operacional_app;
GO
