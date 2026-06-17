$env:GOVBI_DADOS_EXECUTOR = "sqlserver"
$env:GOVBI_DADOS_PERMITIR_EXECUCAO_REAL = "true"
$env:GOVBI_SQLSERVER_URL = "jdbc:sqlserver://servidor:1433;databaseName=DW_GOVBI;encrypt=true;trustServerCertificate=false"
$env:GOVBI_SQLSERVER_USER = "govbi_readonly"
$env:GOVBI_SQLSERVER_PASSWORD = "***"

Set-Location backend
mvn spring-boot:run
