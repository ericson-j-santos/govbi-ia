package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.PlanoConsulta;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;

import java.util.List;

public interface MotorIaPort {
    PlanoConsulta criarPlano(PerguntaAnalitica pergunta, List<TrechoCatalogoSemantico> contextoSemantico);
    ConsultaGerada gerarConsulta(PlanoConsulta plano, MetricaSemantica metrica, List<TrechoCatalogoSemantico> contextoSemantico, List<String> feedbackValidacao);
}
