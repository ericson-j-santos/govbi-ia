# ADR-020 — Consolidação release corporativa v1.0.0

## Status
Aceita — release candidate v1.0.0.

## Contexto
O GovBI IA evoluiu de MVP conversacional para plataforma corporativa com catálogo semântico, governança, execução read-only, RAG, RBAC/RLS, aprovação humana, fila, DLQ, notificações, download controlado, observabilidade e persistência operacional.

A versão v1.0.0 consolida o contrato estável para homologação e preparação de produção.

## Decisão
Consolidar a release v1.0.0 com os seguintes compromissos arquiteturais:

1. O LLM não executa SQL diretamente.
2. O SQL final é composto e validado pelo backend.
3. O catálogo semântico é o contrato de métricas e joins permitidos.
4. Toda execução real exige configuração explícita.
5. Dados pessoais exigem aprovação humana, mascaramento e retenção controlada.
6. Worker assíncrono usa lock distribuído e DLQ.
7. Homologação/produção exigem checklist formal.
8. A rastreabilidade deve ligar requisito, implementação, teste e evidência.

## Consequências
- A entrega passa a ser operável como produto corporativo.
- A expansão de métricas exige versionamento do catálogo.
- A evolução de modelos LLM não altera o contrato de segurança.
- A implantação em produção depende de OIDC, SQL Server operacional, observabilidade e gates executados.
