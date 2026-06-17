#!/usr/bin/env bash
set -euo pipefail

curl -X POST http://localhost:8080/api/v1/perguntas \
  -H 'Content-Type: application/json' \
  -H 'X-Usuario: analista.demo' \
  -H 'X-Perfil: ANALISTA' \
  -H 'X-Escopo-Unidade: GERAL' \
  -d '{
    "pergunta": "Mostre propostas cadastradas por mês em 2025 por situação e unidade",
    "formatoResposta": "tabela_grafico",
    "exibirSql": true
  }'
