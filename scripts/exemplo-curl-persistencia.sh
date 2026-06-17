#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"

curl -s "$BASE_URL/api/v1/auditoria/recentes?limite=5" | jq .
curl -s "$BASE_URL/api/v1/historico/recentes?limite=5" | jq .
curl -s "$BASE_URL/api/v1/aprovacoes/pendentes" | jq .
curl -s "$BASE_URL/api/v1/fila-consultas/pendentes" | jq .
