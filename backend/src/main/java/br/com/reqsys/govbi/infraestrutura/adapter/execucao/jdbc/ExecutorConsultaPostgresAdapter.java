package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import br.com.reqsys.govbi.infraestrutura.config.ConsultaDadosProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adapter PostgreSQL — usado para consultas ao Redmine (Render) ou qualquer banco Postgres.
 * Dry-run usa LIMIT 1 em subconsulta; sintaxe FETCH FIRST N ROWS ONLY é nativa no Postgres 8.4+.
 */
@Component
@ConditionalOnProperty(name = "govbi.dados.executor", havingValue = "postgres")
public class ExecutorConsultaPostgresAdapter extends ExecutorConsultaJdbcBaseAdapter {

    public ExecutorConsultaPostgresAdapter(ConsultaDadosProperties properties) {
        super("PostgreSQL", properties, properties.getPostgres());
    }

    @Override
    protected String sqlDryRun(String sqlOriginal) {
        String sql = SqlGovernadoUtils.limparParaSubconsulta(sqlOriginal);
        return "SELECT * FROM (" + sql + ") AS govbi_dry_run LIMIT 1";
    }
}
