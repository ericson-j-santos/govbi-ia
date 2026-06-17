package br.com.reqsys.govbi.infraestrutura.adapter.seguranca;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.infraestrutura.adapter.catalogo.CatalogoSemanticoEmMemoriaAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorSqlSeguroAdapterTest {
    private final ValidadorSqlSeguroAdapter validador = new ValidadorSqlSeguroAdapter();
    private final CatalogoSemanticoEmMemoriaAdapter catalogo = new CatalogoSemanticoEmMemoriaAdapter();

    @Test
    void devePermitirSelectNaCamadaGoldComObjetoNaAllowlist() {
        var metrica = catalogo.buscarMetrica("qtd_propostas_cadastradas").orElseThrow();
        var consulta = new ConsultaGerada("SELECT t.ano_mes, COUNT(*) AS valor FROM gold.fato_proposta p JOIN gold.dim_tempo t ON t.id_tempo = p.id_tempo_cadastro WHERE p.ic_excluido = 0 GROUP BY t.ano_mes FETCH FIRST 500 ROWS ONLY", true, "teste");

        var resultado = validador.validar(consulta, metrica);

        assertThat(resultado.valida()).isTrue();
        assertThat(resultado.erros()).isEmpty();
    }

    @Test
    void deveBloquearDelete() {
        var metrica = catalogo.buscarMetrica("qtd_propostas_cadastradas").orElseThrow();
        var consulta = new ConsultaGerada("DELETE FROM gold.fato_proposta", false, "teste");

        var resultado = validador.validar(consulta, metrica);

        assertThat(resultado.valida()).isFalse();
        assertThat(resultado.erros()).isNotEmpty();
    }

    @Test
    void deveBloquearTabelaForaDaGold() {
        var metrica = catalogo.buscarMetrica("qtd_propostas_cadastradas").orElseThrow();
        var consulta = new ConsultaGerada("SELECT coluna FROM dbo.cliente FETCH FIRST 10 ROWS ONLY", false, "teste");

        var resultado = validador.validar(consulta, metrica);

        assertThat(resultado.valida()).isFalse();
    }

    @Test
    void deveBloquearSelectEstrela() {
        var metrica = catalogo.buscarMetrica("qtd_propostas_cadastradas").orElseThrow();
        var consulta = new ConsultaGerada("SELECT * FROM gold.fato_proposta p FETCH FIRST 10 ROWS ONLY", false, "teste");

        var resultado = validador.validar(consulta, metrica);

        assertThat(resultado.valida()).isFalse();
        assertThat(resultado.erros()).anyMatch(e -> e.contains("SELECT *"));
    }

    @Test
    void deveBloquearMultiplasInstrucoes() {
        var metrica = catalogo.buscarMetrica("qtd_propostas_cadastradas").orElseThrow();
        var consulta = new ConsultaGerada("SELECT t.ano_mes FROM gold.fato_proposta p JOIN gold.dim_tempo t ON t.id_tempo = p.id_tempo_cadastro; DROP TABLE gold.fato_proposta", false, "teste");

        var resultado = validador.validar(consulta, metrica);

        assertThat(resultado.valida()).isFalse();
    }
}
