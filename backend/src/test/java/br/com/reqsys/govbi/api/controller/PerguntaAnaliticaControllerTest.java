package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.seguranca.UsuarioContextoFactory;
import br.com.reqsys.govbi.aplicacao.caso_uso.ResponderPerguntaAnaliticaUseCase;
import br.com.reqsys.govbi.aplicacao.dto.RespostaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.StatusFluxoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PerguntaAnaliticaController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        }
)
class PerguntaAnaliticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResponderPerguntaAnaliticaUseCase useCase;

    @MockBean
    private UsuarioContextoFactory usuarioContextoFactory;

    @Test
    void deveResponderPerguntaAnaliticaComSucesso() throws Exception {
        var resposta = respostaFake(StatusFluxoConsulta.EXECUTADA, false);
        when(usuarioContextoFactory.resolver(any(), any(), any(), any()))
                .thenReturn(new UsuarioContexto("analista", "ANALISTA", "GERAL"));
        when(useCase.executar(any(), any())).thenReturn(resposta);

        mockMvc.perform(post("/api/v1/perguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario", "analista")
                        .header("X-Perfil", "ANALISTA")
                        .header("X-Escopo-Unidade", "GERAL")
                        .content("""
                                {
                                  "pergunta": "Mostre propostas por mês em 2025",
                                  "formatoResposta": "tabela",
                                  "exibirSql": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").value("corr-001"))
                .andExpect(jsonPath("$.sqlGerado").value("SELECT * FROM propostas"))
                .andExpect(jsonPath("$.requerAprovacao").value(false));
    }

    @Test
    void deveRetornar400QuandoPerguntaEstiverEmBranco() throws Exception {
        mockMvc.perform(post("/api/v1/perguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pergunta": "",
                                  "formatoResposta": "tabela"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarRequerAprovacaoQuandoConsultaTemPii() throws Exception {
        var resposta = respostaFake(StatusFluxoConsulta.PENDENTE_APROVACAO, true);
        when(usuarioContextoFactory.resolver(any(), any(), any(), any()))
                .thenReturn(new UsuarioContexto("analista", "ANALISTA", "GERAL"));
        when(useCase.executar(any(), any())).thenReturn(resposta);

        mockMvc.perform(post("/api/v1/perguntas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario", "analista")
                        .header("X-Perfil", "ANALISTA")
                        .header("X-Escopo-Unidade", "GERAL")
                        .content("""
                                {
                                  "pergunta": "Liste CPF e nome de clientes",
                                  "formatoResposta": "tabela",
                                  "exibirSql": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requerAprovacao").value(true))
                .andExpect(jsonPath("$.aprovacaoId").value("aprov-abc"))
                .andExpect(jsonPath("$.statusFluxo").value("PENDENTE_APROVACAO"));
    }

    private RespostaAnalitica respostaFake(StatusFluxoConsulta statusFluxo, boolean requerAprovacao) {
        return new RespostaAnalitica(
                "corr-001",
                "consulta_analitica",
                "propostas",
                List.of("mes", "situacao"),
                Map.<String, Object>of(),
                "SELECT * FROM propostas",
                new ResultadoConsulta(List.of("mes"), List.of(Map.of("mes", "2025-01"))),
                List.<String>of(),
                false,
                "Consulta gerada com sucesso.",
                "BAIXO",
                List.of(),
                List.of(),
                10L,
                0.5,
                statusFluxo,
                requerAprovacao,
                requerAprovacao ? "aprov-abc" : null,
                "hist-001",
                List.of("csv")
        );
    }
}
