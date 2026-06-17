package br.com.reqsys.govbi.infraestrutura.adapter.notificacao;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.CanalNotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CanalNotificacaoLogAdapter implements CanalNotificacaoPort {
    private static final Logger log = LoggerFactory.getLogger(CanalNotificacaoLogAdapter.class);
    @Override public boolean suporta(String canal) { return canal == null || "LOG".equalsIgnoreCase(canal); }
    @Override public void enviar(NotificacaoOperacional n) {
        log.info("notificacao_log id={} tipo={} destinatario={} titulo={}", n.id(), n.tipo(), n.destinatario(), n.titulo());
    }
}
