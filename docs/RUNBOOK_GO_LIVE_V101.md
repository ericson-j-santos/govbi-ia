# Runbook de go-live — GovBI IA v1.0.1

## Pré-requisitos
- Branch/tag de release validada.
- Imagem Docker gerada e versionada.
- Secrets cadastrados no cofre corporativo.
- Migrations aplicadas em homologação.
- Smoke tests aprovados.
- Janela de implantação aprovada.

## Sequência homologação
```bash
export SPRING_PROFILES_ACTIVE=hom
export GOVBI_RELEASE_MODO=homologacao
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=false
python scripts/run_homologation_smoke.py --base-url http://localhost:8080 --modo hom
python scripts/generate_aceite_report.py --ambiente hom
```

## Sequência produção assistida
```bash
export SPRING_PROFILES_ACTIVE=prod
export GOVBI_RELEASE_MODO=producao
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true
export GOVBI_WORKER_HABILITADO=true
python scripts/run_homologation_smoke.py --base-url https://govbi.empresa.gov.br --modo prod --no-write
```

## Monitoramento pós-go-live
- `/actuator/health`.
- `/actuator/prometheus`.
- `/api/v1/release/readiness`.
- DLQ sem crescimento anormal.
- p95 de consulta dentro do limite operacional.
- downloads sempre auditados.

## Critério de rollback
Executar rollback se houver falha crítica em autenticação, RLS, auditoria, DLQ recorrente ou vazamento de PII.
