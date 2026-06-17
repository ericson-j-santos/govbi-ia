package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ReleaseReadinessStatus(
        String produto,
        String versao,
        String status,
        Instant verificadoEm,
        List<String> gatesObrigatorios,
        Map<String, String> configuracoesCriticas,
        List<String> pendenciasProducao
) {
    public boolean aptoParaHomologacao() {
        return pendenciasProducao == null || pendenciasProducao.isEmpty();
    }
}
