package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoSemanticoVersionamentoTest {
    private static final String VERSAO_CATALOGO_ESPERADA = "1.0.1";

    @Test
    void catalogoDeveDeclararContratoDeGovernanca() throws Exception {
        try (var input = getClass().getClassLoader().getResourceAsStream("catalogo-semantico.yml")) {
            assertThat(input).isNotNull();
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(yaml).contains("versao: \"" + VERSAO_CATALOGO_ESPERADA + "\"");
            assertThat(yaml).contains("contratoGovernanca:");
            assertThat(yaml).contains("compatibilidadeMinimaBackend: \"" + VERSAO_CATALOGO_ESPERADA + "\"");
            assertThat(yaml).contains("politicaAcesso:");
            assertThat(yaml).contains("camposSensiveis:");
        }
    }
}
