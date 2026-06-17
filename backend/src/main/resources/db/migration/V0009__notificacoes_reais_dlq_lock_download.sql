-- GovBI IA v0.9.0 — notificações reais, DLQ, lock distribuído e download controlado.
IF SCHEMA_ID('govbi') IS NULL EXEC('CREATE SCHEMA govbi');
GO

IF OBJECT_ID('govbi.dlq_consulta', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.dlq_consulta (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        fila_id UNIQUEIDENTIFIER NOT NULL,
        aprovacao_id UNIQUEIDENTIFIER NULL,
        correlation_id VARCHAR(80) NOT NULL,
        metrica VARCHAR(160) NULL,
        motivo_falha NVARCHAR(2000) NOT NULL,
        stack_sanitizado NVARCHAR(1000) NULL,
        payload_json NVARCHAR(MAX) NOT NULL CHECK (ISJSON(payload_json) = 1),
        tentativas_originais INT NOT NULL DEFAULT 0,
        tentativas_reprocessamento INT NOT NULL DEFAULT 0,
        status VARCHAR(40) NOT NULL DEFAULT 'ABERTA' CHECK (status IN ('ABERTA','REPROCESSAMENTO_SOLICITADO','REPROCESSADA','ENCERRADA')),
        criado_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME(),
        atualizado_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME()
    );
END;
GO

IF OBJECT_ID('govbi.lock_distribuido', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.lock_distribuido (
        chave VARCHAR(200) NOT NULL PRIMARY KEY,
        dono VARCHAR(200) NOT NULL,
        adquirido_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME(),
        expira_em DATETIME2(3) NOT NULL
    );
END;
GO

IF OBJECT_ID('govbi.download_resultado', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.download_resultado (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        resultado_id UNIQUEIDENTIFIER NOT NULL,
        correlation_id VARCHAR(80) NOT NULL,
        usuario_hash CHAR(64) NOT NULL,
        formato VARCHAR(20) NOT NULL,
        total_linhas INT NOT NULL DEFAULT 0,
        mascarado BIT NOT NULL DEFAULT 1,
        gerado_em DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME()
    );
END;
GO

CREATE INDEX IX_govbi_dlq_consulta_status ON govbi.dlq_consulta(status, criado_em DESC);
CREATE INDEX IX_govbi_lock_distribuido_expira ON govbi.lock_distribuido(expira_em);
CREATE INDEX IX_govbi_download_resultado_correlation ON govbi.download_resultado(correlation_id, gerado_em DESC);
GO
