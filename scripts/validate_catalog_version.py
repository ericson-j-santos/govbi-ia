#!/usr/bin/env python3
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
CATALOGO = ROOT / "backend/src/main/resources/catalogo-semantico.yml"
SCHEMA = ROOT / "docs/schema/catalogo-semantico.schema.json"

texto = CATALOGO.read_text(encoding="utf-8")
erros = []
versao = re.search(r'^versao:\s*"?([0-9]+\.[0-9]+\.[0-9]+)"?', texto, re.M)
compat = re.search(r'compatibilidadeMinimaBackend:\s*"?([0-9]+\.[0-9]+\.[0-9]+)"?', texto)
if not versao:
    erros.append("versao do catálogo ausente ou inválida")
if not compat:
    erros.append("compatibilidadeMinimaBackend ausente")
if versao and compat and versao.group(1) != compat.group(1):
    erros.append(f"versao ({versao.group(1)}) diferente de compatibilidadeMinimaBackend ({compat.group(1)})")
for token in ["metricas:", "trechos:", "politicaAcesso:", "camposSensiveis:", "joinsPorDimensao:"]:
    if token not in texto:
        erros.append(f"token obrigatório ausente: {token}")
if not SCHEMA.exists():
    erros.append("schema JSON do catálogo não encontrado")
if erros:
    for erro in erros:
        print("FAIL", erro)
    sys.exit(2)
print("OK catalog_version", versao.group(1))
