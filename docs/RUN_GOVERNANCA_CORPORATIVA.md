# Execução com governança corporativa — GovBI IA v0.4.0

## Modo local seguro

```bash
cd backend
mvn spring-boot:run
```

Por padrão:

- OIDC desabilitado;
- executor `mock`;
- LLM `mock-rag`;
- catálogo YAML;
- RLS ativo para escopo diferente de `GERAL`.

## Habilitar OIDC/JWT

```bash
export GOVBI_OIDC_HABILITADO=true
export GOVBI_OIDC_ISSUER_URI='https://login.exemplo.gov.br/realms/corporativo'
export GOVBI_OIDC_CLAIM_USUARIO='preferred_username'
export GOVBI_OIDC_CLAIM_PERFIS='roles'
export GOVBI_OIDC_CLAIM_UNIDADE='escopo_unidade'
```

Em produção, remova headers simulados e envie `Authorization: Bearer <token>`.

## RLS por unidade

Quando `X-Escopo-Unidade` ou claim `escopo_unidade` for diferente de `GERAL`, o backend injeta:

```sql
AND u.codigo_unidade = '<escopo>'
```

antes da validação SQL e do dry-run.

## LLM real — OpenAI compatível

```bash
export GOVBI_IA_MODO=openai
export GOVBI_OPENAI_API_KEY='***'
export GOVBI_OPENAI_MODEL='gpt-4.1-mini'
```

O adapter real só retorna plano JSON. O SQL continua sendo composto pelo motor governado.

## Azure OpenAI

```bash
export GOVBI_IA_MODO=azure-openai
export GOVBI_AZURE_OPENAI_ENDPOINT='https://seu-recurso.openai.azure.com'
export GOVBI_AZURE_OPENAI_DEPLOYMENT='govbi-planejador'
export GOVBI_AZURE_OPENAI_API_KEY='***'
```

## OpenAPI

- Swagger UI: `/swagger-ui.html`
- JSON gerado: `/v3/api-docs`
- Contrato estático: `docs/openapi/govbi-ia-v0.4.0.yaml`
