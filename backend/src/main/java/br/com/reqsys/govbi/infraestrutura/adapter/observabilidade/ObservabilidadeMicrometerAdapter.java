package br.com.reqsys.govbi.infraestrutura.adapter.observabilidade;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.ObservabilidadePort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class ObservabilidadeMicrometerAdapter implements ObservabilidadePort {
    private final MeterRegistry meterRegistry;
    private final String executorDados;

    public ObservabilidadeMicrometerAdapter(
            MeterRegistry meterRegistry,
            @Value("${govbi.dados.executor:mock}") String executorDados
    ) {
        this.meterRegistry = meterRegistry;
        this.executorDados = normalizarTag(executorDados);
    }

    @Override
    public long iniciarMedicao() {
        return System.nanoTime();
    }

    @Override
    public void registrarSucesso(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, ConsultaGerada consultaGerada,
                                 ResultadoConsulta resultadoConsulta, String nivelSensibilidade, long inicioNanos) {
        String perfil = perfil(usuarioContexto);
        String sensibilidade = normalizarTag(nivelSensibilidade);
        registrarContador("sucesso", perfil, sensibilidade, "nenhum");
        registrarTimer("sucesso", perfil, sensibilidade, inicioNanos);
        DistributionSummary.builder("govbi_resultado_linhas")
                .description("Quantidade de linhas retornadas por resposta analítica governada")
                .baseUnit("linhas")
                .tag("executor", executorDados)
                .tag("perfil", perfil)
                .tag("sensibilidade", sensibilidade)
                .register(meterRegistry)
                .record(resultadoConsulta == null || resultadoConsulta.linhas() == null ? 0 : resultadoConsulta.linhas().size());
    }

    @Override
    public void registrarBloqueio(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, String motivo,
                                  String nivelSensibilidade, long inicioNanos) {
        String perfil = perfil(usuarioContexto);
        String sensibilidade = normalizarTag(nivelSensibilidade);
        registrarContador("bloqueio", perfil, sensibilidade, normalizarMotivo(motivo));
        registrarTimer("bloqueio", perfil, sensibilidade, inicioNanos);
    }

    @Override
    public void registrarErro(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, String classeErro,
                              String nivelSensibilidade, long inicioNanos) {
        String perfil = perfil(usuarioContexto);
        String sensibilidade = normalizarTag(nivelSensibilidade);
        registrarContador("erro", perfil, sensibilidade, normalizarMotivo(classeErro));
        registrarTimer("erro", perfil, sensibilidade, inicioNanos);
    }

    private void registrarContador(String status, String perfil, String sensibilidade, String motivo) {
        Counter.builder("govbi_perguntas_total")
                .description("Total de perguntas analíticas processadas pelo GovBI IA")
                .tag("status", status)
                .tag("executor", executorDados)
                .tag("perfil", perfil)
                .tag("sensibilidade", sensibilidade)
                .tag("motivo", motivo)
                .register(meterRegistry)
                .increment();
    }

    private void registrarTimer(String status, String perfil, String sensibilidade, long inicioNanos) {
        Timer.builder("govbi_consulta_duracao")
                .description("Duração do ciclo pergunta → consulta → resposta")
                .minimumExpectedValue(Duration.ofMillis(10))
                .maximumExpectedValue(Duration.ofSeconds(60))
                .tag("status", status)
                .tag("executor", executorDados)
                .tag("perfil", perfil)
                .tag("sensibilidade", sensibilidade)
                .register(meterRegistry)
                .record(System.nanoTime() - inicioNanos, TimeUnit.NANOSECONDS);
    }

    private String perfil(UsuarioContexto usuarioContexto) {
        if (usuarioContexto == null || usuarioContexto.perfil() == null || usuarioContexto.perfil().isBlank()) {
            return "nao_informado";
        }
        if (usuarioContexto.perfil().contains(",") || usuarioContexto.perfil().contains(";") || usuarioContexto.perfil().contains(" ")) {
            return "multiplo";
        }
        return normalizarTag(usuarioContexto.perfil());
    }

    private String normalizarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            return "nao_informado";
        }
        String valor = motivo.toLowerCase(Locale.ROOT);
        if (valor.contains("dados pessoais") || valor.contains("pii")) {
            return "pii";
        }
        if (valor.contains("acesso") || valor.contains("perfil") || valor.contains("rbac")) {
            return "rbac";
        }
        if (valor.contains("linha") || valor.contains("rls")) {
            return "rls";
        }
        if (valor.contains("sql") || valor.contains("select") || valor.contains("ddl") || valor.contains("dml")) {
            return "sql_inseguro";
        }
        return "outro";
    }

    private String normalizarTag(String valor) {
        if (valor == null || valor.isBlank()) {
            return "nao_informado";
        }
        return valor.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "_");
    }
}
