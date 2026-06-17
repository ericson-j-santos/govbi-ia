package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;

public record PoliticaMetrica(
        List<String> perfisPermitidos,
        List<String> perfisAprovadoresPii,
        RlsMetrica rls
) {
    public static PoliticaMetrica padrao() {
        return new PoliticaMetrica(List.of("ANALISTA", "ADMIN", "BI_GOVERNADO"), List.of("ADMIN", "DPO", "SEGURANCA_DADOS"), RlsMetrica.padrao());
    }
}
