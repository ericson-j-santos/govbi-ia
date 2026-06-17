# Plano de Testes — GovBI IA

## Unitários

- `CatalogoSemanticoEmMemoriaAdapterTest`
  - recupera contexto relevante para pergunta.

- `ValidadorSqlSeguroAdapterTest`
  - permite SELECT governado na camada Gold;
  - bloqueia DELETE;
  - bloqueia objeto fora da Gold;
  - bloqueia `SELECT *`;
  - bloqueia múltiplas instruções.

- `ResponderPerguntaAnaliticaUseCaseTest`
  - responde pergunta agregada com RAG, dry-run, SQL e resultado;
  - bloqueia pergunta com listagem individualizada de PII.

## Integração futura

- API `POST /api/v1/perguntas` com contrato OpenAPI.
- Adapter SQL Server com banco de teste.
- Adapter LLM real com respostas mockadas por contrato.
- Teste de autorização por perfil e unidade.

## Não-funcionais futuros

- Teste de timeout.
- Teste de custo máximo.
- Teste de mascaramento real.
- Teste de auditoria JSON.
- Teste de acessibilidade do frontend.
