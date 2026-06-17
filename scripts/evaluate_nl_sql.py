#!/usr/bin/env python3
"""
Harness de avaliação NL→SQL do GovBI IA.

Modo offline: valida estrutura e políticas do golden dataset.
Modo online: chama a API local/remota e compara resposta com expectativas.
"""
from __future__ import annotations

import argparse
import json
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_DATASET = ROOT / "backend/src/main/resources/evaluation/golden-dataset.json"


def carregar_dataset(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as f:
        return json.load(f)


def validar_offline(dataset: dict) -> list[dict]:
    resultados = []
    if not dataset.get("versao"):
        resultados.append(falha("DATASET", "versao ausente"))
    casos = dataset.get("casos", [])
    if not casos:
        resultados.append(falha("DATASET", "nenhum caso cadastrado"))
    for caso in casos:
        cid = caso.get("id", "SEM_ID")
        pergunta = caso.get("pergunta", "")
        esperado = caso.get("esperado", {})
        erros = []
        if len(pergunta.strip()) < 10:
            erros.append("pergunta curta ou vazia")
        if not esperado:
            erros.append("expectativa ausente")
        if esperado.get("bloqueio") is not True:
            for token in esperado.get("sqlNaoContem", []):
                if token.upper() in {"DELETE", "UPDATE", "DROP", "INSERT", "TRUNCATE", "ALTER"}:
                    continue
            if not esperado.get("metrica"):
                erros.append("métrica esperada ausente")
            if not esperado.get("sqlContem"):
                erros.append("sqlContem ausente")
        resultados.append(ok(cid, "offline") if not erros else falha(cid, "; ".join(erros)))
    return resultados


def chamar_api(base_url: str, pergunta: str, exibir_sql: bool = True) -> tuple[int, dict | str]:
    payload = json.dumps({"pergunta": pergunta, "formatoResposta": "tabela", "exibirSql": exibir_sql}).encode("utf-8")
    req = urllib.request.Request(
        base_url.rstrip("/") + "/api/v1/perguntas",
        data=payload,
        headers={
            "Content-Type": "application/json",
            "X-Usuario": "avaliador.quality-gate",
            "X-Perfil": "ANALISTA",
            "X-Escopo-Unidade": "GERAL",
            "X-Correlation-Id": "quality-gate-nl-sql"
        },
        method="POST",
    )
    try:
        with urllib.request.urlopen(req, timeout=20) as resp:
            body = resp.read().decode("utf-8")
            return resp.status, json.loads(body)
    except urllib.error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        try:
            return exc.code, json.loads(body)
        except json.JSONDecodeError:
            return exc.code, body


def validar_online(dataset: dict, base_url: str) -> list[dict]:
    resultados = []
    for caso in dataset.get("casos", []):
        cid = caso["id"]
        esperado = caso.get("esperado", {})
        inicio = time.perf_counter()
        status, body = chamar_api(base_url, caso["pergunta"])
        duracao_ms = round((time.perf_counter() - inicio) * 1000, 2)
        erros = []
        if esperado.get("bloqueio"):
            texto = json.dumps(body, ensure_ascii=False).lower()
            if status < 400:
                erros.append(f"bloqueio esperado, mas API retornou {status}")
            for termo in esperado.get("motivoContem", []):
                if termo.lower() not in texto:
                    erros.append(f"motivo não contém: {termo}")
        else:
            if status != 200:
                erros.append(f"status esperado 200, recebido {status}")
            if isinstance(body, dict):
                if esperado.get("metrica") and body.get("metrica") != esperado["metrica"]:
                    erros.append(f"métrica divergente: {body.get('metrica')}")
                dims = set(body.get("dimensoes") or [])
                for dim in esperado.get("dimensoes", []):
                    if dim not in dims:
                        erros.append(f"dimensão ausente: {dim}")
                sql = (body.get("sqlGerado") or "").upper()
                for token in esperado.get("sqlContem", []):
                    if token.upper() not in sql:
                        erros.append(f"SQL não contém: {token}")
                for token in esperado.get("sqlNaoContem", []):
                    if token.upper() in sql:
                        erros.append(f"SQL contém termo proibido: {token}")
            else:
                erros.append("resposta não é JSON")
        resultados.append(ok(cid, "online", duracao_ms) if not erros else falha(cid, "; ".join(erros), duracao_ms))
    return resultados


def ok(caso: str, modo: str, duracao_ms: float | None = None) -> dict:
    return {"caso": caso, "modo": modo, "status": "OK", "duracaoMs": duracao_ms}


def falha(caso: str, erro: str, duracao_ms: float | None = None) -> dict:
    return {"caso": caso, "status": "FAIL", "erro": erro, "duracaoMs": duracao_ms}


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", default=str(DEFAULT_DATASET))
    parser.add_argument("--offline", action="store_true", help="não chama backend; valida apenas contrato do dataset")
    parser.add_argument("--base-url", default="http://localhost:8080")
    parser.add_argument("--saida", default=str(ROOT / "reports/evaluation/evaluation-last.json"))
    args = parser.parse_args()

    dataset = carregar_dataset(Path(args.dataset))
    resultados = validar_offline(dataset) if args.offline else validar_online(dataset, args.base_url)
    saida = {"datasetVersao": dataset.get("versao"), "resultados": resultados}
    out = Path(args.saida)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(saida, ensure_ascii=False, indent=2), encoding="utf-8")
    for r in resultados:
        print(f"{r['status']} {r['caso']} {r.get('erro', '')}")
    return 0 if all(r["status"] == "OK" for r in resultados) else 2


if __name__ == "__main__":
    sys.exit(main())
