package br.com.reqsys.govbi.api.dto;

import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import jakarta.validation.constraints.NotNull;

public record ExportacaoRequest(
        @NotNull ResultadoConsulta resultado,
        String formato,
        String correlationId
) {
}
