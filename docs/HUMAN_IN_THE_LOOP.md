# Aprovação humana para consultas sensíveis

Consultas que pedem CPF, nome, e-mail, telefone, detalhe individual ou combinações de alto risco são classificadas como `PENDENTE_APROVACAO`.

O retorno inclui:

- `statusFluxo`: `PENDENTE_APROVACAO`;
- `requerAprovacao`: `true`;
- `aprovacaoId`;
- `historicoId`;
- avisos de governança.

A consulta não é executada antes da aprovação.
