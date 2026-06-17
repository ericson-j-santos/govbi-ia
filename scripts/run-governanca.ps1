$env:GOVBI_CATALOGO_TIPO="yaml"
$env:GOVBI_IA_MODO="mock-rag"
$env:GOVBI_DADOS_EXECUTOR="mock"
$env:GOVBI_OIDC_HABILITADO="false"
Set-Location backend
mvn spring-boot:run
