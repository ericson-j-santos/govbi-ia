package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusItemFila;
import br.com.reqsys.govbi.dominio.porta.FilaConsultaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class FilaConsultaSqlServerAdapter extends SqlServerOperacionalJdbc implements FilaConsultaPort {
    private final RowMapper<ItemFilaConsulta> mapper = (rs, rowNum) -> new ItemFilaConsulta(
            rs.getString("id"), rs.getString("tipo"), rs.getString("correlation_id"), rs.getString("aprovacao_id"),
            rs.getString("usuario_solicitante"), rs.getString("solicitado_por"), rs.getString("metrica"), JsonBancoUtil.toMap(rs.getString("payload_json")),
            StatusItemFila.valueOf(rs.getString("status")), rs.getInt("tentativas"), rs.getTimestamp("criado_em").toInstant(),
            rs.getTimestamp("atualizado_em").toInstant(), rs.getString("mensagem"));

    public FilaConsultaSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public ItemFilaConsulta enfileirarReprocessamento(SolicitacaoAprovacao aprovacao, String solicitadoPor) {
        var agora = Instant.now();
        var item = new ItemFilaConsulta(UUID.randomUUID().toString(), "REPROCESSAMENTO_APROVACAO", aprovacao.correlationId(), aprovacao.id(), aprovacao.usuarioSolicitante(), solicitadoPor, aprovacao.metrica(),
                Map.of("filtros", aprovacao.filtros(), "motivos", aprovacao.motivos()), StatusItemFila.PENDENTE, 0, agora, agora, "Aguardando worker assíncrono.");
        jdbcTemplate.update("INSERT INTO " + table("fila_consulta") + " (id, tipo, correlation_id, aprovacao_id, usuario_solicitante, solicitado_por, metrica, payload_json, status, tentativas, criado_em, atualizado_em, mensagem) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                item.id(), item.tipo(), item.correlationId(), item.aprovacaoId(), item.usuarioSolicitante(), item.solicitadoPor(), item.metrica(), JsonBancoUtil.toJson(item.payload()), item.status().name(), item.tentativas(), Timestamp.from(item.criadoEm()), Timestamp.from(item.atualizadoEm()), item.mensagem());
        return item;
    }

    @Override public Optional<ItemFilaConsulta> buscar(String id) { return jdbcTemplate.query("SELECT TOP (1) * FROM " + table("fila_consulta") + " WHERE id=?", mapper, id).stream().findFirst(); }
    @Override public List<ItemFilaConsulta> listarPendentes(int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("fila_consulta") + " WHERE status='PENDENTE' ORDER BY criado_em ASC", mapper); }
    @Override public List<ItemFilaConsulta> listarRecentes(int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("fila_consulta") + " ORDER BY criado_em DESC", mapper); }
    @Override public ItemFilaConsulta marcarEmProcessamento(String id) { return atualizar(id, StatusItemFila.EM_PROCESSAMENTO, "Item em processamento.", true); }
    @Override public ItemFilaConsulta concluir(String id, String mensagem) { return atualizar(id, StatusItemFila.CONCLUIDA, mensagem, false); }
    @Override public ItemFilaConsulta falhar(String id, String mensagem) { return atualizar(id, StatusItemFila.FALHA, mensagem, false); }

    private ItemFilaConsulta atualizar(String id, StatusItemFila status, String mensagem, boolean incrementaTentativa) {
        jdbcTemplate.update("UPDATE " + table("fila_consulta") + " SET status=?, mensagem=?, atualizado_em=SYSUTCDATETIME(), tentativas = tentativas + ? WHERE id=?",
                status.name(), mensagem, incrementaTentativa ? 1 : 0, id);
        return buscar(id).orElseThrow(() -> new IllegalArgumentException("Item de fila não encontrado: " + id));
    }
}
