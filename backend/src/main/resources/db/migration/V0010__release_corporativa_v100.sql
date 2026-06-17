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
