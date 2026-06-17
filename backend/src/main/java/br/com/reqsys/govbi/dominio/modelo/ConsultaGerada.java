package br.com.reqsys.govbi.dominio.modelo;

public record ConsultaGerada(
        String sql,
        boolean mascaramentoNecessario,
        String explicacao
) {
}
