package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.CatalogoAlteracao;
import br.com.reqsys.govbi.dominio.porta.CatalogoAdminPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "sqlserver")
public class CatalogoAdminSqlServerAdapter extends SqlServerOperacionalJdbc implements CatalogoAdminPort {
    private final Resource resource;
    private final boolean edicaoHabilitada;
    private final RowMapper<CatalogoAlteracao> mapper = (rs, rowNum) -> new CatalogoAlteracao(
            rs.getString("id"), rs.getString("usuario"), rs.getString("descricao"), rs.getString("status"), rs.getString("diff_resumo"), rs.getTimestamp("registrada_em").toInstant());

    public CatalogoAdminSqlServerAdapter(
            SqlServerOperacionalProperties properties,
            @Value("classpath:catalogo-semantico.yml") Resource resource,
            @Value("${produto-operacional.admin.catalogo-edicao-habilitada:false}") boolean edicaoHabilitada
    ) {
        super(properties);
        this.resource = resource;
        this.edicaoHabilitada = edicaoHabilitada;
    }

    @Override
    public String obterYamlAtual() {
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível ler catálogo semântico", e);
        }
    }

    @Override
    public CatalogoAlteracao proporAlteracao(String usuario, String descricao, String novoYaml) {
        if (novoYaml == null || !novoYaml.contains("versao:") || !novoYaml.contains("metricas:")) {
            throw new IllegalArgumentException("Catálogo proposto inválido: versão e métricas são obrigatórias.");
        }
        var alteracao = new CatalogoAlteracao(UUID.randomUUID().toString(), usuario, descricao,
                edicaoHabilitada ? "PROPOSTA_REGISTRADA" : "PROPOSTA_REGISTRADA_SEM_APLICACAO_AUTOMATICA",
                "Alteração proposta com " + novoYaml.length() + " caracteres. Aplicação automática=" + edicaoHabilitada,
                Instant.now());
        jdbcTemplate.update("INSERT INTO " + table("catalogo_alteracao") + " (id, usuario, descricao, status, diff_resumo, novo_yaml_hash, registrada_em) VALUES (?,?,?,?,?,?,?)",
                alteracao.id(), alteracao.usuario(), alteracao.descricao(), alteracao.status(), alteracao.diffResumo(), Integer.toHexString(novoYaml.hashCode()), Timestamp.from(alteracao.registradaEm()));
        return alteracao;
    }

    @Override
    public List<CatalogoAlteracao> listarAlteracoes(int limite) {
        return jdbcTemplate.query("SELECT TOP (" + Math.max(1, Math.min(limite, properties.maxRowsAdmin())) + ") * FROM " + table("catalogo_alteracao") + " ORDER BY registrada_em DESC", mapper);
    }
}
