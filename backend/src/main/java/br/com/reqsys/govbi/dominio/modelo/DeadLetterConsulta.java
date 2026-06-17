package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.Map;

public record DeadLetterConsulta(
        String id,
        String filaId,
        String aprovacaoId,
        String correlationId,
        String metrica,
        String motivoFalha,
        String stackSanitizado,
        Map<String, Object> payload,
        int tentativasOriginais,
        int tentativasReprocessamento,
        String status,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public DeadLetterConsulta comoReprocessado(String novoStatus) {
        return new DeadLetterConsulta(id, filaId, aprovacaoId, correlationId, metrica, motivoFalha, stackSanitizado, payload,
                tentativasOriginais, tentativasReprocessamento + 1, novoStatus, criadoEm, Instant.now());
    }
}
