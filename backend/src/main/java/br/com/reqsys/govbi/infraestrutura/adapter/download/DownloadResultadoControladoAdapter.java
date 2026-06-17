package br.com.reqsys.govbi.infraestrutura.adapter.download;

import br.com.reqsys.govbi.dominio.modelo.DownloadResultadoControlado;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.DownloadResultadoPort;
import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DownloadResultadoControladoAdapter implements DownloadResultadoPort {
    private final ResultadoConsultaPersistidaPort resultadoPort;
    private final int limiteLinhas;

    public DownloadResultadoControladoAdapter(ResultadoConsultaPersistidaPort resultadoPort,
                                              @Value("${integracao-corporativa.download.limite-linhas:5000}") int limiteLinhas) {
        this.resultadoPort = resultadoPort;
        this.limiteLinhas = Math.max(1, limiteLinhas);
    }

    @Override
    public DownloadResultadoControlado gerar(String resultadoId, String formato, UsuarioContexto usuario) {
        if (!usuario.possuiPerfilAnalitico()) {
            throw new SecurityException("Usuário sem perfil autorizado para download controlado.");
        }
        var r = resultadoPort.buscar(resultadoId).orElseThrow(() -> new IllegalArgumentException("Resultado não encontrado: " + resultadoId));
        if (!"ATIVO".equalsIgnoreCase(r.statusRetencao())) throw new IllegalStateException("Resultado fora da política de retenção ativa.");
        var linhas = r.linhas().size() > limiteLinhas ? r.linhas().subList(0, limiteLinhas) : r.linhas();
        var resultadoLimitado = new ResultadoConsulta(r.colunas(), linhas);
        var fmt = formato == null ? "csv" : formato.toLowerCase();
        byte[] conteudo;
        String contentType;
        if ("json".equals(fmt)) {
            conteudo = json(resultadoLimitado).getBytes(StandardCharsets.UTF_8);
            contentType = "application/json";
        } else if ("csv".equals(fmt)) {
            conteudo = csv(resultadoLimitado).getBytes(StandardCharsets.UTF_8);
            contentType = "text/csv";
        } else {
            throw new IllegalArgumentException("Formato de download não permitido: " + formato);
        }
        return new DownloadResultadoControlado(UUID.randomUUID().toString(), resultadoId, r.correlationId(), fmt,
                "govbi-resultado-" + r.correlationId() + "." + fmt, contentType, conteudo, linhas.size(), true, Instant.now());
    }

    private String csv(ResultadoConsulta resultado) {
        var sb = new StringBuilder();
        sb.append(String.join(";", resultado.colunas())).append("\n");
        for (var linha : resultado.linhas()) {
            sb.append(resultado.colunas().stream().map(c -> escape(String.valueOf(linha.getOrDefault(c, "")))).collect(Collectors.joining(";"))).append("\n");
        }
        return sb.toString();
    }
    private String escape(String valor) { return "\"" + valor.replace("\"", "\"\"") + "\""; }
    private String json(ResultadoConsulta resultado) { return "{\"colunas\":" + list(resultado.colunas()) + ",\"linhas\":" + resultado.linhas().toString().replace("=", ":") + "}"; }
    private String list(List<String> values) { return values.stream().map(v -> "\"" + v + "\"").collect(Collectors.joining(",", "[", "]")); }
}
