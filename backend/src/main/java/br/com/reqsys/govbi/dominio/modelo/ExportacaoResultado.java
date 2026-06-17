package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;

public record ExportacaoResultado(
        String nomeArquivo,
        String contentType,
        byte[] conteudo,
        int totalLinhas,
        Instant geradoEm
) {
}
