package br.com.reqsys.govbi.infraestrutura.adapter.notificacao;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.NotificacaoOperacionalPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class NotificacaoOperacionalEmMemoriaAdapter implements NotificacaoOperacionalPort {
    private final Map<String, NotificacaoOperacional> store = new ConcurrentHashMap<>();

    @Override public NotificacaoOperacional registrar(NotificacaoOperacional notificacao) { store.put(notificacao.id(), notificacao); return notificacao; }
    @Override public NotificacaoOperacional marcarEnviada(String id) { var n = buscar(id).orElseThrow(); var ok = n.comoEnviada(); store.put(id, ok); return ok; }
    @Override public NotificacaoOperacional marcarFalha(String id, String erro) { var n = buscar(id).orElseThrow(); var falha = n.comoFalha(erro); store.put(id, falha); return falha; }
    @Override public Optional<NotificacaoOperacional> buscar(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<NotificacaoOperacional> listarRecentes(int limite) { return store.values().stream().sorted(Comparator.comparing(NotificacaoOperacional::criadaEm).reversed()).limit(Math.max(1, limite)).toList(); }
}
