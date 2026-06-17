package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import br.com.reqsys.govbi.dominio.porta.FilaConsultaPort;
import org.springframework.stereotype.Service;

@Service
public class ReprocessarAprovacaoUseCase {
    private final AprovacaoHumanaPort aprovacaoHumanaPort;
    private final FilaConsultaPort filaConsultaPort;

    public ReprocessarAprovacaoUseCase(AprovacaoHumanaPort aprovacaoHumanaPort, FilaConsultaPort filaConsultaPort) {
        this.aprovacaoHumanaPort = aprovacaoHumanaPort;
        this.filaConsultaPort = filaConsultaPort;
    }

    public ItemFilaConsulta reprocessar(String aprovacaoId, String solicitadoPor) {
        var aprovacao = aprovacaoHumanaPort.buscar(aprovacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Aprovação não encontrada: " + aprovacaoId));
        if (aprovacao.status() != StatusAprovacao.APROVADA) {
            throw new IllegalStateException("Somente aprovações APROVADAS podem ser reprocessadas.");
        }
        return filaConsultaPort.enfileirarReprocessamento(aprovacao, solicitadoPor);
    }
}
