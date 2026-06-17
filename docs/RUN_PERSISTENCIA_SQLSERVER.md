# Execução com persistência operacional SQL Server

## Variáveis obrigatórias

```bash
export GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver
export GOVBI_OPERACIONAL_SQLSERVER_URL='jdbc:sqlserver://servidor:1433;databaseName=GOVBI_OPERACIONAL;encrypt=true;trustServerCertificate=false'
export GOVBI_OPERACIONAL_SQLSERVER_USER='govbi_operacional_app'
export GOVBI_OPERACIONAL_SQLSERVER_PASSWORD='***'
export GOVBI_OPERACIONAL_SQLSERVER_SCHEMA='govbi'
```

## Preparação do banco

Execute uma vez, com usuário de implantação controlado:

```bash
sqlcmd -S servidor -d GOVBI_OPERACIONAL -i database/sqlserver/001_schema_operacional.sql
sqlcmd -S servidor -d GOVBI_OPERACIONAL -i database/sqlserver/002_permissoes_readwrite_restritas.sql
```

## Execução

```bash
cd backend
mvn test
mvn spring-boot:run
```

## Validação rápida

```bash
curl http://localhost:8080/api/v1/auditoria/recentes
curl http://localhost:8080/api/v1/historico/recentes
curl http://localhost:8080/api/v1/aprovacoes/pendentes
curl http://localhost:8080/api/v1/fila-consultas/pendentes
```
