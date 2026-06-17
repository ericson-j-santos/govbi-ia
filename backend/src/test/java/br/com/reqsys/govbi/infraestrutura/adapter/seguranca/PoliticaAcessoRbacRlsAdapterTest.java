package br.com.reqsys.govbi.infraestrutura.adapter.seguranca;

import br.com.reqsys.govbi.dominio.modelo.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaAcessoRbacRlsAdapterTest {
    private final MetricaSemantica metrica = new MetricaSemantica(
            "qtd_propostas_cadastradas", "desc", "gold.fato_proposta p", "COUNT(*)", "p.ic_excluido = 0",
            List.of("ano_mes"), List.of("cpf"),
            Map.of("unidade", "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade"),
            new PoliticaMetrica(List.of("ANALISTA"), List.of("ADMIN"), new RlsMetrica("u.codigo_unidade", "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade", "GERAL"))
    );

    @Test
    void deveBloquearPerfilNaoAutorizado() {
        var policy = new PoliticaAcessoRbacRlsAdapter(true);
        assertThrows(SecurityException.class, () -> policy.validarAcesso(new UsuarioContexto("u", "VISUALIZADOR", "GERAL"), metrica));
    }

    @Test
    void deveAplicarRlsQuandoEscopoNaoForGeral() {
        var policy = new PoliticaAcessoRbacRlsAdapter(true);
        var consulta = new ConsultaGerada("SELECT t.ano_mes, COUNT(*) AS valor
FROM gold.fato_proposta p
WHERE p.ic_excluido = 0
GROUP BY t.ano_mes", true, "x");
        var comRls = policy.aplicarRestricoesLinha(new UsuarioContexto("u", "ANALISTA", "SR001"), metrica, consulta);
        assertTrue(comRls.sql().contains("JOIN gold.dim_unidade u"));
        assertTrue(comRls.sql().contains("u.codigo_unidade = 'SR001'"));
    }
}
