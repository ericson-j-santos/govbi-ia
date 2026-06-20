package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoSemanticoVersionamentoTest {
    @Test
    void catalogoDeveDeclararContratoDeGovernanca() throws Exception {
        try (var input = getClass().getClassLoader().getResourceAsStream("catalogo-semantico.yml")) {
            assertThat(input).isNotNull();
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(yaml).contains("versao: \"1.0.1\"");
            assertThat(yaml).contains("contratoGovernanca:");
            assertThat(yaml).contains("compatibilidadeMinimaBackend: \"1.0.1\"");
            assertThat(yaml).contains("politicaAcesso:");
            assertThat(yaml).contains("camposSensiveis:");
        }
    }
}
