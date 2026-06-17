package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoSemanticoYamlAdapterTest {
    @Test
    void deveCarregarCatalogoYamlERecuperarMetrica() {
        var adapter = new CatalogoSemanticoYamlAdapter(new ClassPathResource("catalogo-semantico.yml"));
        var metrica = adapter.buscarMetrica("qtd_propostas_cadastradas");
        assertTrue(metrica.isPresent());
        assertTrue(metrica.get().permiteDimensao("ano_mes"));
        assertFalse(adapter.buscarContexto("propostas por mês em 2025", 5).isEmpty());
    }
}
