#!/usr/bin/env bash
set -euo pipefail

curl -X POST http://localhost:8080/api/v1/perguntas   -H 'Content-Type: application/json'   -H 'X-Correlation-Id: demo-rbac-rls-001'   -H 'X-Usuario: analista.demo'   -H 'X-Perfil: ANALISTA'   -H 'X-Escopo-Unidade: SR001'   -d '{
    "pergunta": "Mostre propostas cadastradas por mês em 2025 por situação",
    "formatoResposta": "tabela_grafico",
    "exibirSql": true
  }'
