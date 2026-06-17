package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;

public interface CanalNotificacaoPort {
    boolean suporta(String canal);
    void enviar(NotificacaoOperacional notificacao);
}
