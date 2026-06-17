package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.Map;

public record ItemFilaConsulta(
        String id,
        String tipo,
        String correlationId,
        String aprovacaoId,
        String usuarioSolicitante,
        String solicitadoPor,
        String metrica,
        Map<String, Object> payload,
        StatusItemFila status,
        int tentativas,
        Instant criadoEm,
        Instant atualizadoEm,
        String mensagem
) {
    public ItemFilaConsulta comStatus(StatusItemFila novoStatus, int novasTentativas, String novaMensagem) {
        return new ItemFilaConsulta(id, tipo, correlationId, aprovacaoId, usuarioSolicitante, solicitadoPor, metrica,
                payload, novoStatus, novasTentativas, criadoEm, Instant.now(), novaMensagem);
    }
}
