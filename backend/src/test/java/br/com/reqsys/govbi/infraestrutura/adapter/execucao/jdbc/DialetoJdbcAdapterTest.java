package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import br.com.reqsys.govbi.infraestrutura.config.ConsultaDadosProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DialetoJdbcAdapterTest {
    @Test
    void deveConverterFetchFirstParaSqlServer() {
        var adapter = new ExecutorConsultaSqlServerAdapter(new ConsultaDadosProperties());
        String sql = adapter.prepararSqlParaExecucao("SELECT x FROM gold.t ORDER BY x FETCH FIRST 500 ROWS ONLY");
        assertTrue(sql.contains("OFFSET 0 ROWS FETCH NEXT 500 ROWS ONLY"));
    }

    @Test
    void deveConverterFetchFirstParaDatabricks() {
        var adapter = new ExecutorConsultaDatabricksAdapter(new ConsultaDadosProperties());
        String sql = adapter.prepararSqlParaExecucao("SELECT x FROM gold.t ORDER BY x FETCH FIRST 500 ROWS ONLY");
        assertTrue(sql.endsWith("LIMIT 500"));
    }
}
