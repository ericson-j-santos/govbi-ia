# Arquitetura — GovBI IA v1.0.1

## Visão

O GovBI IA é uma plataforma de BI conversacional governado. O usuário pergunta em linguagem natural, mas a execução segue um pipeline controlado e auditável.

```text
Pergunta
→ Identidade (OIDC/JWT ou headers demo)
→ Rate limit
→ Recuperação semântica RAG
→ Plano analítico (LLM)
→ SQL governado
→ Validação de segurança
→ Dry-run
→ Aprovação humana (quando sensível)
→ Execução read-only
→ Resposta mascarada e auditável
```

## Camadas

### API

Controllers REST em `api/controller`. Recebem HTTP, montam `PerguntaAnalitica` e `UsuarioContexto` via `UsuarioContextoFactory`.

Filtros:
- `CorrelationIdFilter` — propaga `X-Correlation-Id` e MDC
- `RateLimitFilter` — limita requisições por usuário/IP em `/api/v1/*`

### Aplicação

Use cases em `aplicacao/caso_uso`:
- `ResponderPerguntaAnaliticaUseCase` — fluxo analítico principal
- `ProcessarFilaConsultaUseCase` — worker assíncrono
- `ReprocessarAprovacaoUseCase` — reprocessamento pós-aprovação
- `NotificarOperacaoUseCase` — notificações operacionais

### Domínio

Modelos e portas hexagonais em `dominio/modelo` e `dominio/porta`.

### Infraestrutura — adapters substituíveis

| Porta | Adapters |
|-------|----------|
| `ClienteLlmPort` | mock-rag, OpenAI, Azure OpenAI, Groq, **Gemini** |
| `ExecutorConsultaPort` | mock, SQL Server, Databricks, **PostgreSQL** |
| `CatalogoSemanticoPort` | YAML, memória |
| `PoliticaAcessoPort` | RBAC/RLS |
| `AprovacaoHumanaPort` | memória, SQL Server |
| `AuditoriaPort` | log, SQL Server |
| `CanalNotificacaoPort` | log, Teams, SMTP |
| `LockDistribuidoPort` | memória, SQL Server |

Resiliência LLM: `HttpLlmResilienteClient` com retry exponencial para timeout, 429 e 5xx.

## Fluxo detalhado

1. Receber pergunta com contexto de usuário.
2. Gerar ou propagar `correlation_id`.
3. Buscar contexto semântico no catálogo (RAG).
4. Criar plano analítico via motor LLM/RAG.
5. Bloquear PII individualizada sem aprovação.
6. Buscar métrica governada e validar RBAC/RLS.
7. Gerar e validar SQL (allowlist, read-only, dry-run).
8. Repetir geração até N tentativas se houver falha.
9. Executar consulta read-only ou enfileirar para aprovação.
10. Registrar auditoria e responder com resultado mascarado.

## Segurança

- OIDC/JWT em hom/prod; headers demo em local/test
- Rate limiting configurável por minuto
- SQL read-only com allowlist por métrica
- Camada Gold obrigatória
- Bloqueio DDL/DML, `SELECT *`, PII sem aprovação
- Dry-run com custo e linhas estimadas
- Download controlado e auditado
- DLQ para falhas definitivas

## Frontend Angular

- Rotas: `/` (perguntas), `/operacional`, `/operacional/console`, `/operacional/release`
- `authInterceptor` injeta `Authorization: Bearer` (OIDC) ou headers demo
- Proxy dev: `proxy.conf.json` → backend `:8080`

## Deploy e ambientes

| Ambiente | Perfil | LLM | Executor | Persistência |
|----------|--------|-----|----------|--------------|
| local | `local` | mock-rag/gemini | mock | memória/H2 |
| hom | `hom` | gemini | sqlserver | sqlserver |
| prod | `prod` | gemini | sqlserver | sqlserver |

Templates: `deploy/env/.env.hom.example`, `deploy/env/.env.prod.example`

## Observabilidade

Actuator, Prometheus, Grafana, OTLP tracing. Golden dataset NL→SQL com avaliação offline e online.
