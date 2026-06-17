package br.com.reqsys.govbi.infraestrutura.adapter.aprovacao;

import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class AprovacaoHumanaEmMemoriaAdapter implements AprovacaoHumanaPort {
    private final Map<String, SolicitacaoAprovacao> store = new ConcurrentHashMap<>();

    @Override
    public SolicitacaoAprovacao solicitar(SolicitacaoAprovacao solicitacao) {
        store.put(solicitacao.id(), solicitacao);
        return solicitacao;
    }

    @Override
    public Optional<SolicitacaoAprovacao> buscar(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<SolicitacaoAprovacao> listarPendentes() {
        return store.values().stream()
                .filter(s -> s.status() == StatusAprovacao.PENDENTE)
                .sorted(Comparator.comparing(SolicitacaoAprovacao::criadaEm).reversed())
                .toList();
    }

    @Override
    public SolicitacaoAprovacao decidir(String id, StatusAprovacao decisao, String decisor, String justificativa) {
        var atual = buscar(id).orElseThrow(() -> new IllegalArgumentException("Solicitação de aprovação não encontrada: " + id));
        if (atual.status() != StatusAprovacao.PENDENTE) {
            throw new IllegalStateException("Solicitação de aprovação já decidida: " + id);
        }
        var decidida = atual.comDecisao(decisao, decisor, justificativa);
        store.put(id, decidida);
        return decidida;
    }
}
