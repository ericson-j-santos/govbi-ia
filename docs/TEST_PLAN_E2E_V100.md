# Plano de testes E2E — GovBI IA v1.0.0

## Cenários mínimos

1. Usuário analítico consulta métrica governada sem PII.
2. Usuário com escopo de unidade recebe SQL com RLS.
3. Pergunta com CPF/nome gera aprovação humana.
4. Aprovador aprova consulta sensível.
5. Aprovação aprovada gera item de fila.
6. Worker processa item com lock distribuído.
7. Resultado é persistido com retenção.
8. Notificação é criada.
9. Download CSV é liberado apenas para perfil autorizado.
10. Falha definitiva vai para DLQ.
11. Expiração remove linhas de resultado e preserva metadados.
12. Readiness `/api/v1/release/readiness` retorna status coerente.

## Comandos

```bash
python scripts/e2e_contract_v100.py --offline
python scripts/e2e_smoke.py http://localhost:8080
```
