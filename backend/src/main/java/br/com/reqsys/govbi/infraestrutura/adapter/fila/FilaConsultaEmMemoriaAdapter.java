package br.com.reqsys.govbi.infraestrutura.adapter.fila;

import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusItemFila;
import br.com.reqsys.govbi.dominio.porta.FilaConsultaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class FilaConsultaEmMemoriaAdapter implements FilaConsultaPort {
    private final Map<String, ItemFilaConsulta> store = new ConcurrentHashMap<>();

    @Override
    public ItemFilaConsulta enfileirarReprocessamento(SolicitacaoAprovacao aprovacao, String solicitadoPor) {
        var item = new ItemFilaConsulta(UUID.randomUUID().toString(), "REPROCESSAMENTO_APROVACAO", aprovacao.correlationId(), aprovacao.id(),
                aprovacao.usuarioSolicitante(), solicitadoPor, aprovacao.metrica(), Map.of("filtros", aprovacao.filtros(), "motivos", aprovacao.motivos()),
                StatusItemFila.PENDENTE, 0, Instant.now(), Instant.now(), "Aguardando execução assíncrona controlada.");
        store.put(item.id(), item);
        return item;
    }

    @Override public Optional<ItemFilaConsulta> buscar(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<ItemFilaConsulta> listarPendentes(int limite) { return filtrar(StatusItemFila.PENDENTE, limite); }
    @Override public List<ItemFilaConsulta> listarRecentes(int limite) { return store.values().stream().sorted(Comparator.comparing(ItemFilaConsulta::criadoEm).reversed()).limit(Math.max(1, limite)).toList(); }
    @Override public ItemFilaConsulta marcarEmProcessamento(String id) { return trocar(id, StatusItemFila.EM_PROCESSAMENTO, "Item em processamento."); }
    @Override public ItemFilaConsulta concluir(String id, String mensagem) { return trocar(id, StatusItemFila.CONCLUIDA, mensagem); }
    @Override public ItemFilaConsulta falhar(String id, String mensagem) { return trocar(id, StatusItemFila.FALHA, mensagem); }

    private List<ItemFilaConsulta> filtrar(StatusItemFila status, int limite) {
        return store.values().stream().filter(i -> i.status() == status).sorted(Comparator.comparing(ItemFilaConsulta::criadoEm)).limit(Math.max(1, limite)).toList();
    }
    private ItemFilaConsulta trocar(String id, StatusItemFila status, String mensagem) {
        var atual = buscar(id).orElseThrow(() -> new IllegalArgumentException("Item de fila não encontrado: " + id));
        var novo = atual.comStatus(status, atual.tentativas() + (status == StatusItemFila.EM_PROCESSAMENTO ? 1 : 0), mensagem);
        store.put(id, novo);
        return novo;
    }
}
