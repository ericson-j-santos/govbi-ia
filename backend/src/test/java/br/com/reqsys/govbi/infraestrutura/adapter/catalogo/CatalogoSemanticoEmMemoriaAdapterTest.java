package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoSemanticoEmMemoriaAdapterTest {
    @Test
    void deveRecuperarContextoSemanticoRelevanteParaPergunta() {
        var catalogo = new CatalogoSemanticoEmMemoriaAdapter();

        var contexto = catalogo.buscarContexto("evolução mensal de propostas por unidade", 5);

        assertThat(contexto).isNotEmpty();
        assertThat(contexto).anyMatch(t -> t.nome().equals("ano_mes"));
        assertThat(contexto).anyMatch(t -> t.nome().equals("unidade"));
    }
}
