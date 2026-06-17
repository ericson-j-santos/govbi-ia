# Execução com Databricks

## Pré-requisitos

- SQL Warehouse ativo.
- Token ou credencial corporativa conforme padrão da organização.
- Driver JDBC Databricks disponível no classpath.
- Tabelas/views Gold registradas no catálogo usado pela URL JDBC.

## Variáveis

```bash
export GOVBI_DADOS_EXECUTOR=databricks
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true
export GOVBI_DATABRICKS_URL='jdbc:databricks://host:443/default;transportMode=http;ssl=1;httpPath=/sql/1.0/warehouses/xxx'
export GOVBI_DATABRICKS_USER='token'
export GOVBI_DATABRICKS_TOKEN='***'
```

## Execução

```bash
cd backend
mvn spring-boot:run
```

## Observações

- O adapter executa dry-run com `LIMIT 1`.
- A execução final também recebe limite via JDBC `setMaxRows`.
- O schema Gold precisa estar refletido no catálogo semântico do GovBI IA.
- Para produção, recomenda-se políticas Unity Catalog, grants mínimos e auditoria centralizada.
