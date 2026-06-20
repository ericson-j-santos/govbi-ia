package br.com.reqsys.govbi.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {
    @Test
    void deveTratarEstadoInvalidoComoConflitoPadronizado() {
        var handler = new ApiExceptionHandler();

        var resposta = handler.tratarEstadoInvalido(new IllegalStateException("Solicitação de aprovação já decidida."));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).containsEntry("erro", "ESTADO_INVALIDO");
        assertThat(resposta.getBody()).containsEntry("mensagem", "Solicitação de aprovação já decidida.");
        assertThat(resposta.getBody()).containsKey("timestamp");
    }
}
