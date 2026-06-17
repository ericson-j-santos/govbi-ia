#!/usr/bin/env python3
from __future__ import annotations

import json
import sys
import urllib.request

BASE_URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080"
payload = json.dumps({
    "pergunta": "Mostre propostas cadastradas por mês em 2025 por situação e unidade",
    "formatoResposta": "tabela",
    "exibirSql": True
}).encode("utf-8")
req = urllib.request.Request(
    BASE_URL.rstrip("/") + "/api/v1/perguntas",
    data=payload,
    method="POST",
    headers={
        "Content-Type": "application/json",
        "X-Usuario": "e2e.smoke",
        "X-Perfil": "ANALISTA",
        "X-Escopo-Unidade": "GERAL",
        "X-Correlation-Id": "e2e-smoke"
    }
)
with urllib.request.urlopen(req, timeout=20) as resp:
    body = json.loads(resp.read().decode("utf-8"))
    assert resp.status == 200
    assert body["metrica"] == "qtd_propostas_cadastradas"
    assert "SELECT" in body.get("sqlGerado", "")
print("OK e2e_smoke")
