package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import br.com.reqsys.govbi.infraestrutura.config.ConsultaDadosProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "govbi.dados.executor", havingValue = "sqlserver")
public class ExecutorConsultaSqlServerAdapter extends ExecutorConsultaJdbcBaseAdapter {
    private static final java.util.regex.Pattern FETCH_FIRST_FINAL = java.util.regex.Pattern.compile("(?is)\\s+FETCH\\s+FIRST\\s+(\\d+)\\s+ROWS\\s+ONLY\\s*$");

    public ExecutorConsultaSqlServerAdapter(ConsultaDadosProperties properties) {
        super("SQL Server", properties, properties.getSqlserver());
    }

    @Override
    protected String prepararSqlParaExecucao(String sqlOriginal) {
        return FETCH_FIRST_FINAL.matcher(sqlOriginal).replaceFirst(" OFFSET 0 ROWS FETCH NEXT $1 ROWS ONLY");
    }

    @Override
    protected String sqlDryRun(String sqlOriginal) {
        String sql = SqlGovernadoUtils.limparParaSubconsulta(sqlOriginal);
        return "SELECT TOP (1) * FROM (" + sql + ") AS govbi_dry_run";
    }
}
