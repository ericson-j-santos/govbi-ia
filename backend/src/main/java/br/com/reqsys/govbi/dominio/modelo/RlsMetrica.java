package br.com.reqsys.govbi.dominio.modelo;

public record RlsMetrica(
        String campoUnidade,
        String joinObrigatorio,
        String escopoGeral
) {
    public static RlsMetrica padrao() {
        return new RlsMetrica("u.codigo_unidade", "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade", "GERAL");
    }
}
