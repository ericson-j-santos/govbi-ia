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
