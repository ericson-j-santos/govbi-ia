package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.TemplateNotificacao;

import java.util.Optional;

public interface TemplateNotificacaoPort {
    Optional<TemplateNotificacao> buscar(String tipo, String canal);
}
