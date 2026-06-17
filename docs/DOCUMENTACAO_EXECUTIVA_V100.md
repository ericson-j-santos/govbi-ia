# GovBI IA v1.0.0 — Documentação executiva

## Objetivo
Permitir que usuários de negócio consultem bases corporativas por linguagem natural, com geração de relatórios, tabelas, gráficos e SQL auditável, sem abrir mão de governança, LGPD, RBAC/RLS, aprovação humana e rastreabilidade.

## Valor entregue
- Redução do tempo entre pergunta de negócio e análise.
- Padronização de métricas via catálogo semântico.
- Auditoria completa de perguntas, SQL, execuções e downloads.
- Controle de dados sensíveis por aprovação humana.
- Operação assíncrona para consultas aprovadas e custosas.
- Observabilidade para operação sustentada.

## Escopo v1.0.0
Incluído:
- Chat analítico governado.
- Catálogo semântico YAML.
- LLM/RAG substituível.
- SQL Server e Databricks como executores reais.
- Persistência operacional SQL Server.
- Aprovação humana, histórico, auditoria, fila, worker, DLQ, lock e retenção.
- Notificações Teams/e-mail.
- Download controlado.
- Demo HTML autocontida.

Fora do escopo:
- Garantia de acurácia para métricas não cadastradas.
- Escrita em sistemas transacionais.
- Acesso irrestrito a dados pessoais.
- Substituição de governança de dados institucional.
