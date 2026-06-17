package br.com.reqsys.govbi.infraestrutura.adapter.dlq;

import br.com.reqsys.govbi.dominio.modelo.DeadLetterConsulta;
import br.com.reqsys.govbi.dominio.modelo.ItemFilaConsulta;
import br.com.reqsys.govbi.dominio.porta.DeadLetterConsultaPort;
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
public class DeadLetterConsultaMemoriaAdapter implements DeadLetterConsultaPort {
    private final Map<String, DeadLetterConsulta> store = new ConcurrentHashMap<>();

    @Override
    public DeadLetterConsulta registrar(ItemFilaConsulta item, String motivoFalha, String stackSanitizado) {
        var dlq = new DeadLetterConsulta(UUID.randomUUID().toString(), item.id(), item.aprovacaoId(), item.correlationId(), item.metrica(), motivoFalha,
                stackSanitizado, item.payload(), item.tentativas(), 0, "ABERTA", Instant.now(), Instant.now());
        store.put(dlq.id(), dlq);
        return dlq;
    }
    @Override public Optional<DeadLetterConsulta> buscar(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<DeadLetterConsulta> listarRecentes(int limite) { return store.values().stream().sorted(Comparator.comparing(DeadLetterConsulta::criadoEm).reversed()).limit(Math.max(1, limite)).toList(); }
    @Override public DeadLetterConsulta atualizarStatus(String id, String status) { var atual = buscar(id).orElseThrow(); var novo = atual.comoReprocessado(status); store.put(id, novo); return novo; }
}
