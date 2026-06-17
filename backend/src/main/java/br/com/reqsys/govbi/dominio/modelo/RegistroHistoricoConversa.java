package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record RegistroHistoricoConversa(
        String id,
        String correlationId,
        String usuarioHash,
        String perguntaHash,
        String metrica,
        List<String> dimensoes,
        Map<String, Object> filtros,
        StatusFluxoConsulta status,
        String aprovacaoId,
        int totalLinhas,
        Instant registradoEm
) {
}
