# ADR-012 — Motor LLM/RAG Governado para BI Conversacional

## Status

Aprovada.

## Contexto

O MVP inicial demonstrava geração de SQL por heurística. Para evoluir para uso corporativo, o motor precisa trabalhar com contexto semântico recuperado, validação iterativa e bloqueio de consultas sensíveis.

O risco principal é permitir que um LLM gere SQL diretamente sobre bases reais, com alucinação de colunas, violação de permissão, consulta cara ou exposição indevida de PII.

## Decisão

Adotar um motor LLM/RAG governado com as seguintes regras:

1. A IA não acessa banco diretamente.
2. A IA recebe apenas contexto semântico recuperado do catálogo.
3. A IA retorna plano estruturado, não execução direta.
4. O SQL gerado é validado contra métrica e allowlist.
5. Todo SQL passa por dry-run antes da execução.
6. Perguntas com listagem individualizada de PII são bloqueadas e exigem aprovação humana.
7. O resultado mantém SQL visível, correlation_id e trilha de auditoria.

## Implementação

Componentes adicionados:

- `CatalogoSemanticoPort.buscarContexto`
- `MotorIaLlmRagAdapter`
- `ClienteLlmPort`
- `ClienteLlmMockAdapter`
- `DryRunConsulta`
- `TentativaGeracaoConsulta`

## Consequências positivas

- Reduz alucinação de schema.
- Limita o espaço de geração do SQL.
- Permite troca segura de LLM.
- Mantém núcleo testável sem dependência externa.
- Permite auditoria de pergunta, contexto, SQL, validação e execução.

## Consequências negativas

- Mais componentes no fluxo.
- O catálogo semântico precisa ser bem mantido.
- Perguntas ambíguas precisam de tratamento incremental.

## Critérios de aceite

- Consulta agregada deve ser executada com SQL visível.
- Consulta com PII individualizada deve ser bloqueada.
- SQL fora da allowlist deve ser bloqueado.
- `SELECT *` deve ser bloqueado.
- Dry-run deve ocorrer antes da execução.
- Resposta deve retornar contexto semântico e tentativas.
