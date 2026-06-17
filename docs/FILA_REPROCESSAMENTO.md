# Fila de reprocessamento

A fila de consultas foi adicionada para separar a decisão humana da execução operacional posterior.

Fluxo:

```text
consulta sensível
→ PENDENTE_APROVACAO
→ aprovador decide APROVADA
→ operador chama /api/v1/fila-consultas/aprovacoes/{id}/reprocessar
→ item de fila PENDENTE
→ worker ou operação controlada processa
```

No v0.7.0 o worker automático fica preparado, mas não executa consulta sensível sem implementação explícita de política corporativa. A decisão evita que uma aprovação humana dispare, sozinha, acesso sensível sem janela operacional, trilha e controle de lotes.
