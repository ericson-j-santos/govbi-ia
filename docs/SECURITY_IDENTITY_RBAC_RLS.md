# Segurança — Identidade, RBAC e RLS

## Identidade

O GovBI IA opera como Resource Server OIDC/JWT quando `GOVBI_OIDC_HABILITADO=true`.

Claims esperadas:

| Claim | Uso |
|---|---|
| `preferred_username` | identificação auditável do usuário |
| `roles` | perfis/permissões |
| `escopo_unidade` | escopo RLS |

## RBAC

A métrica no catálogo define perfis permitidos:

```yaml
perfisPermitidos: ["ANALISTA", "ADMIN", "BI_GOVERNADO"]
```

Sem perfil autorizado, a consulta é bloqueada antes da geração final.

## RLS

A métrica também define a regra de linha:

```yaml
rls:
  campoUnidade: "u.codigo_unidade"
  joinObrigatorio: "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade"
  escopoGeral: "GERAL"
```

Se o usuário tiver escopo `SR001`, por exemplo, o SQL recebe predicado obrigatório por unidade.

## LGPD

- Perguntas individualizadas com CPF/nome/e-mail/telefone exigem aprovação humana.
- Logs registram hash da pergunta e hash do SQL, não o conteúdo bruto.
- Resultados com PII são mascarados no adapter JDBC.
