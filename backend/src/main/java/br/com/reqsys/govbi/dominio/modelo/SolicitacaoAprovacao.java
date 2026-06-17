package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SolicitacaoAprovacao(
        String id,
        String correlationId,
        String usuarioSolicitante,
        String perfilSolicitante,
        String escopoUnidade,
        String perguntaHash,
        String metrica,
        String nivelSensibilidade,
        List<String> motivos,
        Map<String, Object> filtros,
        StatusAprovacao status,
        Instant criadaEm,
        Instant expiraEm,
        String decisor,
        Instant decididaEm,
        String justificativa
) {
    public SolicitacaoAprovacao comDecisao(StatusAprovacao novoStatus, String decisor, String justificativa) {
        return new SolicitacaoAprovacao(id, correlationId, usuarioSolicitante, perfilSolicitante, escopoUnidade,
                perguntaHash, metrica, nivelSensibilidade, motivos, filtros, novoStatus, criadaEm, expiraEm,
                decisor, Instant.now(), justificativa);
    }
}
