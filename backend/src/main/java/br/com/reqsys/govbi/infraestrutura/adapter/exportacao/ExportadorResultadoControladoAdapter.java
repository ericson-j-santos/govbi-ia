package br.com.reqsys.govbi.infraestrutura.adapter.exportacao;

import br.com.reqsys.govbi.dominio.modelo.ExportacaoResultado;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.ExportadorResultadoPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ExportadorResultadoControladoAdapter implements ExportadorResultadoPort {
    private final int limiteLinhas;
    private final ObjectMapper mapper = new ObjectMapper();

    public ExportadorResultadoControladoAdapter(@Value("${produto-operacional.exportacao.limite-linhas:5000}") int limiteLinhas) {
        this.limiteLinhas = Math.max(1, limiteLinhas);
    }

    @Override
    public ExportacaoResultado exportar(ResultadoConsulta resultado, String formato, UsuarioContexto usuarioContexto, String correlationId) {
        if (!usuarioContexto.possuiAlgumPerfil("ANALISTA", "ADMIN", "BI_GOVERNADO")) {
            throw new SecurityException("Usuário sem perfil autorizado para exportação controlada.");
        }
        if (resultado.linhas().size() > limiteLinhas) {
            throw new IllegalArgumentException("Exportação excede limite de linhas configurado: " + limiteLinhas);
        }
        String normalizado = formato == null ? "csv" : formato.toLowerCase(Locale.ROOT).trim();
        try {
            if (normalizado.equals("json")) {
                byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(resultado);
                return new ExportacaoResultado("govbi-resultado-" + correlationId + ".json", "application/json", bytes, resultado.linhas().size(), Instant.now());
            }
            String cabecalho = String.join(",", resultado.colunas());
            String linhas = resultado.linhas().stream().map(linha -> resultado.colunas().stream()
                    .map(c -> escaparCsv(String.valueOf(linha.getOrDefault(c, ""))))
                    .collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
            String csv = cabecalho + "\n" + linhas + "\n";
            return new ExportacaoResultado("govbi-resultado-" + correlationId + ".csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8), resultado.linhas().size(), Instant.now());
        } catch (Exception e) {
            throw new IllegalStateException("Falha na exportação controlada", e);
        }
    }

    private static String escaparCsv(String valor) {
        if (valor.contains(",") || valor.contains("\n") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
