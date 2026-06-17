package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;

public record CatalogoAlteracao(
        String id,
        String usuario,
        String descricao,
        String status,
        String diffResumo,
        Instant registradaEm
) {
}
