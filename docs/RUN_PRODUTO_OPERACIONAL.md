# Execução — Produto operacional v0.6.0

## Subir backend local

```bash
cd backend
mvn spring-boot:run
```

## Endpoints novos

- `GET /api/v1/aprovacoes/pendentes`
- `POST /api/v1/aprovacoes/{id}/decisao`
- `GET /api/v1/historico/meu`
- `GET /api/v1/auditoria/recentes`
- `POST /api/v1/exportacoes`
- `GET /api/v1/catalogo/metricas`
- `GET /api/v1/catalogo/yaml`
- `POST /api/v1/catalogo/alteracoes`

## Variáveis úteis

```bash
export GOVBI_APROVACAO_HABILITADA=true
export GOVBI_APROVACAO_SLA_HORAS=24
export GOVBI_EXPORTACAO_LIMITE_LINHAS=5000
export GOVBI_CATALOGO_EDICAO_HABILITADA=false
```
