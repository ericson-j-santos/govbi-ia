package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;

public record DryRunConsulta(
        boolean aprovado,
        long linhasEstimadas,
        double custoEstimado,
        List<String> avisos,
        List<String> erros
) {
    public static DryRunConsulta aprovado(long linhasEstimadas, double custoEstimado, List<String> avisos) {
        return new DryRunConsulta(true, linhasEstimadas, custoEstimado, avisos, List.of());
    }

    public static DryRunConsulta bloqueado(List<String> erros) {
        return new DryRunConsulta(false, 0L, 0.0d, List.of(), erros);
    }
}
