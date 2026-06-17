package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;

import java.util.List;
import java.util.Optional;

public interface AprovacaoHumanaPort {
    SolicitacaoAprovacao solicitar(SolicitacaoAprovacao solicitacao);
    Optional<SolicitacaoAprovacao> buscar(String id);
    List<SolicitacaoAprovacao> listarPendentes();
    SolicitacaoAprovacao decidir(String id, StatusAprovacao decisao, String decisor, String justificativa);
}
