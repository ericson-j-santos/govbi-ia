# Quality Gate Enterprise

## Gates aplicados

1. `mvn test` no backend.
2. `scripts/quality_check.py` para presença de artefatos obrigatórios.
3. `scripts/validate_catalog_version.py` para contrato de versionamento do catálogo.
4. `scripts/evaluate_nl_sql.py --offline` para golden dataset.
5. Publicação do relatório de avaliação como artifact de CI.

## Critério de bloqueio

O merge/deploy deve ser bloqueado quando qualquer item abaixo ocorrer:

- falha de teste unitário;
- catálogo semântico sem versão compatível;
- métrica sem política de acesso;
- ausência de campos sensíveis em métrica com PII conhecida;
- golden dataset com falha;
- remoção de validação SQL read-only;
- ausência de documentação ADR para mudança arquitetural.

## Branches recomendadas

```text
feature/<id>-descricao
develop
release/vX.Y.Z
main
hotfix/<id>-descricao
```
