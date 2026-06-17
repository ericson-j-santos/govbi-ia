package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.EventoAuditoriaConsulta;
import java.util.List;

public interface AuditoriaConsultavelPort {
    EventoAuditoriaConsulta registrar(EventoAuditoriaConsulta evento);
    List<EventoAuditoriaConsulta> listarRecentes(int limite);
    List<EventoAuditoriaConsulta> buscarPorCorrelationId(String correlationId);
}
