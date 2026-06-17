package br.com.reqsys.govbi.aplicacao.dto;

import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.StatusFluxoConsulta;
import br.com.reqsys.govbi.dominio.modelo.TentativaGeracaoConsulta;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;

import java.util.List;
import java.util.Map;

public record RespostaAnalitica(
        String correlationId,
        String intencao,
        String metrica,
        List<String> dimensoes,
        Map<String, Object> filtros,
        String sqlGerado,
        ResultadoConsulta resultado,
        List<String> avisos,
        boolean mascaramentoAplicado,
        String explicacao,
        String nivelSensibilidade,
        List<TrechoCatalogoSemantico> contextoSemantico,
        List<TentativaGeracaoConsulta> tentativas,
        long linhasEstimadas,
        double custoEstimado,
        StatusFluxoConsulta statusFluxo,
        boolean requerAprovacao,
        String aprovacaoId,
        String historicoId,
        List<String> exportacoesPermitidas
) {
}
