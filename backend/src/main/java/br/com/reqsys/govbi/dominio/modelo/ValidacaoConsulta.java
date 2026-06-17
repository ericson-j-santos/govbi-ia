package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;

public record ValidacaoConsulta(
        boolean valida,
        List<String> erros,
        List<String> avisos
) {
    public static ValidacaoConsulta ok(List<String> avisos) {
        return new ValidacaoConsulta(true, List.of(), avisos);
    }

    public static ValidacaoConsulta erro(List<String> erros) {
        return new ValidacaoConsulta(false, erros, List.of());
    }
}
