package br.com.reqsys.govbi.infraestrutura.adapter.auditoria;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.AuditoriaPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AuditoriaLogAdapter implements AuditoriaPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaLogAdapter.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void registrar(String correlationId, UsuarioContexto usuarioContexto, PerguntaAnalitica pergunta, ConsultaGerada consulta, ResultadoConsulta resultado) {
        try {
            MDC.put("correlation_id", correlationId);
            Map<String, Object> evento = new LinkedHashMap<>();
            evento.put("evento", "consulta_analitica_executada");
            evento.put("correlation_id", correlationId);
            evento.put("usuario_hash", sha256(usuarioContexto.usuario()));
            evento.put("perfil", usuarioContexto.perfil());
            evento.put("escopo_unidade", usuarioContexto.escopoUnidade());
            evento.put("linhas", resultado.linhas().size());
            evento.put("colunas", resultado.colunas());
            evento.put("mascaramento", consulta.mascaramentoNecessario());
            evento.put("pergunta_hash", sha256(pergunta.texto()));
            evento.put("sql_hash", sha256(consulta.sql()));
            LOGGER.info(mapper.writeValueAsString(evento));
        } catch (Exception e) {
            LOGGER.warn("event=audit_fallback correlation_id={} motivo={}", correlationId, e.getClass().getSimpleName());
        } finally {
            MDC.remove("correlation_id");
        }
    }

    private String sha256(String valor) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest((valor == null ? "" : valor).getBytes(StandardCharsets.UTF_8)));
    }
}
