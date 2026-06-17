-- GovBI IA v0.8.0 — Operação assíncrona real, resultados materializados, notificações e retenção.
IF SCHEMA_ID('govbi') IS NULL EXEC('CREATE SCHEMA govbi');
GO

IF OBJECT_ID('govbi.resultado_consulta', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.resultado_consulta (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        fila_id UNIQUEIDENTIFIER NULL,
        aprovacao_id UNIQUEIDENTIFIER NULL,
        correlation_id VARCHAR(80) NOT NULL,
        metrica VARCHAR(160) NOT NULL,
        colunas_json NVARCHAR(MAX) NOT NULL CHECK (ISJSON(colunas_json) = 1),
        linhas_json NVARCHAR(MAX) NOT NULL CHECK (ISJSON(linhas_json) = 1),
        total_linhas INT NOT NULL DEFAULT 0,
        criado_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME(),
        expira_em DATETIME2(3) NULL,
        status_retencao VARCHAR(30) NOT NULL DEFAULT 'ATIVO' CHECK (status_retencao IN ('ATIVO','EXPIRADO','BLOQUEADO_RETENCAO')),
        mensagem NVARCHAR(1000) NULL
    );
END;
GO

IF OBJECT_ID('govbi.notificacao_operacional', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.notificacao_operacional (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        tipo VARCHAR(80) NOT NULL,
        canal VARCHAR(40) NOT NULL,
        destinatario VARCHAR(320) NOT NULL,
        titulo NVARCHAR(300) NOT NULL,
        mensagem NVARCHAR(2000) NOT NULL,
        metadados_json NVARCHAR(MAX) NOT NULL CHECK (ISJSON(metadados_json) = 1),
        status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE','ENVIADA','FALHA','CANCELADA')),
        criada_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME(),
        enviada_em DATETIME2(3) NULL,
        erro NVARCHAR(1000) NULL
    );
END;
GO

CREATE INDEX IX_govbi_resultado_consulta_aprovacao ON govbi.resultado_consulta(aprovacao_id, criado_em DESC);
CREATE INDEX IX_govbi_resultado_consulta_retencao ON govbi.resultado_consulta(status_retencao, expira_em);
CREATE INDEX IX_govbi_notificacao_operacional_status ON govbi.notificacao_operacional(status, criada_em DESC);
GO
