package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;
import java.util.Map;

public record PlanoConsulta(
        String intencao,
        String metrica,
        List<String> dimensoes,
        Map<String, Object> filtros,
        List<String> avisos,
        String nivelSensibilidade,
        boolean requerAprovacao
) {
}
