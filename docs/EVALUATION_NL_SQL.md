# Avaliação NL→SQL

## Objetivo
Medir se a pergunta em linguagem natural está sendo convertida para plano e SQL governados sem regressão.

## Dataset

```text
backend/src/main/resources/evaluation/golden-dataset.json
```

Cada caso define:

- pergunta;
- métrica esperada;
- dimensões esperadas;
- trechos obrigatórios no SQL;
- termos proibidos;
- casos de bloqueio esperados.

## Execução offline

Valida contrato do dataset, sem subir backend.

```bash
python scripts/evaluate_nl_sql.py --offline
```

## Execução online

Com backend ativo:

```bash
python scripts/evaluate_nl_sql.py --base-url http://localhost:8080
```

## Critério de aprovação

- 100% dos casos obrigatórios devem passar.
- Casos de PII precisam bloquear.
- SQL não pode conter DDL/DML, `SELECT *`, campos PII ou objetos fora da allowlist.
- Mudança em métrica governada exige atualização explícita do golden dataset.
