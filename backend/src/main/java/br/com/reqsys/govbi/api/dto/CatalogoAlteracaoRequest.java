package br.com.reqsys.govbi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CatalogoAlteracaoRequest(
        @NotBlank String descricao,
        @NotBlank String novoYaml
) {
}
