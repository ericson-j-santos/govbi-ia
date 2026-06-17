package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.DeadLetterConsulta;
import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;

import java.util.List;
import java.util.Optional;

public interface DeadLetterConsultaPort {
    DeadLetterConsulta registrar(ItemFilaConsulta item, String motivoFalha, String stackSanitizado);
    Optional<DeadLetterConsulta> buscar(String id);
    List<DeadLetterConsulta> listarRecentes(int limite);
    DeadLetterConsulta atualizarStatus(String id, String status);
}
