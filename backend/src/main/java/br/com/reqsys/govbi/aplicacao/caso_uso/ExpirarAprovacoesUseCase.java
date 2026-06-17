package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ExpirarAprovacoesUseCase {
    private final AprovacaoHumanaPort aprovacaoHumanaPort;
    private final ResultadoConsultaPersistidaPort resultadoConsultaPersistidaPort;
    private final NotificarOperacaoUseCase notificarOperacaoUseCase;

    public ExpirarAprovacoesUseCase(AprovacaoHumanaPort aprovacaoHumanaPort, ResultadoConsultaPersistidaPort resultadoConsultaPersistidaPort, NotificarOperacaoUseCase notificarOperacaoUseCase) {
        this.aprovacaoHumanaPort = aprovacaoHumanaPort;
        this.resultadoConsultaPersistidaPort = resultadoConsultaPersistidaPort;
        this.notificarOperacaoUseCase = notificarOperacaoUseCase;
    }

    public Map<String, Integer> executar() {
        var agora = Instant.now();
        int aprovacoesExpiradas = 0;
        for (var pendente : aprovacaoHumanaPort.listarPendentes()) {
            if (pendente.expiraEm() != null && pendente.expiraEm().isBefore(agora)) {
                aprovacaoHumanaPort.decidir(pendente.id(), StatusAprovacao.EXPIRADA, "sistema-sla", "Expirada automaticamente por SLA.");
                aprovacoesExpiradas++;
                notificarOperacaoUseCase.registrar("APROVACAO_EXPIRADA", "LOG", pendente.usuarioSolicitante(), "GovBI IA: aprovação expirada", "A solicitação de aprovação expirou por SLA.", Map.of("aprovacaoId", pendente.id(), "correlationId", pendente.correlationId()));
            }
        }
        int resultadosExpirados = resultadoConsultaPersistidaPort.expirarResultadosVencidos(agora);
        return Map.of("aprovacoesExpiradas", aprovacoesExpiradas, "resultadosExpirados", resultadosExpirados);
    }
}
