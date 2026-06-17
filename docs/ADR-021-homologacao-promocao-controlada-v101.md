# ADR-021 — Homologação e promoção controlada v1.0.1

## Status
Aceita para homologação.

## Contexto
A versão v1.0.0 consolidou a plataforma como release candidate corporativo. O próximo passo correto não é adicionar nova funcionalidade ampla, mas preparar a solução para homologação, produção assistida e promoção controlada.

## Decisão
Criar a versão v1.0.1 como pacote de homologação operacional, contendo scripts, evidências, checklist de aceite, runbook de go-live, rollback e matriz de configuração por ambiente.

## Regras
- Nenhuma promoção para produção sem readiness aprovado.
- Nenhuma execução real sem `GOVBI_DADOS_PERMITIR_EXECUCAO_REAL=true` explícito.
- Nenhuma consulta sensível sem aprovação humana.
- Nenhum download sem auditoria e retenção ativa.
- OIDC, RBAC, RLS, lock distribuído e persistência SQL Server são obrigatórios em produção.

## Consequências
- A release passa a ter trilha formal de homologação.
- A equipe de dados, segurança e operação passa a ter checklist único de aceite.
- Rollback passa a ser procedimento documentado e testável.
