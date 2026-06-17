package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.RegistroHistoricoConversa;
import br.com.reqsys.govbi.dominio.modelo.StatusFluxoConsulta;
import br.com.reqsys.govbi.dominio.porta.HistoricoConversacionalPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class HistoricoConversacionalSqlServerAdapter extends SqlServerOperacionalJdbc implements HistoricoConversacionalPort {
    private final RowMapper<RegistroHistoricoConversa> mapper = (rs, rowNum) -> new RegistroHistoricoConversa(
            rs.getString("id"), rs.getString("correlation_id"), rs.getString("usuario_hash"), rs.getString("pergunta_hash"),
            rs.getString("metrica"), JsonBancoUtil.toStringList(rs.getString("dimensoes_json")), JsonBancoUtil.toMap(rs.getString("filtros_json")),
            StatusFluxoConsulta.valueOf(rs.getString("status")), rs.getString("aprovacao_id"), rs.getInt("total_linhas"), rs.getTimestamp("registrado_em").toInstant());

    public HistoricoConversacionalSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public RegistroHistoricoConversa registrar(RegistroHistoricoConversa r) {
        jdbcTemplate.update("INSERT INTO " + table("historico_conversa") + " (id, correlation_id, usuario_hash, pergunta_hash, metrica, dimensoes_json, filtros_json, status, aprovacao_id, total_linhas, registrado_em) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                r.id(), r.correlationId(), r.usuarioHash(), r.perguntaHash(), r.metrica(), JsonBancoUtil.toJson(r.dimensoes()), JsonBancoUtil.toJson(r.filtros()), r.status().name(), r.aprovacaoId(), r.totalLinhas(), Timestamp.from(r.registradoEm()));
        return r;
    }

    @Override
    public List<RegistroHistoricoConversa> listarPorUsuarioHash(String usuarioHash, int limite) {
        return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("historico_conversa") + " WHERE usuario_hash=? ORDER BY registrado_em DESC", mapper, usuarioHash);
    }

    @Override
    public List<RegistroHistoricoConversa> listarRecentes(int limite) {
        return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("historico_conversa") + " ORDER BY registrado_em DESC", mapper);
    }
}
