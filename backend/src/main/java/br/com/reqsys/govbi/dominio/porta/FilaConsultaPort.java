package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;

import java.util.List;
import java.util.Optional;

public interface FilaConsultaPort {
    ItemFilaConsulta enfileirarReprocessamento(SolicitacaoAprovacao aprovacao, String solicitadoPor);
    Optional<ItemFilaConsulta> buscar(String id);
    List<ItemFilaConsulta> listarPendentes(int limite);
    List<ItemFilaConsulta> listarRecentes(int limite);
    ItemFilaConsulta marcarEmProcessamento(String id);
    ItemFilaConsulta concluir(String id, String mensagem);
    ItemFilaConsulta falhar(String id, String mensagem);
}
