# Versionamento do Catálogo Semântico

## Regras

O catálogo semântico possui versão própria e deve evoluir junto com o backend quando houver mudança de contrato.

Arquivo principal:

```text
backend/src/main/resources/catalogo-semantico.yml
```

Schema de referência:

```text
docs/schema/catalogo-semantico.schema.json
```

## Mudanças que exigem versão nova

- criação, remoção ou alteração de métrica;
- alteração de agregação;
- alteração de filtro padrão;
- alteração de join;
- alteração de campos sensíveis;
- alteração de RBAC/RLS;
- alteração de sinônimos relevantes usados por RAG.

## Comando de validação

```bash
python scripts/validate_catalog_version.py
```

## Política

Toda mudança deve ter:

- justificativa;
- impacto esperado;
- atualização do golden dataset;
- teste automatizado;
- aprovação do responsável pelo dado.
