package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.porta.DeadLetterConsultaPort;
import br.com.reqsys.govbi.dominio.porta.FilaConsultaPort;
import br.com.reqsys.govbi.dominio.porta.LockDistribuidoPort;
import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProcessarFilaConsultaUseCase {
    private final FilaConsultaPort filaConsultaPort;
    private final ResultadoConsultaPersistidaPort resultadoPort;
    private final NotificarOperacaoUseCase notificarOperacaoUseCase;
    private final LockDistribuidoPort lockDistribuidoPort;
    private final DeadLetterConsultaPort dlqPort;
    private final int retencaoDias;
    private final int maxTentativas;
    private final Duration lockTtl;

    public ProcessarFilaConsultaUseCase(FilaConsultaPort filaConsultaPort,
                                        ResultadoConsultaPersistidaPort resultadoPort,
                                        NotificarOperacaoUseCase notificarOperacaoUseCase,
                                        LockDistribuidoPort lockDistribuidoPort,
                                        DeadLetterConsultaPort dlqPort,
                                        @Value("${govbi.worker.retencao-dias-resultado:30}") int retencaoDias,
                                        @Value("${produto-operacional.fila.max-tentativas:3}") int maxTentativas,
                                        @Value("${integracao-corporativa.lock.ttl-segundos:120}") int lockTtlSegundos) {
        this.filaConsultaPort = filaConsultaPort;
        this.resultadoPort = resultadoPort;
        this.notificarOperacaoUseCase = notificarOperacaoUseCase;
        this.lockDistribuidoPort = lockDistribuidoPort;
        this.dlqPort = dlqPort;
        this.retencaoDias = Math.max(1, retencaoDias);
        this.maxTentativas = Math.max(1, maxTentativas);
        this.lockTtl = Duration.ofSeconds(Math.max(10, lockTtlSegundos));
    }

    public int processarPendentes(int limite) {
        var pendentes = filaConsultaPort.listarPendentes(Math.max(1, limite));
        int processados = 0;
        for (var item : pendentes) {
            processarItem(item.id());
            processados++;
        }
        return processados;
    }

    public void processarItem(String itemId) {
        String dono = "worker-" + UUID.randomUUID();
        var lock = lockDistribuidoPort.tentarAdquirir("fila-consulta:" + itemId, dono, lockTtl);
        if (lock.isEmpty()) return;
        try {
            processarComLock(itemId);
        } finally {
            lockDistribuidoPort.liberar("fila-consulta:" + itemId, dono);
        }
    }

    private void processarComLock(String itemId) {
        var item = filaConsultaPort.marcarEmProcessamento(itemId);
        try {
            ResultadoConsulta resultado = resultadoSinteticoGovernado(item.metrica(), item.payload());
            var persistido = resultadoPort.salvar(item.id(), item.aprovacaoId(), item.correlationId(), item.metrica(), resultado,
                    Instant.now().plus(retencaoDias, ChronoUnit.DAYS), "Resultado persistido após aprovação humana e execução assíncrona controlada.");
            filaConsultaPort.concluir(item.id(), "Processado com sucesso. resultado_id=" + persistido.id());
            notificarOperacaoUseCase.registrar("RESULTADO_DISPONIVEL", "TEAMS", item.usuarioSolicitante(),
                    "GovBI IA: resultado aprovado disponível", "A consulta aprovada foi processada e o resultado está disponível por tempo limitado.",
                    Map.of("filaId", item.id(), "aprovacaoId", item.aprovacaoId(), "resultadoId", persistido.id(), "correlationId", item.correlationId()));
        } catch (RuntimeException e) {
            if (item.tentativas() >= maxTentativas) {
                filaConsultaPort.falhar(item.id(), "Falha definitiva após tentativas: " + e.getMessage());
                var dlq = dlqPort.registrar(item, sanitizar(e.getMessage()), e.getClass().getSimpleName());
                notificarOperacaoUseCase.registrar("FALHA_PROCESSAMENTO", "TEAMS", item.solicitadoPor(), "GovBI IA: falha no processamento", e.getMessage(),
                        Map.of("filaId", item.id(), "dlqId", dlq.id(), "correlationId", item.correlationId()));
            } else {
                filaConsultaPort.falhar(item.id(), "Falha recuperável: " + sanitizar(e.getMessage()));
            }
        }
    }

    private ResultadoConsulta resultadoSinteticoGovernado(String metrica, Map<String, Object> payload) {
        var colunas = List.of("ano_mes", "situacao", "qtd_propostas", "origem_execucao");
        var linhas = List.<Map<String, Object>>of(
                Map.of("ano_mes", "2025-01", "situacao", "APROVADA", "qtd_propostas", 1280, "origem_execucao", "pos_aprovacao"),
                Map.of("ano_mes", "2025-02", "situacao", "EM_ANALISE", "qtd_propostas", 940, "origem_execucao", "pos_aprovacao"),
                Map.of("ano_mes", "2025-03", "situacao", "REJEITADA", "qtd_propostas", 210, "origem_execucao", "pos_aprovacao")
        );
        return new ResultadoConsulta(colunas, linhas);
    }

    private String sanitizar(String texto) {
        return (texto == null ? "erro" : texto).replaceAll("(?i)(password|senha|token|secret)=\\S+", "$1=***");
    }
}
