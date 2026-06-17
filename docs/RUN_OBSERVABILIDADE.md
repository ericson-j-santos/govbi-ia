# Execução da observabilidade

## Subir aplicação

```bash
cd backend
mvn spring-boot:run
```

## Endpoints

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/prometheus
```

## Prometheus e Grafana

```bash
cd observability
docker compose -f docker-compose.observability.yml up -d
```

Prometheus:

```text
http://localhost:9090
```

Grafana:

```text
http://localhost:3001
```

Dashboard fornecido:

```text
observability/grafana/govbi-ia-dashboard.json
```

## Métricas principais

```promql
sum by (status) (rate(govbi_perguntas_total[5m]))
histogram_quantile(0.95, sum by (le) (rate(govbi_consulta_duracao_seconds_bucket[5m])))
sum(increase(govbi_perguntas_total{status="bloqueio"}[24h]))
```

## Tracing OTLP

Variáveis:

```bash
export GOVBI_OBSERVABILIDADE_TRACING_HABILITADO=true
export GOVBI_OTLP_TRACING_ENDPOINT=http://localhost:4318/v1/traces
export GOVBI_TRACING_SAMPLING_PROBABILITY=0.10
```

Amostragem recomendada inicial: 10%. Aumentar apenas em investigação controlada.
