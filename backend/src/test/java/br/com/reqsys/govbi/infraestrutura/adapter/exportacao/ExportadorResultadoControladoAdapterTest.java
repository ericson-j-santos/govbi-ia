package br.com.reqsys.govbi.infraestrutura.adapter.exportacao;

import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExportadorResultadoControladoAdapterTest {
    @Test
    void deveExportarCsvControlado() {
        var adapter = new ExportadorResultadoControladoAdapter(10);
        var resultado = new ResultadoConsulta(List.of("ano_mes", "valor"), List.of(Map.of("ano_mes", "2025-01", "valor", 10)));
        var arquivo = adapter.exportar(resultado, "csv", new UsuarioContexto("u", "ANALISTA", "GERAL"), "corr");
        assertThat(arquivo.contentType()).isEqualTo("text/csv");
        assertThat(new String(arquivo.conteudo(), StandardCharsets.UTF_8)).contains("ano_mes,valor");
    }
}
