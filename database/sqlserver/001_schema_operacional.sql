-- GovBI IA v0.7.0 - Persistência operacional SQL Server
-- Execute com usuário DDL controlado. A aplicação deve usar somente usuário operacional com SELECT/INSERT/UPDATE restrito.

IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'govbi')
    EXEC('CREATE SCHEMA govbi');
GO

IF OBJECT_ID('govbi.aprovacao_consulta', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.aprovacao_consulta (
        id                  varchar(64)   NOT NULL PRIMARY KEY,
        correlation_id      varchar(64)   NOT NULL,
        usuario_solicitante varchar(256)  NOT NULL,
        perfil_solicitante  varchar(128)  NOT NULL,
        escopo_unidade      varchar(128)  NOT NULL,
        pergunta_hash       char(64)      NOT NULL,
        metrica             varchar(128)  NOT NULL,
        nivel_sensibilidade varchar(64)   NOT NULL,
        motivos_json        nvarchar(max) NOT NULL,
        filtros_json        nvarchar(max) NOT NULL,
        status              varchar(32)   NOT NULL,
        criada_em           datetime2(3)  NOT NULL CONSTRAINT df_aprovacao_criada DEFAULT SYSUTCDATETIME(),
        expira_em           datetime2(3)  NOT NULL,
        decisor             varchar(256)  NULL,
        decidida_em         datetime2(3)  NULL,
        justificativa       nvarchar(2000) NULL,
        CONSTRAINT ck_aprovacao_status CHECK (status IN ('PENDENTE','APROVADA','REJEITADA','EXPIRADA')),
        CONSTRAINT ck_aprovacao_json CHECK (ISJSON(motivos_json)=1 AND ISJSON(filtros_json)=1)
    );
END
GO

IF OBJECT_ID('govbi.historico_conversa', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.historico_conversa (
        id             varchar(64)   NOT NULL PRIMARY KEY,
        correlation_id varchar(64)   NOT NULL,
        usuario_hash   char(64)      NOT NULL,
        pergunta_hash  char(64)      NOT NULL,
        metrica        varchar(128)  NULL,
        dimensoes_json nvarchar(max) NOT NULL,
        filtros_json   nvarchar(max) NOT NULL,
        status         varchar(64)   NOT NULL,
        aprovacao_id   varchar(64)   NULL,
        total_linhas   int           NOT NULL CONSTRAINT df_historico_total_linhas DEFAULT 0,
        registrado_em  datetime2(3)  NOT NULL CONSTRAINT df_historico_registrado DEFAULT SYSUTCDATETIME(),
        CONSTRAINT ck_historico_json CHECK (ISJSON(dimensoes_json)=1 AND ISJSON(filtros_json)=1)
    );
END
GO

IF OBJECT_ID('govbi.auditoria_consulta', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.auditoria_consulta (
        id             varchar(64)   NOT NULL PRIMARY KEY,
        correlation_id varchar(64)   NOT NULL,
        tipo_evento    varchar(64)   NOT NULL,
        usuario_hash   char(64)      NOT NULL,
        perfil         varchar(128)  NOT NULL,
        escopo_unidade varchar(128)  NOT NULL,
        metrica        varchar(128)  NULL,
        sql_hash       char(64)      NULL,
        linhas         int           NOT NULL,
        colunas_json   nvarchar(max) NOT NULL,
        status         varchar(64)   NOT NULL,
        registrado_em  datetime2(3)  NOT NULL CONSTRAINT df_auditoria_registrado DEFAULT SYSUTCDATETIME(),
        CONSTRAINT ck_auditoria_json CHECK (ISJSON(colunas_json)=1)
    );
END
GO

IF OBJECT_ID('govbi.catalogo_alteracao', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.catalogo_alteracao (
        id             varchar(64)    NOT NULL PRIMARY KEY,
        usuario        varchar(256)   NOT NULL,
        descricao      nvarchar(2000) NOT NULL,
        status         varchar(80)    NOT NULL,
        diff_resumo    nvarchar(2000) NOT NULL,
        novo_yaml_hash varchar(128)   NOT NULL,
        registrada_em  datetime2(3)   NOT NULL CONSTRAINT df_catalogo_alt_registrada DEFAULT SYSUTCDATETIME()
    );
END
GO

IF OBJECT_ID('govbi.fila_consulta', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.fila_consulta (
        id                  varchar(64)   NOT NULL PRIMARY KEY,
        tipo                varchar(80)   NOT NULL,
        correlation_id      varchar(64)   NOT NULL,
        aprovacao_id        varchar(64)   NULL,
        usuario_solicitante varchar(256)  NOT NULL,
        solicitado_por      varchar(256)  NOT NULL,
        metrica             varchar(128)  NULL,
        payload_json        nvarchar(max) NOT NULL,
        status              varchar(32)   NOT NULL,
        tentativas          int           NOT NULL CONSTRAINT df_fila_tentativas DEFAULT 0,
        criado_em           datetime2(3)  NOT NULL CONSTRAINT df_fila_criado DEFAULT SYSUTCDATETIME(),
        atualizado_em       datetime2(3)  NOT NULL CONSTRAINT df_fila_atualizado DEFAULT SYSUTCDATETIME(),
        mensagem            nvarchar(2000) NULL,
        CONSTRAINT ck_fila_status CHECK (status IN ('PENDENTE','EM_PROCESSAMENTO','CONCLUIDA','FALHA','CANCELADA')),
        CONSTRAINT ck_fila_payload_json CHECK (ISJSON(payload_json)=1)
    );
END
GO

CREATE INDEX ix_aprovacao_status_criada ON govbi.aprovacao_consulta(status, criada_em DESC);
CREATE INDEX ix_historico_usuario_registrado ON govbi.historico_conversa(usuario_hash, registrado_em DESC);
CREATE INDEX ix_historico_correlation ON govbi.historico_conversa(correlation_id);
CREATE INDEX ix_auditoria_correlation ON govbi.auditoria_consulta(correlation_id, registrado_em DESC);
CREATE INDEX ix_fila_status_criado ON govbi.fila_consulta(status, criado_em ASC);
GO


-- v0.8.0
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

-- v0.9.0
-- Notificações reais, DLQ, lock distribuído e download controlado.
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


-- v1.0.0 release corporativa
/*
  GovBI IA v1.0.0 — metadados de release corporativa.
  Não altera dados analíticos. Registra readiness, checklist e rastreabilidade operacional.
*/
IF SCHEMA_ID('govbi') IS NULL
    EXEC('CREATE SCHEMA govbi');
GO

IF OBJECT_ID('govbi.release_corporativa', 'U') IS NULL
BEGIN
    CREATE TABLE govbi.release_corporativa (
        id_release UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        versao VARCHAR(20) NOT NULL,
        status VARCHAR(40) NOT NULL,
        checklist_json NVARCHAR(MAX) NOT NULL,
        matriz_rastreabilidade_json NVARCHAR(MAX) NOT NULL,
        criado_em_utc DATETIME2(3) NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT pk_release_corporativa PRIMARY KEY (id_release),
        CONSTRAINT ck_release_corporativa_checklist_json CHECK (ISJSON(checklist_json) = 1),
        CONSTRAINT ck_release_corporativa_matriz_json CHECK (ISJSON(matriz_rastreabilidade_json) = 1)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM govbi.release_corporativa WHERE versao = '1.0.0')
BEGIN
    INSERT INTO govbi.release_corporativa (versao, status, checklist_json, matriz_rastreabilidade_json)
    VALUES ('1.0.0', 'RELEASE_CANDIDATE', N'{"gates":["quality_check","catalog_version","nl_sql_evaluation","release_v100","e2e_contract"]}', N'{"requisitos":["REQ-GOVBI-001","REQ-GOVBI-002","REQ-GOVBI-003","REQ-GOVBI-004","REQ-GOVBI-005"]}');
END;
GO
