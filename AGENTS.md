# AGENTS.md

## Cursor Cloud specific instructions

GovBI IA is a single-product monorepo: a Spring Boot backend (`backend/`, the product core), an Angular SPA (`frontend-angular/`), SQL Server schema (`database/`), and Python quality-gate scripts (`scripts/`). The backend runs fully standalone with in-memory/mock adapters (no external DB/LLM/IdP needed) under the `local` profile.

Toolchain already provisioned by the update script / base image: Java 21, Maven 3.8 (`mvn`), Node 22 + npm, Python 3.12. There is no Maven wrapper and no `package-lock.json`.

### Backend (`backend/`, Spring Boot, port 8080)
- Run in dev: `cd backend && SPRING_PROFILES_ACTIVE=local mvn -Dmaven.test.skip=true spring-boot:run`
- IMPORTANT: you MUST set `SPRING_PROFILES_ACTIVE=local`. The README shows a bare `mvn spring-boot:run`, but the default profile activates the OAuth2 resource-server autoconfig with empty `jwk-set-uri` and the app fails to boot (`jwkSetUri cannot be empty`). The `local` profile excludes that autoconfig and uses an in-memory H2 datasource.
- Health: `GET http://localhost:8080/actuator/health`; Swagger UI: `/swagger-ui.html`; core endpoint: `POST /api/v1/perguntas` (send headers `X-Usuario`, `X-Perfil`, `X-Escopo-Unidade`; see `scripts/exemplo-curl.sh`).
- Tests: `cd backend && mvn test`. NOTE: as of this writing `mvn test` (and any goal that compiles tests, e.g. plain `package`) FAILS to compile because `src/test/java/.../seguranca/PoliticaAcessoRbacRlsAdapterTest.java` contains an invalid multi-line string literal (should be a Java text block). This is a pre-existing source bug, not an environment problem. To build/run while it is unfixed, skip test compilation with `-Dmaven.test.skip=true` (note: `-DskipTests` is NOT enough — it still compiles tests).

### Frontend (`frontend-angular/`, Angular 21, port 4200)
- Run in dev: `cd frontend-angular && npx ng serve --host 0.0.0.0 --port 4200` (the `npm start` script uses `--open`, which needs a browser).
- Build: `npm run build`. Tests: `npm test` (Karma; needs a browser).
- CORS caveat: the chat UI (`govbi-api.service.ts`) calls `http://localhost:8080` cross-origin with custom headers. The backend has no CORS config and no dev proxy (`proxy.conf.json`) exists, so browser calls fail the preflight with `403 Invalid CORS request`. The UI renders fine but cannot reach the backend until CORS/a dev proxy is added (a code change, out of scope for env setup). Exercise the core product directly via the API (curl/Swagger) instead.

### Python quality gate (`scripts/`)
- Use `python3` (there is no `python` alias). Commands are listed in `README.md` and `.github/workflows/quality-gate.yml`.
- Two homologation checks (`validate_homologation_v101.py`, `run_homologation_smoke.py`) FAIL locally because `deploy/env/.env.hom.example` and `deploy/env/.env.prod.example` are not committed (`.env*` is gitignored). Pre-existing; not an environment issue.
