package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlGovernadoUtilsTest {
    @Test
    void deveDetectarFiltroTemporal() {
        assertTrue(SqlGovernadoUtils.possuiFiltroTemporal("select t.ano_mes, count(*) from gold.fato_proposta p join gold.dim_tempo t on 1=1 where t.ano = 2025 group by t.ano_mes"));
    }

    @Test
    void deveRemoverOrderByParaSubconsultaDeDryRun() {
        String sql = SqlGovernadoUtils.limparParaSubconsulta("SELECT x FROM gold.t ORDER BY x DESC");
        assertFalse(sql.toLowerCase().contains("order by"));
    }
}
