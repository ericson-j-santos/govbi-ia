# Plano de testes — Incremento 8

## Casos obrigatórios

1. Enfileirar aprovação aprovada.
2. Processar item manualmente via endpoint.
3. Processar lote via worker/use case.
4. Persistir resultado com `expira_em`.
5. Listar resultado por aprovação.
6. Registrar notificação de resultado disponível.
7. Expirar aprovação vencida.
8. Expirar resultado fora da retenção.
9. Garantir que resultado expirado não mantém linhas.
10. Validar migrations SQL Server.

## Quality gate

```bash
python scripts/validate_async_increment.py
python scripts/validate_catalog_version.py
python scripts/evaluate_nl_sql.py --offline
python scripts/quality_check.py
```
