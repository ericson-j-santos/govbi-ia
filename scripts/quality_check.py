from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
required = [
    "backend/src/main/resources/catalogo-semantico.yml",
    "backend/src/main/resources/evaluation/golden-dataset.json",
    "backend/src/main/java/br/com/reqsys/govbi/dominio/porta/ObservabilidadePort.java",
    "backend/src/main/java/br/com/reqsys/govbi/dominio/porta/ReleaseReadinessPort.java",
    "backend/src/main/java/br/com/reqsys/govbi/api/controller/ReleaseReadinessController.java",
    "docs/ADR-020-consolidacao-release-corporativa-v100.md",
    "docs/DOCUMENTACAO_EXECUTIVA_V100.md",
    "docs/CHECKLIST_PRODUCAO_V100.md",
    "docs/MATRIZ_RASTREABILIDADE_V100.md",
    "docs/GUIA_IMPLANTACAO_HOMOLOG_PROD_V100.md",
    "docs/HARDENING_SEGURANCA_V100.md",
    "docs/TEST_PLAN_E2E_V100.md",
    "docs/RELEASE_NOTES_V100.md",
    "docs/schema/catalogo-semantico.schema.json",
    "docs/openapi/govbi-ia-v1.0.0.yaml",
    "release/release-manifest-v1.0.0.json",
    "backend/src/main/resources/db/migration/V0010__release_corporativa_v100.sql",
    "scripts/validate_release_v100.py",
    "scripts/e2e_contract_v100.py",
    "observability/prometheus/prometheus.yml",
    "observability/grafana/govbi-ia-dashboard.json",
    ".github/workflows/quality-gate.yml",
    "demo/govbi-ia-demo.html",
    "docs/HOMOLOGACAO_V101.md",
    "docs/ACEITE_FORMAL_V101.md",
    "docs/RUNBOOK_GO_LIVE_V101.md",
    "docs/PLANO_ROLLBACK_V101.md",
    "release/homologacao-manifest-v1.0.1.json",
    "scripts/validate_homologation_v101.py",
    "scripts/run_homologation_smoke.py",
    "scripts/generate_aceite_report.py",
]
checks = []
for item in required:
    p = ROOT / item
    checks.append((item, p.exists() and p.stat().st_size > 0))

pom = (ROOT / "backend/pom.xml").read_text(encoding="utf-8")
readme = (ROOT / "README.md").read_text(encoding="utf-8")
app = (ROOT / "backend/src/main/resources/application.yml").read_text(encoding="utf-8")
catalogo = (ROOT / "backend/src/main/resources/catalogo-semantico.yml").read_text(encoding="utf-8")
checks.append(("backend version 1.0.x", "<version>1.0.0</version>" in pom or "<version>1.0.1</version>" in pom))
checks.append(("catalog version 1.0.x", ('versao: "1.0.0"' in catalogo and 'compatibilidadeMinimaBackend: "1.0.0"' in catalogo) or ('versao: "1.0.1"' in catalogo and 'compatibilidadeMinimaBackend: "1.0.1"' in catalogo)))
checks.append(("README v1.0.0", "v1.0.0" in readme and "release candidate" in readme.lower()))
checks.append(("release config", "release:" in app and "hardening-obrigatorio" in app))
checks.append(("prometheus registry", "micrometer-registry-prometheus" in pom))
checks.append(("otel tracing", "micrometer-tracing-bridge-otel" in pom and "opentelemetry-exporter-otlp" in pom))
checks.append(("actuator prometheus", "prometheus" in app and "/actuator/prometheus" in readme))
checks.append(("catalog governance contract", "contratoGovernanca:" in catalogo and "releaseV100:" in catalogo))
java_files = list((ROOT / "backend/src").rglob("*.java"))
html = ROOT / "demo/govbi-ia-demo.html"
print("quality_check=OK" if all(ok for _, ok in checks) else "quality_check=FAIL")
print(f"java_files={len(java_files)}")
print(f"html_size={html.stat().st_size if html.exists() else 0}")
for item, ok in checks:
    print(f"{'OK' if ok else 'FAIL'} {item}")
raise SystemExit(0 if all(ok for _, ok in checks) else 2)

# v1.0.1 homologation artifacts are validated by scripts/validate_homologation_v101.py
