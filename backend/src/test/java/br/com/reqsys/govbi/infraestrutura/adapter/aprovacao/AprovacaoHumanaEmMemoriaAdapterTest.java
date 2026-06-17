package br.com.reqsys.govbi.infraestrutura.adapter.aprovacao;

import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AprovacaoHumanaEmMemoriaAdapterTest {
    @Test
    void deveRegistrarEDecidirAprovacao() {
        var adapter = new AprovacaoHumanaEmMemoriaAdapter();
        var s = new SolicitacaoAprovacao("1", "c", "u", "ANALISTA", "GERAL", "ph", "m", "alta", List.of("pii"), Map.of(), StatusAprovacao.PENDENTE, Instant.now(), Instant.now().plusSeconds(3600), null, null, null);
        adapter.solicitar(s);
        assertThat(adapter.listarPendentes()).hasSize(1);
        var decidida = adapter.decidir("1", StatusAprovacao.APROVADA, "admin", "ok");
        assertThat(decidida.status()).isEqualTo(StatusAprovacao.APROVADA);
    }
}
