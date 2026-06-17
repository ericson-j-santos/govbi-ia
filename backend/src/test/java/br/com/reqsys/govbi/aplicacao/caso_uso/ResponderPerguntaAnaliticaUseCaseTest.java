package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.infraestrutura.adapter.auditoria.AuditoriaLogAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.catalogo.CatalogoSemanticoEmMemoriaAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.execucao.ExecutorConsultaMockAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.ia.ClienteLlmMockAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.ia.MotorIaLlmRagAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.seguranca.PoliticaAcessoSimplesAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.seguranca.ValidadorSqlSeguroAdapter;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderPerguntaAnaliticaUseCaseTest {
    @Test
    void deveResponderPerguntaAnaliticaComRagDryRunSqlResultadoEAuditoria() {
        var useCase = criarUseCase();

        var resposta = useCase.executar(
                new PerguntaAnalitica("Mostre propostas por mês em 2025 por situação e unidade", "tabela", true, Instant.now()),
                new UsuarioContexto("analista", "ANALISTA", "GERAL")
        );

        assertThat(resposta.correlationId()).isNotBlank();
        assertThat(resposta.sqlGerado()).contains("SELECT");
        assertThat(resposta.contextoSemantico()).isNotEmpty();
        assertThat(resposta.tentativas()).anyMatch(t -> t.aprovada());
        assertThat(resposta.linhasEstimadas()).isGreaterThan(0);
        assertThat(resposta.custoEstimado()).isGreaterThan(0);
        assertThat(resposta.resultado().linhas()).hasSize(3);
        assertThat(resposta.mascaramentoAplicado()).isTrue();
        assertThat(resposta.statusFluxo().name()).isEqualTo("EXECUTADA");
        assertThat(resposta.exportacoesPermitidas()).contains("csv");
    }

    @Test
    void deveEncaminharPerguntaComListagemIndividualDePiiParaAprovacao() {
        var useCase = criarUseCase();

        var resposta = useCase.executar(
                new PerguntaAnalitica("Liste CPF e nome dos clientes por proposta", "tabela", true, Instant.now()),
                new UsuarioContexto("analista", "ANALISTA", "GERAL")
        );

        assertThat(resposta.requerAprovacao()).isTrue();
        assertThat(resposta.aprovacaoId()).isNotBlank();
        assertThat(resposta.statusFluxo().name()).isEqualTo("PENDENTE_APROVACAO");
    }

    private ResponderPerguntaAnaliticaUseCase criarUseCase() {
        return new ResponderPerguntaAnaliticaUseCase(
                new MotorIaLlmRagAdapter(new ClienteLlmMockAdapter()),
                new CatalogoSemanticoEmMemoriaAdapter(),
                new ValidadorSqlSeguroAdapter(),
                new ExecutorConsultaMockAdapter(),
                new PoliticaAcessoSimplesAdapter(),
                new AuditoriaLogAdapter()
        );
    }
}
