package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class AprovacaoHumanaSqlServerAdapter extends SqlServerOperacionalJdbc implements AprovacaoHumanaPort {
    private final RowMapper<SolicitacaoAprovacao> mapper = (rs, rowNum) -> new SolicitacaoAprovacao(
            rs.getString("id"), rs.getString("correlation_id"), rs.getString("usuario_solicitante"),
            rs.getString("perfil_solicitante"), rs.getString("escopo_unidade"), rs.getString("pergunta_hash"),
            rs.getString("metrica"), rs.getString("nivel_sensibilidade"), JsonBancoUtil.toStringList(rs.getString("motivos_json")),
            JsonBancoUtil.toMap(rs.getString("filtros_json")), StatusAprovacao.valueOf(rs.getString("status")),
            rs.getTimestamp("criada_em").toInstant(), rs.getTimestamp("expira_em").toInstant(), rs.getString("decisor"),
            rs.getTimestamp("decidida_em") == null ? null : rs.getTimestamp("decidida_em").toInstant(), rs.getString("justificativa"));

    public AprovacaoHumanaSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public SolicitacaoAprovacao solicitar(SolicitacaoAprovacao s) {
        jdbcTemplate.update("INSERT INTO " + table("aprovacao_consulta") + " (id, correlation_id, usuario_solicitante, perfil_solicitante, escopo_unidade, pergunta_hash, metrica, nivel_sensibilidade, motivos_json, filtros_json, status, criada_em, expira_em) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                s.id(), s.correlationId(), s.usuarioSolicitante(), s.perfilSolicitante(), s.escopoUnidade(), s.perguntaHash(), s.metrica(), s.nivelSensibilidade(),
                JsonBancoUtil.toJson(s.motivos()), JsonBancoUtil.toJson(s.filtros()), s.status().name(), Timestamp.from(s.criadaEm()), Timestamp.from(s.expiraEm()));
        return s;
    }

    @Override
    public Optional<SolicitacaoAprovacao> buscar(String id) {
        var lista = jdbcTemplate.query("SELECT TOP (1) * FROM " + table("aprovacao_consulta") + " WHERE id = ?", mapper, id);
        return lista.stream().findFirst();
    }

    @Override
    public List<SolicitacaoAprovacao> listarPendentes() {
        return jdbcTemplate.query("SELECT TOP (" + properties.maxRowsAdmin() + ") * FROM " + table("aprovacao_consulta") + " WHERE status = 'PENDENTE' ORDER BY criada_em DESC", mapper);
    }

    @Override
    public SolicitacaoAprovacao decidir(String id, StatusAprovacao decisao, String decisor, String justificativa) {
        var atual = buscar(id).orElseThrow(() -> new IllegalArgumentException("Solicitação de aprovação não encontrada: " + id));
        if (atual.status() != StatusAprovacao.PENDENTE) {
            throw new IllegalStateException("Solicitação de aprovação já decidida: " + id);
        }
        var decidida = atual.comDecisao(decisao, decisor, justificativa);
        jdbcTemplate.update("UPDATE " + table("aprovacao_consulta") + " SET status=?, decisor=?, decidida_em=?, justificativa=? WHERE id=? AND status='PENDENTE'",
                decidida.status().name(), decidida.decisor(), Timestamp.from(decidida.decididaEm()), decidida.justificativa(), id);
        return decidida;
    }
}
