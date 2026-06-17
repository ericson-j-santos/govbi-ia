package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.Objects;

public record PerguntaAnalitica(
        String texto,
        String formatoResposta,
        boolean exibirSql,
        Instant solicitadaEm
) {
    public PerguntaAnalitica {
        Objects.requireNonNull(texto, "texto da pergunta é obrigatório");
        if (texto.isBlank()) {
            throw new IllegalArgumentException("texto da pergunta não pode ser vazio");
        }
        formatoResposta = formatoResposta == null || formatoResposta.isBlank() ? "tabela" : formatoResposta;
        solicitadaEm = solicitadaEm == null ? Instant.now() : solicitadaEm;
    }
}
