package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.ValidacaoConsulta;

public interface ValidadorConsultaPort {
    ValidacaoConsulta validar(ConsultaGerada consulta, MetricaSemantica metrica);
}
