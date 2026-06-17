package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.EventoAuditoriaConsulta;
import br.com.reqsys.govbi.dominio.porta.AuditoriaConsultavelPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class AuditoriaConsultavelSqlServerAdapter extends SqlServerOperacionalJdbc implements AuditoriaConsultavelPort {
    private final RowMapper<EventoAuditoriaConsulta> mapper = (rs, rowNum) -> new EventoAuditoriaConsulta(
            rs.getString("id"), rs.getString("correlation_id"), rs.getString("tipo_evento"), rs.getString("usuario_hash"),
            rs.getString("perfil"), rs.getString("escopo_unidade"), rs.getString("metrica"), rs.getString("sql_hash"),
            rs.getInt("linhas"), JsonBancoUtil.toStringList(rs.getString("colunas_json")), rs.getString("status"), rs.getTimestamp("registrado_em").toInstant());

    public AuditoriaConsultavelSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public EventoAuditoriaConsulta registrar(EventoAuditoriaConsulta e) {
        jdbcTemplate.update("INSERT INTO " + table("auditoria_consulta") + " (id, correlation_id, tipo_evento, usuario_hash, perfil, escopo_unidade, metrica, sql_hash, linhas, colunas_json, status, registrado_em) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                e.id(), e.correlationId(), e.tipoEvento(), e.usuarioHash(), e.perfil(), e.escopoUnidade(), e.metrica(), e.sqlHash(), e.linhas(), JsonBancoUtil.toJson(e.colunas()), e.status(), Timestamp.from(e.registradoEm()));
        return e;
    }

    @Override
    public List<EventoAuditoriaConsulta> listarRecentes(int limite) {
        return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("auditoria_consulta") + " ORDER BY registrado_em DESC", mapper);
    }

    @Override
    public List<EventoAuditoriaConsulta> buscarPorCorrelationId(String correlationId) {
        return jdbcTemplate.query("SELECT * FROM " + table("auditoria_consulta") + " WHERE correlation_id=? ORDER BY registrado_em DESC", mapper, correlationId);
    }
}
