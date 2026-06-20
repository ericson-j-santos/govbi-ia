package br.com.reqsys.govbi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GovBiIaApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthDeveResponderOk() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void fluxoPerguntaAnaliticaDeveResponderComMetrica() throws Exception {
        mockMvc.perform(post("/api/v1/perguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario", "integracao")
                        .header("X-Perfil", "ANALISTA")
                        .header("X-Escopo-Unidade", "GERAL")
                        .content("""
                                {"pergunta":"Mostre propostas cadastradas por mês em 2025 por situação","formatoResposta":"tabela","exibirSql":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").isNotEmpty())
                .andExpect(jsonPath("$.metrica").isNotEmpty());
    }

    @Test
    void releaseReadinessDeveEstarDisponivel() throws Exception {
        mockMvc.perform(get("/api/v1/release/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versao").exists());
    }
}
