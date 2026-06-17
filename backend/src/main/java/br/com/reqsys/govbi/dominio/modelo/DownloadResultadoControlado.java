package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;

public record DownloadResultadoControlado(
        String id,
        String resultadoId,
        String correlationId,
        String formato,
        String nomeArquivo,
        String contentType,
        byte[] conteudo,
        int totalLinhas,
        boolean mascarado,
        Instant geradoEm
) {}
