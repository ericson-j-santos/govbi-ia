# Retenção de resultado analítico

Resultados pós-aprovação são persistidos para consumo operacional temporário. Eles não devem virar nova base analítica oficial.

## Política padrão

```bash
GOVBI_RESULTADO_RETENCAO_DIAS=30
```

Após expiração:

- `linhas_json` é substituído por `[]`;
- `total_linhas` passa para `0`;
- `status_retencao` passa para `EXPIRADO`;
- metadados mínimos permanecem para auditoria.

## Dados sensíveis

O resultado persistido deve ser o mesmo resultado governado retornado pela camada de execução: já mascarado e limitado. Não persistir PII bruta.
