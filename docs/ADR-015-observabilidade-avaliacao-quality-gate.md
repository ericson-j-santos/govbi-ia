# ADR-015 — Observabilidade, avaliação NL→SQL e quality gate enterprise

## Status
Aprovada para o Incremento 5 / v0.5.0.

## Contexto
O GovBI IA já possui governança, RBAC/RLS, catálogo YAML, adapters reais de dados e validação SQL. Para operação corporativa, isso ainda não basta: é necessário medir comportamento, detectar regressão de qualidade, registrar bloqueios, acompanhar latência e impedir promoção de mudanças inseguras no pipeline.

## Decisão
Adicionar uma camada operacional com:

1. métricas Micrometer/Prometheus;
2. dashboard Grafana;
3. preparação para tracing distribuído via OTLP;
4. golden dataset de avaliação NL→SQL;
5. scripts de quality gate;
6. workflow CI;
7. versionamento formal do catálogo semântico;
8. testes de contrato e avaliação.

## Métricas mínimas

- `govbi_perguntas_total`: total por `status`, `executor`, `perfil`, `sensibilidade` e `motivo`.
- `govbi_consulta_duracao`: tempo de ponta a ponta da pergunta até a resposta.
- `govbi_resultado_linhas`: distribuição de linhas retornadas por consulta.

## Restrições

- Nenhuma métrica pode expor pergunta bruta, SQL bruto, CPF, e-mail, nome, usuário nominal ou token.
- Tags devem ser de baixa cardinalidade.
- Relatórios de avaliação devem guardar resultado agregado, não dados sensíveis.

## Consequências

- A operação passa a ter indicadores objetivos de adoção, bloqueio, latência e erro.
- Mudanças no catálogo e no motor NL→SQL passam a ser avaliadas por golden dataset.
- O pipeline pode impedir merge/deploy quando a qualidade ou governança regredir.
