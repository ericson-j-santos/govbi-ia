package br.com.reqsys.govbi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DecisaoAprovacaoRequest(
        @NotBlank String decisao,
        @NotBlank String justificativa
) {
}
