package br.com.reqsys.govbi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PerguntaAnaliticaRequest(
        @NotBlank String pergunta,
        String formatoResposta,
        Boolean exibirSql
) {
}
