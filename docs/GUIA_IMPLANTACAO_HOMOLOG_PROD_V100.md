# Guia de implantação — Homologação e Produção v1.0.0

## Homologação

```bash
export SPRING_PROFILES_ACTIVE=hom
export GOVBI_OIDC_HABILITADO=true
export GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver
export GOVBI_DADOS_EXECUTOR=sqlserver
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=false
export GOVBI_LOCK_TIPO=sqlserver
```

Executar:

```bash
cd backend
mvn test
mvn spring-boot:run -Dspring-boot.run.profiles=hom
```

Validar:

```bash
python scripts/quality_check.py
python scripts/validate_catalog_version.py
python scripts/evaluate_nl_sql.py --offline
python scripts/validate_release_v100.py
```

## Produção

```bash
export SPRING_PROFILES_ACTIVE=prod
export GOVBI_OIDC_HABILITADO=true
export GOVBI_PERSISTENCIA_OPERACIONAL_TIPO=sqlserver
export GOVBI_DADOS_EXECUTOR=sqlserver
export GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true
export GOVBI_LOCK_TIPO=sqlserver
export GOVBI_WORKER_HABILITADO=true
```

Antes de subir:

1. Aplicar migrations SQL Server.
2. Configurar segredos fora do repositório.
3. Validar OIDC e claims.
4. Importar dashboard Grafana.
5. Executar smoke E2E em homologação.
6. Formalizar aceite do catálogo semântico.
