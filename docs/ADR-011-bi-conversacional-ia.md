# ADR-011 — BI Conversacional Governado com IA sobre fontes heterogêneas

- **Status:** Proposto
- **Data:** 2026-06-14
- **Contexto:** Uso de IA para criação de consultas, relatórios e insights a partir de linguagem natural sobre múltiplas bases corporativas.

## Contexto

A organização possui diversas origens de dados, incluindo bancos relacionais, APIs, arquivos, sistemas legados, camada Gold analítica e possíveis fontes federadas. Usuários de negócio precisam obter relatórios sem depender sempre de desenvolvimento manual de SQL, dashboards ou consultas específicas.

O risco principal é permitir que um LLM gere SQL diretamente contra os bancos, o que pode causar erro lógico, vazamento de dados, consulta cara, acesso indevido, inconsistência de regra de negócio ou alucinação de tabelas e colunas.

## Decisão

Adotar uma **Plataforma de BI Conversacional Governado com IA**, composta por:

1. frontend conversacional;
2. API Java/Spring Boot;
3. motor IA NL→Query;
4. catálogo semântico governado;
5. política de acesso;
6. validador SQL;
7. executor read-only;
8. auditoria;
9. mascaramento LGPD.

A IA não acessa bases diretamente. Ela sempre passa por portas de domínio e validações.

## Consequências positivas

- Redução de dependência de SQL manual para perguntas recorrentes.
- Reuso de métricas governadas.
- Maior rastreabilidade de consultas.
- Menor risco de vazamento de dados.
- Evolução incremental para múltiplas origens.

## Consequências negativas

- Exige manutenção de catálogo semântico.
- Exige testes de qualidade NL→SQL.
- Nem toda pergunta deve ser respondida automaticamente.
- Consultas sensíveis podem exigir aprovação humana.

## Regras inegociáveis

- Toda execução deve ser read-only.
- Toda tabela deve estar em allowlist.
- Toda consulta deve ter `correlation_id`.
- Toda PII deve ser mascarada por padrão.
- Toda métrica crítica deve vir do catálogo semântico.
- Toda consulta cara ou sensível deve ser bloqueada ou enviada para revisão.
