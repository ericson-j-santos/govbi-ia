package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;

public record TentativaGeracaoConsulta(
        int rodada,
        boolean aprovada,
        List<String> erros,
        List<String> avisos
) {
}
