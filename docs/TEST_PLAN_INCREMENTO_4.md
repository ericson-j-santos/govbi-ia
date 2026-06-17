# Plano de testes — Incremento 4

## Unitários

- `CatalogoSemanticoYamlAdapterTest`: carrega catálogo e recupera contexto.
- `PoliticaAcessoRbacRlsAdapterTest`: valida RBAC e injeta RLS.
- `UsuarioContextoFactoryTest`: resolve headers e JWT.
- `ClienteLlmOpenAiAdapterContractTest`: valida contrato esperado do JSON do LLM.

## Integração

- `PerguntaAnaliticaControllerSecurityTest`: valida endpoint com OIDC desabilitado e headers simulados.
- `OpenApiContractTest`: valida existência do contrato OpenAPI estático.

## E2E manual

```bash
curl -X POST http://localhost:8080/api/v1/perguntas   -H 'Content-Type: application/json'   -H 'X-Usuario: analista.demo'   -H 'X-Perfil: ANALISTA'   -H 'X-Escopo-Unidade: SR001'   -d '{"pergunta":"Mostre propostas cadastradas por mês em 2025 por situação","formatoResposta":"tabela_grafico","exibirSql":true}'
```

Critério: SQL retornado deve conter filtro RLS por `u.codigo_unidade = 'SR001'`.
