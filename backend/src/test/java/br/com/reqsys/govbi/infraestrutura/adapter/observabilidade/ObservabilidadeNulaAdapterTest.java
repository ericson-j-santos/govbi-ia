package br.com.reqsys.govbi.infraestrutura.adapter.observabilidade;

import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilidadeNulaAdapterTest {
    @Test
    void devePermitirUsoEmTesteUnitarioSemMeterRegistry() {
        var adapter = new ObservabilidadeNulaAdapter();
        long inicio = adapter.iniciarMedicao();
        adapter.registrarBloqueio(
                new PerguntaAnalitica("Liste CPF", "tabela", true, Instant.now()),
                new UsuarioContexto("teste", "ANALISTA", "GERAL"),
                "dados pessoais",
                "alta",
                inicio
        );
        assertThat(inicio).isGreaterThan(0);
    }
}
