package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;
import java.util.Map;

public record MetricaSemantica(
        String nome,
        String descricao,
        String tabelaFato,
        String agregacao,
        String filtroPadrao,
        List<String> dimensoesPermitidas,
        List<String> camposSensiveis,
        Map<String, String> joinsPorDimensao,
        PoliticaMetrica politicaAcesso
) {
    public MetricaSemantica(
            String nome,
            String descricao,
            String tabelaFato,
            String agregacao,
            String filtroPadrao,
            List<String> dimensoesPermitidas,
            List<String> camposSensiveis,
            Map<String, String> joinsPorDimensao
    ) {
        this(nome, descricao, tabelaFato, agregacao, filtroPadrao, dimensoesPermitidas, camposSensiveis, joinsPorDimensao, PoliticaMetrica.padrao());
    }

    public boolean permiteDimensao(String dimensao) {
        return dimensoesPermitidas.contains(dimensao);
    }
}
