package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ResultadoAnaliticoPersistido(
        String id,
        String filaId,
        String aprovacaoId,
        String correlationId,
        String metrica,
        List<String> colunas,
        List<Map<String, Object>> linhas,
        int totalLinhas,
        Instant criadoEm,
        Instant expiraEm,
        String statusRetencao,
        String mensagem
) {
    public boolean expirado(Instant agora) {
        return expiraEm != null && expiraEm.isBefore(agora);
    }
}
