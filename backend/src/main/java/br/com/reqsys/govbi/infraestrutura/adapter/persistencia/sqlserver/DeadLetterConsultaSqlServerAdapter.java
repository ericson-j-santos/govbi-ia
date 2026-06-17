package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.DeadLetterConsulta;
import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.porta.DeadLetterConsultaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class DeadLetterConsultaSqlServerAdapter extends SqlServerOperacionalJdbc implements DeadLetterConsultaPort {
    private final RowMapper<DeadLetterConsulta> mapper = (rs, rowNum) -> new DeadLetterConsulta(
            rs.getString("id"), rs.getString("fila_id"), rs.getString("aprovacao_id"), rs.getString("correlation_id"), rs.getString("metrica"),
            rs.getString("motivo_falha"), rs.getString("stack_sanitizado"), JsonBancoUtil.toMap(rs.getString("payload_json")),
            rs.getInt("tentativas_originais"), rs.getInt("tentativas_reprocessamento"), rs.getString("status"),
            rs.getTimestamp("criado_em").toInstant(), rs.getTimestamp("atualizado_em").toInstant());

    public DeadLetterConsultaSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public DeadLetterConsulta registrar(ItemFilaConsulta item, String motivoFalha, String stackSanitizado) {
        var agora = Instant.now();
        var dlq = new DeadLetterConsulta(UUID.randomUUID().toString(), item.id(), item.aprovacaoId(), item.correlationId(), item.metrica(), motivoFalha,
                stackSanitizado, item.payload(), item.tentativas(), 0, "ABERTA", agora, agora);
        jdbcTemplate.update("INSERT INTO " + table("dlq_consulta") + " (id, fila_id, aprovacao_id, correlation_id, metrica, motivo_falha, stack_sanitizado, payload_json, tentativas_originais, tentativas_reprocessamento, status, criado_em, atualizado_em) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                dlq.id(), dlq.filaId(), dlq.aprovacaoId(), dlq.correlationId(), dlq.metrica(), dlq.motivoFalha(), dlq.stackSanitizado(), JsonBancoUtil.toJson(dlq.payload()), dlq.tentativasOriginais(), dlq.tentativasReprocessamento(), dlq.status(), Timestamp.from(dlq.criadoEm()), Timestamp.from(dlq.atualizadoEm()));
        return dlq;
    }
    @Override public Optional<DeadLetterConsulta> buscar(String id) { return jdbcTemplate.query("SELECT TOP (1) * FROM " + table("dlq_consulta") + " WHERE id=?", mapper, id).stream().findFirst(); }
    @Override public List<DeadLetterConsulta> listarRecentes(int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("dlq_consulta") + " ORDER BY criado_em DESC", mapper); }
    @Override public DeadLetterConsulta atualizarStatus(String id, String status) { jdbcTemplate.update("UPDATE " + table("dlq_consulta") + " SET status=?, tentativas_reprocessamento=tentativas_reprocessamento+1, atualizado_em=SYSUTCDATETIME() WHERE id=?", status, id); return buscar(id).orElseThrow(); }
}
