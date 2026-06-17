# ADR-016 — Produto corporativo operacional

## Status
Aprovada para MVP v0.6.0.

## Contexto
O GovBI IA já possui governança, execução real controlada, observabilidade e avaliação. O próximo risco operacional é transformar o motor em produto: histórico, aprovação humana, exportação controlada, auditoria consultável, administração do catálogo e deploy padronizado.

## Decisão
Adicionar uma camada operacional acima do núcleo hexagonal, mantendo portas desacopladas:

- `AprovacaoHumanaPort` para consultas sensíveis.
- `HistoricoConversacionalPort` para continuidade e rastreabilidade.
- `AuditoriaConsultavelPort` para consulta operacional de eventos.
- `ExportadorResultadoPort` para exportação com limite, perfil e formato controlado.
- `CatalogoAdminPort` para visualização e proposta de alterações no catálogo.

## Consequências
Consultas sensíveis deixam de ser apenas bloqueadas e passam a gerar uma solicitação formal de aprovação. A execução continua protegida: nenhuma consulta pendente executa SQL antes de decisão humana.
