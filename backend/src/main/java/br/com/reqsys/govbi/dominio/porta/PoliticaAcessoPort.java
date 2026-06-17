package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface PoliticaAcessoPort {
    void validarAcesso(UsuarioContexto usuarioContexto, MetricaSemantica metrica);

    default ConsultaGerada aplicarRestricoesLinha(UsuarioContexto usuarioContexto, MetricaSemantica metrica, ConsultaGerada consulta) {
        return consulta;
    }
}
