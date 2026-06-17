package br.com.reqsys.govbi.infraestrutura.adapter.historico;

import br.com.reqsys.govbi.dominio.modelo.RegistroHistoricoConversa;
import br.com.reqsys.govbi.dominio.porta.HistoricoConversacionalPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class HistoricoConversacionalEmMemoriaAdapter implements HistoricoConversacionalPort {
    private final CopyOnWriteArrayList<RegistroHistoricoConversa> registros = new CopyOnWriteArrayList<>();

    @Override
    public RegistroHistoricoConversa registrar(RegistroHistoricoConversa registro) {
        registros.add(registro);
        return registro;
    }

    @Override
    public List<RegistroHistoricoConversa> listarPorUsuarioHash(String usuarioHash, int limite) {
        return registros.stream()
                .filter(r -> r.usuarioHash().equals(usuarioHash))
                .sorted(Comparator.comparing(RegistroHistoricoConversa::registradoEm).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }

    @Override
    public List<RegistroHistoricoConversa> listarRecentes(int limite) {
        return registros.stream()
                .sorted(Comparator.comparing(RegistroHistoricoConversa::registradoEm).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }
}
