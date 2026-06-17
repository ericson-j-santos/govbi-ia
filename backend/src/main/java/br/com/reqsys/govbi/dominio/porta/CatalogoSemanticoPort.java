package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;

import java.util.List;
import java.util.Optional;

public interface CatalogoSemanticoPort {
    Optional<MetricaSemantica> buscarMetrica(String nome);
    List<MetricaSemantica> listarMetricas();
    List<TrechoCatalogoSemantico> buscarContexto(String pergunta, int limite);
}
