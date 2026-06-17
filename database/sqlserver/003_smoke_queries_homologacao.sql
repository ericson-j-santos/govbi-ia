/* GovBI IA v1.0.1 — smoke queries SQL Server operacional */
SET NOCOUNT ON;

SELECT TOP (5) * FROM govbi.aprovacao_consulta ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.historico_conversa ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.auditoria_consulta ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.fila_consulta ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.resultado_consulta ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.notificacao_operacional ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.dlq_consulta ORDER BY criado_em DESC;
SELECT TOP (5) * FROM govbi.lock_distribuido ORDER BY criado_em DESC;
