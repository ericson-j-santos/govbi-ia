package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;

import java.util.List;
import java.util.Optional;

public interface NotificacaoOperacionalPort {
    NotificacaoOperacional registrar(NotificacaoOperacional notificacao);
    NotificacaoOperacional marcarEnviada(String id);
    NotificacaoOperacional marcarFalha(String id, String erro);
    Optional<NotificacaoOperacional> buscar(String id);
    List<NotificacaoOperacional> listarRecentes(int limite);
}
