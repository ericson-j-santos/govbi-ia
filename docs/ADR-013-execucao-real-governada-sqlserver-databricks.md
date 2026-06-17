# ADR-013 — Execução real governada com SQL Server e Databricks

## Status

Aceita.

## Contexto

O GovBI IA precisava evoluir do executor mockado para execução real sobre bases corporativas, sem violar os controles de governança, LGPD, rastreabilidade e proteção contra SQL inseguro.

## Decisão

Manter a porta única `ExecutorConsultaPort` e adicionar adapters reais substituíveis:

- `ExecutorConsultaSqlServerAdapter`;
- `ExecutorConsultaDatabricksAdapter`;
- `ExecutorConsultaJdbcBaseAdapter` como base comum.

A seleção do executor ocorre por configuração:

```yaml
govbi.dados.executor: mock | sqlserver | databricks
```

A execução real exige ainda:

```yaml
govbi.dados.permitir-execucao-real: true
```

## Controles obrigatórios

- SQL já validado pelo `ValidadorConsultaPort`.
- Allowlist semântica de objetos SQL.
- Somente `SELECT`.
- Bloqueio de DDL/DML.
- Dry-run real antes da execução.
- `Statement.setQueryTimeout`.
- `Statement.setMaxRows`.
- `Statement.setFetchSize`.
- Tentativa de `Connection.setReadOnly(true)`.
- Usuário de banco obrigatoriamente read-only.
- Mascaramento LGPD no mapeamento de resultado.
- Erros sanitizados para não vazar senha, token ou segredo.

## Consequências

### Positivas

- O núcleo de domínio não depende de SQL Server nem Databricks.
- A mesma política de governança vale para mock, SQL Server e Databricks.
- O adapter real pode ser habilitado gradualmente por ambiente.
- A execução real tem proteção dupla: configuração do executor e flag explícita.

### Negativas

- Estimativa de custo em JDBC é conservadora, não equivalente ao otimizador real do banco.
- O driver Databricks deve ser fornecido no classpath conforme padrão corporativo.
- Views Gold precisam estar previamente modeladas e autorizadas.

## Critério de aceite

- Em modo `mock`, o sistema funciona sem banco.
- Em modo `sqlserver`, sem `permitir-execucao-real=true`, a consulta é bloqueada.
- Em modo `databricks`, sem URL/token, a consulta é bloqueada.
- Com execução real habilitada, o dry-run roda antes da execução final.
- Resultado real respeita limite de linhas e mascaramento LGPD.
