package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.modelo.ReleaseReadinessStatus;
import br.com.reqsys.govbi.dominio.porta.ReleaseReadinessPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ReleaseReadinessController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        }
)
class ReleaseReadinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReleaseReadinessPort releaseReadinessPort;

    @Test
    void deveRetornarStatusDeReadinessComSucesso() throws Exception {
        var status = new ReleaseReadinessStatus(
                "govbi-ia",
                "1.0.1",
                "APTO",
                Instant.now(),
                List.of("oidc", "sqlserver", "hardening"),
                Map.of("modo", "homologacao"),
                List.of()
        );
        when(releaseReadinessPort.verificar()).thenReturn(status);

        mockMvc.perform(get("/api/v1/release/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produto").value("govbi-ia"))
                .andExpect(jsonPath("$.versao").value("1.0.1"))
                .andExpect(jsonPath("$.status").value("APTO"));
    }

    @Test
    void deveRetornarPendenciasQuandoNaoApto() throws Exception {
        var statusComPendencias = new ReleaseReadinessStatus(
                "govbi-ia",
                "1.0.1",
                "PENDENTE",
                Instant.now(),
                List.of("oidc", "sqlserver"),
                Map.of("modo", "homologacao"),
                List.of("OIDC não configurado", "SQL Server analítico não acessível")
        );
        when(releaseReadinessPort.verificar()).thenReturn(statusComPendencias);

        mockMvc.perform(get("/api/v1/release/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.pendenciasProducao").isArray())
                .andExpect(jsonPath("$.pendenciasProducao[0]").value("OIDC não configurado"));
    }
}
