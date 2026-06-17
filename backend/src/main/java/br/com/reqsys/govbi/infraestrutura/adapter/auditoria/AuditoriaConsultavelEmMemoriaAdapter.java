package br.com.reqsys.govbi.infraestrutura.adapter.auditoria;

import br.com.reqsys.govbi.dominio.modelo.EventoAuditoriaConsulta;
import br.com.reqsys.govbi.dominio.porta.AuditoriaConsultavelPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class AuditoriaConsultavelEmMemoriaAdapter implements AuditoriaConsultavelPort {
    private final CopyOnWriteArrayList<EventoAuditoriaConsulta> eventos = new CopyOnWriteArrayList<>();

    @Override
    public EventoAuditoriaConsulta registrar(EventoAuditoriaConsulta evento) {
        eventos.add(evento);
        return evento;
    }

    @Override
    public List<EventoAuditoriaConsulta> listarRecentes(int limite) {
        return eventos.stream()
                .sorted(Comparator.comparing(EventoAuditoriaConsulta::registradoEm).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }

    @Override
    public List<EventoAuditoriaConsulta> buscarPorCorrelationId(String correlationId) {
        return eventos.stream()
                .filter(e -> e.correlationId().equals(correlationId))
                .sorted(Comparator.comparing(EventoAuditoriaConsulta::registradoEm).reversed())
                .toList();
    }
}
