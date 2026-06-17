# Arquitetura — GovBI IA

## Visão

O GovBI IA é uma plataforma de BI conversacional governado. O usuário pergunta em linguagem natural, mas a execução segue um pipeline controlado.

```text
Pergunta
→ Recuperação semântica RAG
→ Plano analítico
→ SQL governado
→ Validação de segurança
→ Dry-run
→ Execução read-only
→ Resposta auditável
```

## Camadas

### API

Recebe requisição HTTP, monta `PerguntaAnalitica` e `UsuarioContexto`.

### Aplicação

`ResponderPerguntaAnaliticaUseCase` orquestra o fluxo completo.

### Domínio

Modelos e portas:

- `PerguntaAnalitica`
- `PlanoConsulta`
- `ConsultaGerada`
- `DryRunConsulta`
- `ResultadoConsulta`
- `MetricaSemantica`
- `TrechoCatalogoSemantico`
- `MotorIaPort`
- `CatalogoSemanticoPort`
- `ValidadorConsultaPort`
- `ExecutorConsultaPort`
- `PoliticaAcessoPort`
- `AuditoriaPort`

### Infraestrutura

Adapters substituíveis:

- `CatalogoSemanticoEmMemoriaAdapter`
- `MotorIaLlmRagAdapter`
- `ClienteLlmMockAdapter`
- `ValidadorSqlSeguroAdapter`
- `ExecutorConsultaMockAdapter`
- `PoliticaAcessoSimplesAdapter`
- `AuditoriaLogAdapter`

## Fluxo detalhado

1. Receber pergunta.
2. Gerar `correlation_id`.
3. Buscar contexto semântico no catálogo.
4. Criar plano analítico via motor LLM/RAG.
5. Bloquear se houver solicitação individualizada de PII.
6. Buscar métrica governada.
7. Validar acesso do usuário.
8. Gerar SQL.
9. Validar SQL contra políticas e allowlist.
10. Executar dry-run.
11. Repetir geração até 3 vezes se houver falha.
12. Executar consulta read-only.
13. Registrar auditoria.
14. Responder com resultado, SQL, contexto e tentativas.

## Segurança

Controles implementados:

- read-only;
- allowlist por métrica;
- camada Gold obrigatória;
- bloqueio DDL/DML;
- bloqueio de comentários SQL;
- bloqueio de múltiplas instruções;
- bloqueio de `SELECT *`;
- bloqueio de listagem individualizada com PII;
- dry-run com custo e linhas estimadas;
- auditoria com `correlation_id`.

## Extensão para produção

### LLM real

Implementar `ClienteLlmPort`.

### Banco real

Implementar `ExecutorConsultaPort` para SQL Server, Databricks ou Trino.

### Catálogo real

Substituir catálogo em memória por:

- tabela SQL governada;
- YAML versionado em Git;
- DataHub/OpenMetadata;
- dbt Semantic Layer;
- Cube Semantic Layer.
