package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;
import java.util.Map;

public record ResultadoConsulta(
        List<String> colunas,
        List<Map<String, Object>> linhas
) {
}
