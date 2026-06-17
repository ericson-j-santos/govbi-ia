package br.com.reqsys.govbi.infraestrutura.worker;

import br.com.reqsys.govbi.aplicacao.caso_uso.ProcessarFilaConsultaUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FilaConsultaWorker {
    private static final Logger log = LoggerFactory.getLogger(FilaConsultaWorker.class);
    private final ProcessarFilaConsultaUseCase processarFilaConsultaUseCase;
    private final boolean habilitado;
    private final int lote;

    public FilaConsultaWorker(ProcessarFilaConsultaUseCase processarFilaConsultaUseCase,
                              @Value("${govbi.worker.habilitado:false}") boolean habilitado,
                              @Value("${govbi.worker.lote:5}") int lote) {
        this.processarFilaConsultaUseCase = processarFilaConsultaUseCase;
        this.habilitado = habilitado;
        this.lote = Math.max(1, lote);
    }

    @Scheduled(fixedDelayString = "${govbi.worker.intervalo-ms:30000}")
    public void processar() {
        if (!habilitado) return;
        int total = processarFilaConsultaUseCase.processarPendentes(lote);
        if (total > 0) log.info("fila_consulta_worker_processados total={}", total);
    }
}
