package br.com.reqsys.govbi.dominio.modelo;

public record TrechoCatalogoSemantico(
        String id,
        String tipo,
        String nome,
        String conteudo,
        double pontuacao
) {
}
