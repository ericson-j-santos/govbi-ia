package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.aplicacao.caso_uso.ResponderPerguntaAnaliticaUseCase;
import br.com.reqsys.govbi.aplicacao.dto.RespostaAnalitica;
import br.com.reqsys.govbi.api.controller.ApiExceptionHandler;
import br.com.reqsys.govbi.api.seguranca.UsuarioContextoFactory;
import br.com.reqsys.govbi.infraestrutura.config.RateLimitFilter;
import br.com.reqsys.govbi.infraestrutura.config.RateLimitProperties;
import br.com.reqsys.govbi.infraestrutura.config.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.StatusFluxoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PerguntaAnaliticaController.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class, UsuarioContextoFactory.class, RateLimitFilter.class})
@TestPropertySource(properties = {
        "govbi.seguranca.oidc-habilitado=false",
        "govbi.seguranca.rate-limit.habilitado=false"
})
@ActiveProfiles("test")
class PerguntaAnaliticaControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResponderPerguntaAnaliticaUseCase useCase;

    @MockBean
    private UsuarioContextoFactory usuarioContextoFactory;

    @Test
    void deveAceitarPerguntaComHeadersDemo() throws Exception {
        when(usuarioContextoFactory.resolver(any(), any(), any(), any()))
                .thenReturn(new UsuarioContexto("usuario-demo", "ANALISTA", "GERAL"));
        when(useCase.executar(any(), any())).thenReturn(new RespostaAnalitica(
                "corr-1",
                "consulta_agregada",
                "qtd_propostas_cadastradas",
                List.of("ano_mes"),
                Map.of("ano", 2025),
                "SELECT COUNT(*) FROM gold.fato_proposta",
                new ResultadoConsulta(List.of("total"), List.of(Map.of("total", 10))),
                List.of(),
                false,
                "Resposta de teste",
                "BAIXA",
                List.of(),
                List.of(),
                10L,
                1.0,
                StatusFluxoConsulta.EXECUTADA,
                false,
                null,
                null,
                List.of("csv", "json")
        ));

        mockMvc.perform(post("/api/v1/perguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario", "usuario-demo")
                        .header("X-Perfil", "ANALISTA")
                        .header("X-Escopo-Unidade", "GERAL")
                        .content("""
                                {"pergunta":"Mostre propostas por mês em 2025","formatoResposta":"tabela","exibirSql":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").value("corr-1"))
                .andExpect(jsonPath("$.metrica").value("qtd_propostas_cadastradas"));
    }
}
