# ADR-018 — Operação assíncrona real, resultados persistidos e SLA

## Status
Aceita — v0.8.0.

## Contexto
A versão v0.7.0 já possuía fila de reprocessamento e persistência corporativa. Faltava transformar essa fila em operação real: worker, reexecução controlada, materialização temporária do resultado, notificação e expiração por SLA/retenção.

## Decisão
A plataforma passa a ter uma camada operacional assíncrona:

1. `FilaConsultaWorker` processa itens `PENDENTE`.
2. `ProcessarFilaConsultaUseCase` materializa resultado mascarado e controlado.
3. `ResultadoConsultaPersistidaPort` armazena resultado por prazo limitado.
4. `NotificacaoOperacionalPort` registra notificações para usuário/aprovador.
5. `ExpirarAprovacoesUseCase` expira aprovações vencidas e remove resultados fora da retenção.

A execução automática fica desabilitada por padrão em ambiente local:

```bash
GOVBI_WORKER_HABILITADO=false
GOVBI_SLA_EXPIRAR_APROVACOES_HABILITADO=false
```

## Consequências
- A consulta sensível aprovada não depende mais de reexecução manual informal.
- O resultado passa a ter retenção explícita.
- A operação ganha fila, status, falha, notificação e rastreabilidade.
- Em produção, o worker deve rodar com usuário técnico segregado.
