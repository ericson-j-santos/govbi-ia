# ADR-017 — Persistência corporativa real para produto operacional

## Status
Aceita — v0.7.0.

## Contexto
O v0.6.0 mantinha aprovação, histórico, auditoria e catálogo em adapters em memória. Isso era suficiente para demo e desenvolvimento local, mas insuficiente para operação corporativa, auditoria, rastreabilidade e reprocessamento.

## Decisão
Adicionar persistência operacional SQL Server para:

- aprovações humanas;
- histórico conversacional;
- auditoria consultável;
- propostas de alteração do catálogo;
- fila de consultas/reprocessamento.

A escolha é configurável por propriedade:

```yaml
govbi.persistencia.operacional.tipo: memoria | sqlserver
```

O padrão segue `memoria` para execução local sem infraestrutura. Produção deve usar `sqlserver`.

## Consequências
- Estado operacional passa a sobreviver a restart.
- A auditoria vira consultável por API e por SQL corporativo.
- Consultas sensíveis aprovadas podem ser enfileiradas para reprocessamento.
- A aplicação precisa de usuário SQL com permissão restrita, sem DDL em runtime.
