package br.com.reqsys.govbi.infraestrutura.adapter.lock;

import br.com.reqsys.govbi.dominio.modelo.LockDistribuido;
import br.com.reqsys.govbi.dominio.porta.LockDistribuidoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "integracao-corporativa.lock", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class LockDistribuidoMemoriaAdapter implements LockDistribuidoPort {
    private final Map<String, LockDistribuido> locks = new ConcurrentHashMap<>();

    @Override
    public Optional<LockDistribuido> tentarAdquirir(String chave, String dono, Duration ttl) {
        var agora = Instant.now();
        var novo = new LockDistribuido(chave, dono, agora, agora.plus(ttl));
        synchronized (locks) {
            var atual = locks.get(chave);
            if (atual == null || atual.expirado(agora) || atual.dono().equals(dono)) {
                locks.put(chave, novo);
                return Optional.of(novo);
            }
            return Optional.empty();
        }
    }

    @Override
    public void liberar(String chave, String dono) {
        locks.computeIfPresent(chave, (k, v) -> v.dono().equals(dono) ? null : v);
    }
}
