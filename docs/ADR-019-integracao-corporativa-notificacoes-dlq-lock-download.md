# ADR-019 — Integração corporativa real de notificações, DLQ, lock distribuído e download controlado

## Status
Aceita — GovBI IA v0.9.0.

## Contexto
O Incremento 8 materializou a operação assíncrona. O próximo risco operacional era executar múltiplos workers sobre o mesmo item, perder falhas definitivas, depender apenas de log para notificação e permitir exportação sem trilha formal.

## Decisão
Adicionar:

- `CanalNotificacaoPort` com adapters LOG, Teams Webhook e SMTP;
- `TemplateNotificacaoPort` para padronizar mensagens;
- `LockDistribuidoPort` com implementação em memória e SQL Server;
- `DeadLetterConsultaPort` com DLQ consultável;
- `DownloadResultadoPort` para download controlado de resultado persistido;
- tela operacional Angular com aprovação, fila, resultados, auditoria, notificações e DLQ.

## Consequências
A operação passa a ser mais resiliente e auditável. O modo padrão continua seguro para demo: LOG, memória e worker desabilitado. A produção habilita Teams, SMTP, SQL Server e lock distribuído por variáveis de ambiente.
