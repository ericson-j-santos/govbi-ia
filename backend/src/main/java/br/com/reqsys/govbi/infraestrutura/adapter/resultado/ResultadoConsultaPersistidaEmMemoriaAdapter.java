package br.com.reqsys.govbi.infraestrutura.adapter.resultado;

import br.com.reqsys.govbi.dominio.modelo.ResultadoAnaliticoPersistido;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
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
public class ResultadoConsultaPersistidaEmMemoriaAdapter implements ResultadoConsultaPersistidaPort {
    private final Map<String, ResultadoAnaliticoPersistido> store = new ConcurrentHashMap<>();

    @Override
    public ResultadoAnaliticoPersistido salvar(String filaId, String aprovacaoId, String correlationId, String metrica, ResultadoConsulta resultado, Instant expiraEm, String mensagem) {
        var item = new ResultadoAnaliticoPersistido(UUID.randomUUID().toString(), filaId, aprovacaoId, correlationId, metrica,
                resultado.colunas(), resultado.linhas(), resultado.linhas().size(), Instant.now(), expiraEm, "ATIVO", mensagem);
        store.put(item.id(), item);
        return item;
    }

    @Override public Optional<ResultadoAnaliticoPersistido> buscar(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<ResultadoAnaliticoPersistido> listarRecentes(int limite) { return store.values().stream().sorted(Comparator.comparing(ResultadoAnaliticoPersistido::criadoEm).reversed()).limit(Math.max(1, limite)).toList(); }
    @Override public List<ResultadoAnaliticoPersistido> listarPorAprovacao(String aprovacaoId, int limite) { return store.values().stream().filter(r -> aprovacaoId.equals(r.aprovacaoId())).sorted(Comparator.comparing(ResultadoAnaliticoPersistido::criadoEm).reversed()).limit(Math.max(1, limite)).toList(); }

    @Override
    public int expirarResultadosVencidos(Instant agora) {
        int[] total = {0};
        store.replaceAll((id, atual) -> {
            if ("ATIVO".equals(atual.statusRetencao()) && atual.expirado(agora)) {
                total[0]++;
                return new ResultadoAnaliticoPersistido(atual.id(), atual.filaId(), atual.aprovacaoId(), atual.correlationId(), atual.metrica(), atual.colunas(), List.of(), 0, atual.criadoEm(), atual.expiraEm(), "EXPIRADO", "Resultado removido por política de retenção.");
            }
            return atual;
        });
        return total[0];
    }
}
