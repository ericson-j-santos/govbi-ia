# Execução com SQL Server

## Pré-requisitos

- Banco SQL Server acessível pela aplicação.
- Usuário técnico somente leitura.
- Views/tabelas Gold alinhadas ao catálogo semântico.
- Driver `mssql-jdbc` disponível via Maven.

## Variáveis

```bash
export GOVBI_DADOS_EXECUTOR=sqlserver
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true
export GOVBI_SQLSERVER_URL='jdbc:sqlserver://servidor:1433;databaseName=DW_GOVBI;encrypt=true;trustServerCertificate=false'
export GOVBI_SQLSERVER_USER='govbi_readonly'
export GOVBI_SQLSERVER_PASSWORD='***'
```

## Execução

```bash
cd backend
mvn spring-boot:run
```

## Validação

```bash
curl -X POST http://localhost:8080/api/v1/perguntas \
  -H 'Content-Type: application/json' \
  -H 'X-Usuario: analista.demo' \
  -H 'X-Perfil: ANALISTA' \
  -H 'X-Escopo-Unidade: GERAL' \
  -d '{
    "pergunta": "Mostre propostas cadastradas por mês em 2025 por situação e unidade",
    "formatoResposta": "tabela_grafico",
    "exibirSql": true
  }'
```

## Recomendações de banco

- Criar schema ou views `gold`.
- Conceder apenas `SELECT`.
- Evitar acesso direto a tabelas transacionais.
- Aplicar RLS no banco quando houver escopo por unidade.
- Criar índices para chaves de join e filtros temporais.
