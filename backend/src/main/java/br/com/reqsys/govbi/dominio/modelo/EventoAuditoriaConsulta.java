package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.List;

public record EventoAuditoriaConsulta(
        String id,
        String correlationId,
        String tipoEvento,
        String usuarioHash,
        String perfil,
        String escopoUnidade,
        String metrica,
        String sqlHash,
        int linhas,
        List<String> colunas,
        String status,
        Instant registradoEm
) {
}
