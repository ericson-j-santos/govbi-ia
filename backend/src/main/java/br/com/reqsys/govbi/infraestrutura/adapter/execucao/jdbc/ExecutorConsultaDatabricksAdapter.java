package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import br.com.reqsys.govbi.infraestrutura.config.ConsultaDadosProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "govbi.dados.executor", havingValue = "databricks")
public class ExecutorConsultaDatabricksAdapter extends ExecutorConsultaJdbcBaseAdapter {
    private static final java.util.regex.Pattern FETCH_FIRST_FINAL = java.util.regex.Pattern.compile("(?is)\\s+FETCH\\s+FIRST\\s+(\\d+)\\s+ROWS\\s+ONLY\\s*$");

    public ExecutorConsultaDatabricksAdapter(ConsultaDadosProperties properties) {
        super("Databricks", properties, properties.getDatabricks());
    }

    @Override
    protected String prepararSqlParaExecucao(String sqlOriginal) {
        return FETCH_FIRST_FINAL.matcher(sqlOriginal).replaceFirst(" LIMIT $1");
    }

    @Override
    protected String sqlDryRun(String sqlOriginal) {
        String sql = SqlGovernadoUtils.limparParaSubconsulta(sqlOriginal);
        return "SELECT * FROM (" + sql + ") govbi_dry_run LIMIT 1";
    }
}
