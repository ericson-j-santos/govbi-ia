package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.NotificacaoOperacionalPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class NotificacaoOperacionalSqlServerAdapter extends SqlServerOperacionalJdbc implements NotificacaoOperacionalPort {
    private final RowMapper<NotificacaoOperacional> mapper = (rs, rowNum) -> new NotificacaoOperacional(
            rs.getString("id"), rs.getString("tipo"), rs.getString("canal"), rs.getString("destinatario"), rs.getString("titulo"), rs.getString("mensagem"),
            JsonBancoUtil.toMap(rs.getString("metadados_json")), rs.getString("status"), rs.getTimestamp("criada_em").toInstant(),
            rs.getTimestamp("enviada_em") == null ? null : rs.getTimestamp("enviada_em").toInstant(), rs.getString("erro"));

    public NotificacaoOperacionalSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public NotificacaoOperacional registrar(NotificacaoOperacional n) {
        jdbcTemplate.update("INSERT INTO " + table("notificacao_operacional") + " (id, tipo, canal, destinatario, titulo, mensagem, metadados_json, status, criada_em, enviada_em, erro) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                n.id(), n.tipo(), n.canal(), n.destinatario(), n.titulo(), n.mensagem(), JsonBancoUtil.toJson(n.metadados()), n.status(), Timestamp.from(n.criadaEm()), n.enviadaEm() == null ? null : Timestamp.from(n.enviadaEm()), n.erro());
        return n;
    }

    @Override public NotificacaoOperacional marcarEnviada(String id) { jdbcTemplate.update("UPDATE " + table("notificacao_operacional") + " SET status='ENVIADA', enviada_em=SYSUTCDATETIME(), erro=NULL WHERE id=?", id); return buscar(id).orElseThrow(); }
    @Override public NotificacaoOperacional marcarFalha(String id, String erro) { jdbcTemplate.update("UPDATE " + table("notificacao_operacional") + " SET status='FALHA', enviada_em=SYSUTCDATETIME(), erro=? WHERE id=?", erro, id); return buscar(id).orElseThrow(); }
    @Override public Optional<NotificacaoOperacional> buscar(String id) { return jdbcTemplate.query("SELECT TOP (1) * FROM " + table("notificacao_operacional") + " WHERE id=?", mapper, id).stream().findFirst(); }
    @Override public List<NotificacaoOperacional> listarRecentes(int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("notificacao_operacional") + " ORDER BY criada_em DESC", mapper); }
}
