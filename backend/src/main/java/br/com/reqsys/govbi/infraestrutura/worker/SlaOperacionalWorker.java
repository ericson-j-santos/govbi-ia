package br.com.reqsys.govbi.infraestrutura.worker;

import br.com.reqsys.govbi.aplicacao.caso_uso.ExpirarAprovacoesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlaOperacionalWorker {
    private static final Logger log = LoggerFactory.getLogger(SlaOperacionalWorker.class);
    private final ExpirarAprovacoesUseCase expirarAprovacoesUseCase;
    private final boolean habilitado;

    public SlaOperacionalWorker(ExpirarAprovacoesUseCase expirarAprovacoesUseCase,
                                @Value("${govbi.sla.expirar-aprovacoes-habilitado:false}") boolean habilitado) {
        this.expirarAprovacoesUseCase = expirarAprovacoesUseCase;
        this.habilitado = habilitado;
    }

    @Scheduled(fixedDelayString = "${govbi.sla.intervalo-ms:300000}")
    public void expirar() {
        if (!habilitado) return;
        var resumo = expirarAprovacoesUseCase.executar();
        if (resumo.values().stream().mapToInt(Integer::intValue).sum() > 0) {
            log.info("sla_operacional_executado resumo={}", resumo);
        }
    }
}
