package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.aplicacao.dto.RespostaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.DryRunConsulta;
import br.com.reqsys.govbi.dominio.modelo.EventoAuditoriaConsulta;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.PlanoConsulta;
import br.com.reqsys.govbi.dominio.modelo.RegistroHistoricoConversa;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.SolicitacaoAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.modelo.StatusFluxoConsulta;
import br.com.reqsys.govbi.dominio.modelo.TentativaGeracaoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.modelo.ValidacaoConsulta;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import br.com.reqsys.govbi.dominio.porta.AuditoriaConsultavelPort;
import br.com.reqsys.govbi.dominio.porta.AuditoriaPort;
import br.com.reqsys.govbi.dominio.porta.CatalogoSemanticoPort;
import br.com.reqsys.govbi.dominio.porta.ExecutorConsultaPort;
import br.com.reqsys.govbi.dominio.porta.HistoricoConversacionalPort;
import br.com.reqsys.govbi.dominio.porta.MotorIaPort;
import br.com.reqsys.govbi.dominio.porta.ObservabilidadePort;
import br.com.reqsys.govbi.dominio.porta.PoliticaAcessoPort;
import br.com.reqsys.govbi.dominio.porta.ValidadorConsultaPort;
import br.com.reqsys.govbi.infraestrutura.adapter.auditoria.AuditoriaConsultavelEmMemoriaAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.aprovacao.AprovacaoHumanaEmMemoriaAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.historico.HistoricoConversacionalEmMemoriaAdapter;
import br.com.reqsys.govbi.infraestrutura.adapter.observabilidade.ObservabilidadeNulaAdapter;
import br.com.reqsys.govbi.infraestrutura.util.HashSeguro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResponderPerguntaAnaliticaUseCase {
    private final MotorIaPort motorIaPort;
    private final CatalogoSemanticoPort catalogoSemanticoPort;
    private final ValidadorConsultaPort validadorConsultaPort;
    private final ExecutorConsultaPort executorConsultaPort;
    private final PoliticaAcessoPort politicaAcessoPort;
    private final AuditoriaPort auditoriaPort;
    private final ObservabilidadePort observabilidadePort;
    private final AprovacaoHumanaPort aprovacaoHumanaPort;
    private final HistoricoConversacionalPort historicoConversacionalPort;
    private final AuditoriaConsultavelPort auditoriaConsultavelPort;
    private final int maxTentativasGeracao;
    private final int aprovacaoSlaHoras;

    @Autowired
    public ResponderPerguntaAnaliticaUseCase(
            MotorIaPort motorIaPort,
            CatalogoSemanticoPort catalogoSemanticoPort,
            ValidadorConsultaPort validadorConsultaPort,
            ExecutorConsultaPort executorConsultaPort,
            PoliticaAcessoPort politicaAcessoPort,
            AuditoriaPort auditoriaPort,
            ObservabilidadePort observabilidadePort,
            AprovacaoHumanaPort aprovacaoHumanaPort,
            HistoricoConversacionalPort historicoConversacionalPort,
            AuditoriaConsultavelPort auditoriaConsultavelPort,
            @Value("${govbi.ia.max-tentativas-geracao:3}") int maxTentativasGeracao,
            @Value("${produto-operacional.aprovacao.sla-horas:24}") int aprovacaoSlaHoras
    ) {
        this.motorIaPort = motorIaPort;
        this.catalogoSemanticoPort = catalogoSemanticoPort;
        this.validadorConsultaPort = validadorConsultaPort;
        this.executorConsultaPort = executorConsultaPort;
        this.politicaAcessoPort = politicaAcessoPort;
        this.auditoriaPort = auditoriaPort;
        this.observabilidadePort = observabilidadePort == null ? new ObservabilidadeNulaAdapter() : observabilidadePort;
        this.aprovacaoHumanaPort = aprovacaoHumanaPort == null ? new AprovacaoHumanaEmMemoriaAdapter() : aprovacaoHumanaPort;
        this.historicoConversacionalPort = historicoConversacionalPort == null ? new HistoricoConversacionalEmMemoriaAdapter() : historicoConversacionalPort;
        this.auditoriaConsultavelPort = auditoriaConsultavelPort == null ? new AuditoriaConsultavelEmMemoriaAdapter() : auditoriaConsultavelPort;
        this.maxTentativasGeracao = Math.max(1, maxTentativasGeracao);
        this.aprovacaoSlaHoras = Math.max(1, aprovacaoSlaHoras);
    }

    public ResponderPerguntaAnaliticaUseCase(
            MotorIaPort motorIaPort,
            CatalogoSemanticoPort catalogoSemanticoPort,
            ValidadorConsultaPort validadorConsultaPort,
            ExecutorConsultaPort executorConsultaPort,
            PoliticaAcessoPort politicaAcessoPort,
            AuditoriaPort auditoriaPort
    ) {
        this(motorIaPort, catalogoSemanticoPort, validadorConsultaPort, executorConsultaPort,
                politicaAcessoPort, auditoriaPort, new ObservabilidadeNulaAdapter(),
                new AprovacaoHumanaEmMemoriaAdapter(), new HistoricoConversacionalEmMemoriaAdapter(),
                new AuditoriaConsultavelEmMemoriaAdapter(), 3, 24);
    }

    public RespostaAnalitica executar(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto) {
        long inicioNanos = observabilidadePort.iniciarMedicao();
        String nivelSensibilidade = "nao_classificado";
        boolean bloqueioRegistrado = false;
        String correlationId = UUID.randomUUID().toString();
        String usuarioHash = HashSeguro.sha256(usuarioContexto.usuario());
        try {
            var contextoSemantico = catalogoSemanticoPort.buscarContexto(pergunta.texto(), 8);
            PlanoConsulta plano = motorIaPort.criarPlano(pergunta, contextoSemantico);
            nivelSensibilidade = plano.nivelSensibilidade();

            if (plano.requerAprovacao()) {
                var motivos = new ArrayList<String>();
                motivos.add("Consulta solicita dado pessoal, detalhe individual ou sensibilidade elevada.");
                motivos.addAll(plano.avisos());
                var aprovacao = aprovacaoHumanaPort.solicitar(new SolicitacaoAprovacao(
                        UUID.randomUUID().toString(), correlationId, usuarioContexto.usuario(), usuarioContexto.perfil(), usuarioContexto.escopoUnidade(),
                        HashSeguro.sha256(pergunta.texto()), plano.metrica(), plano.nivelSensibilidade(), List.copyOf(motivos), plano.filtros(),
                        StatusAprovacao.PENDENTE, Instant.now(), Instant.now().plus(aprovacaoSlaHoras, ChronoUnit.HOURS), null, null, null));
                var historico = registrarHistorico(correlationId, usuarioHash, plano, StatusFluxoConsulta.PENDENTE_APROVACAO, aprovacao.id(), 0);
                registrarAuditoriaConsultavel(correlationId, usuarioContexto, plano.metrica(), null, 0, List.of(), StatusFluxoConsulta.PENDENTE_APROVACAO.name());
                observabilidadePort.registrarBloqueio(pergunta, usuarioContexto, "Consulta encaminhada para aprovação humana", nivelSensibilidade, inicioNanos);
                bloqueioRegistrado = true;
                return new RespostaAnalitica(
                        correlationId, plano.intencao(), plano.metrica(), plano.dimensoes(), plano.filtros(), null,
                        new ResultadoConsulta(List.of(), List.of()), List.copyOf(motivos), true,
                        "Consulta pendente de aprovação humana antes de qualquer execução.", plano.nivelSensibilidade(), contextoSemantico,
                        List.of(), 0, 0, StatusFluxoConsulta.PENDENTE_APROVACAO, true, aprovacao.id(), historico.id(), List.of());
            }

            MetricaSemantica metrica = catalogoSemanticoPort.buscarMetrica(plano.metrica())
                    .orElseThrow(() -> new IllegalArgumentException("Métrica não encontrada no catálogo semântico: " + plano.metrica()));

            politicaAcessoPort.validarAcesso(usuarioContexto, metrica);

            List<String> feedbackValidacao = new ArrayList<>();
            List<TentativaGeracaoConsulta> tentativas = new ArrayList<>();
            ConsultaGerada consultaAprovada = null;
            ValidacaoConsulta validacaoAprovada = null;
            DryRunConsulta dryRunAprovado = null;

            for (int rodada = 1; rodada <= maxTentativasGeracao; rodada++) {
                ConsultaGerada consulta = motorIaPort.gerarConsulta(plano, metrica, contextoSemantico, feedbackValidacao);
                consulta = politicaAcessoPort.aplicarRestricoesLinha(usuarioContexto, metrica, consulta);
                ValidacaoConsulta validacao = validadorConsultaPort.validar(consulta, metrica);
                if (!validacao.valida()) {
                    feedbackValidacao = validacao.erros();
                    tentativas.add(new TentativaGeracaoConsulta(rodada, false, validacao.erros(), validacao.avisos()));
                    continue;
                }

                DryRunConsulta dryRun = executorConsultaPort.dryRun(consulta, usuarioContexto, correlationId);
                if (!dryRun.aprovado()) {
                    feedbackValidacao = dryRun.erros();
                    tentativas.add(new TentativaGeracaoConsulta(rodada, false, dryRun.erros(), dryRun.avisos()));
                    continue;
                }

                consultaAprovada = consulta;
                validacaoAprovada = validacao;
                dryRunAprovado = dryRun;
                var avisosTentativa = new ArrayList<String>();
                avisosTentativa.addAll(validacao.avisos());
                avisosTentativa.addAll(dryRun.avisos());
                tentativas.add(new TentativaGeracaoConsulta(rodada, true, List.of(), avisosTentativa));
                break;
            }

            if (consultaAprovada == null || validacaoAprovada == null || dryRunAprovado == null) {
                String motivo = "Não foi possível gerar consulta segura após " + maxTentativasGeracao + " tentativas.";
                observabilidadePort.registrarBloqueio(pergunta, usuarioContexto, motivo, nivelSensibilidade, inicioNanos);
                bloqueioRegistrado = true;
                throw new IllegalArgumentException(motivo);
            }

            ResultadoConsulta resultado = executorConsultaPort.executar(consultaAprovada, usuarioContexto, correlationId);
            auditoriaPort.registrar(correlationId, usuarioContexto, pergunta, consultaAprovada, resultado);
            registrarAuditoriaConsultavel(correlationId, usuarioContexto, plano.metrica(), consultaAprovada, resultado.linhas().size(), resultado.colunas(), StatusFluxoConsulta.EXECUTADA.name());
            observabilidadePort.registrarSucesso(pergunta, usuarioContexto, consultaAprovada, resultado, nivelSensibilidade, inicioNanos);

            var avisos = new ArrayList<String>();
            avisos.addAll(plano.avisos());
            avisos.addAll(validacaoAprovada.avisos());
            avisos.addAll(dryRunAprovado.avisos());
            var historico = registrarHistorico(correlationId, usuarioHash, plano, StatusFluxoConsulta.EXECUTADA, null, resultado.linhas().size());

            return new RespostaAnalitica(
                    correlationId,
                    plano.intencao(),
                    plano.metrica(),
                    plano.dimensoes(),
                    plano.filtros(),
                    pergunta.exibirSql() ? consultaAprovada.sql() : null,
                    resultado,
                    List.copyOf(avisos),
                    consultaAprovada.mascaramentoNecessario(),
                    consultaAprovada.explicacao(),
                    plano.nivelSensibilidade(),
                    contextoSemantico,
                    List.copyOf(tentativas),
                    dryRunAprovado.linhasEstimadas(),
                    dryRunAprovado.custoEstimado(),
                    StatusFluxoConsulta.EXECUTADA,
                    false,
                    null,
                    historico.id(),
                    List.of("csv", "json")
            );
        } catch (SecurityException | IllegalArgumentException e) {
            if (!bloqueioRegistrado) {
                observabilidadePort.registrarBloqueio(pergunta, usuarioContexto, e.getMessage(), nivelSensibilidade, inicioNanos);
            }
            throw e;
        } catch (RuntimeException e) {
            observabilidadePort.registrarErro(pergunta, usuarioContexto, e.getClass().getSimpleName(), nivelSensibilidade, inicioNanos);
            throw e;
        }
    }

    private RegistroHistoricoConversa registrarHistorico(String correlationId, String usuarioHash, PlanoConsulta plano, StatusFluxoConsulta status, String aprovacaoId, int totalLinhas) {
        return historicoConversacionalPort.registrar(new RegistroHistoricoConversa(
                UUID.randomUUID().toString(), correlationId, usuarioHash, HashSeguro.sha256(plano.intencao() + plano.metrica()),
                plano.metrica(), plano.dimensoes(), plano.filtros(), status, aprovacaoId, totalLinhas, Instant.now()));
    }

    private void registrarAuditoriaConsultavel(String correlationId, UsuarioContexto usuarioContexto, String metrica, ConsultaGerada consulta, int linhas, List<String> colunas, String status) {
        auditoriaConsultavelPort.registrar(new EventoAuditoriaConsulta(
                UUID.randomUUID().toString(), correlationId, "consulta_analitica", HashSeguro.sha256(usuarioContexto.usuario()),
                usuarioContexto.perfil(), usuarioContexto.escopoUnidade(), metrica,
                consulta == null ? null : HashSeguro.sha256(consulta.sql()), linhas, colunas, status, Instant.now()));
    }
}
