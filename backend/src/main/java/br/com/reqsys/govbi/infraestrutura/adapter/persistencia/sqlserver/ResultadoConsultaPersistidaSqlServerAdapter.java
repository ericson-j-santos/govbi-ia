package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.ResultadoAnaliticoPersistido;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
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
public class ResultadoConsultaPersistidaSqlServerAdapter extends SqlServerOperacionalJdbc implements ResultadoConsultaPersistidaPort {
    private final RowMapper<ResultadoAnaliticoPersistido> mapper = (rs, rowNum) -> new ResultadoAnaliticoPersistido(
            rs.getString("id"), rs.getString("fila_id"), rs.getString("aprovacao_id"), rs.getString("correlation_id"), rs.getString("metrica"),
            JsonBancoUtil.toStringList(rs.getString("colunas_json")), JsonBancoUtil.toListMap(rs.getString("linhas_json")), rs.getInt("total_linhas"),
            rs.getTimestamp("criado_em").toInstant(), rs.getTimestamp("expira_em") == null ? null : rs.getTimestamp("expira_em").toInstant(),
            rs.getString("status_retencao"), rs.getString("mensagem"));

    public ResultadoConsultaPersistidaSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public ResultadoAnaliticoPersistido salvar(String filaId, String aprovacaoId, String correlationId, String metrica, ResultadoConsulta resultado, Instant expiraEm, String mensagem) {
        var id = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO " + table("resultado_consulta") + " (id, fila_id, aprovacao_id, correlation_id, metrica, colunas_json, linhas_json, total_linhas, criado_em, expira_em, status_retencao, mensagem) VALUES (?,?,?,?,?,?,?,?,SYSUTCDATETIME(),?,?,?)",
                id, filaId, aprovacaoId, correlationId, metrica, JsonBancoUtil.toJson(resultado.colunas()), JsonBancoUtil.toJson(resultado.linhas()), resultado.linhas().size(),
                expiraEm == null ? null : Timestamp.from(expiraEm), "ATIVO", mensagem);
        return buscar(id).orElseThrow(() -> new IllegalStateException("Resultado persistido não encontrado após inserção."));
    }

    @Override public Optional<ResultadoAnaliticoPersistido> buscar(String id) { return jdbcTemplate.query("SELECT TOP (1) * FROM " + table("resultado_consulta") + " WHERE id=?", mapper, id).stream().findFirst(); }
    @Override public List<ResultadoAnaliticoPersistido> listarRecentes(int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("resultado_consulta") + " ORDER BY criado_em DESC", mapper); }
    @Override public List<ResultadoAnaliticoPersistido> listarPorAprovacao(String aprovacaoId, int limite) { return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("resultado_consulta") + " WHERE aprovacao_id=? ORDER BY criado_em DESC", mapper, aprovacaoId); }
    @Override public int expirarResultadosVencidos(Instant agora) { return jdbcTemplate.update("UPDATE " + table("resultado_consulta") + " SET status_retencao='EXPIRADO', linhas_json='[]', total_linhas=0, mensagem='Resultado removido por política de retenção.' WHERE status_retencao='ATIVO' AND expira_em IS NOT NULL AND expira_em < ?", Timestamp.from(agora)); }
}
