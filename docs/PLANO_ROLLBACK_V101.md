# Plano de rollback — GovBI IA v1.0.1

## Objetivo
Restaurar a última versão estável sem perda de trilha de auditoria.

## Gatilhos
- Falha crítica em OIDC/RBAC/RLS.
- Consulta sensível executada sem aprovação.
- Download sem auditoria.
- Erro de migration com impacto operacional.
- DLQ com crescimento contínuo.
- Health check indisponível após janela de estabilização.

## Procedimento
1. Congelar novas aprovações.
2. Desabilitar worker:
   ```bash
   export GOVBI_WORKER_HABILITADO=false
   ```
3. Reduzir execução para modo seguro:
   ```bash
   export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=false
   export GOVBI_DADOS_EXECUTOR=mock
   ```
4. Reimplantar imagem anterior.
5. Preservar tabelas de auditoria e DLQ.
6. Executar smoke test pós-rollback.
7. Registrar causa, impacto e ação corretiva.

## Regra de dados
Não apagar auditoria, histórico, DLQ nem registros de download. Se necessário, bloquear leitura operacional, mas preservar evidências.
