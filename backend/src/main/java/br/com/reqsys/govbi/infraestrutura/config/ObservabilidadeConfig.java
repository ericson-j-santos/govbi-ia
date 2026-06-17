package br.com.reqsys.govbi.infraestrutura.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ObservabilidadeConfig {
    @Bean
    MeterRegistryCustomizerAplicacao meterRegistryCustomizerAplicacao(
            @Value("${govbi.observabilidade.tags-comuns.sistema:govbi-ia}") String sistema,
            @Value("${govbi.observabilidade.tags-comuns.ambiente:local}") String ambiente,
            @Value("${govbi.observabilidade.tags-comuns.versao:0.5.0}") String versao
    ) {
        return new MeterRegistryCustomizerAplicacao(sistema, ambiente, versao);
    }

    @Bean
    InfoContributor govBiInfoContributor(
            @Value("${govbi.versao:0.5.0}") String versao,
            @Value("${govbi.dados.executor:mock}") String executor,
            @Value("${govbi.catalogo.tipo:yaml}") String catalogo,
            @Value("${govbi.observabilidade.tracing-habilitado:false}") boolean tracingHabilitado
    ) {
        return builder -> {
            Map<String, Object> detalhes = new LinkedHashMap<>();
            detalhes.put("versao", versao);
            detalhes.put("executorDados", executor);
            detalhes.put("catalogo", catalogo);
            detalhes.put("tracingHabilitado", tracingHabilitado);
            detalhes.put("padrao", "BI conversacional governado");
            builder.withDetail("govbi", detalhes);
        };
    }

    public record MeterRegistryCustomizerAplicacao(String sistema, String ambiente, String versao)
            implements org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer<MeterRegistry> {
        @Override
        public void customize(MeterRegistry registry) {
            registry.config().commonTags("sistema", sistema, "ambiente", ambiente, "versao", versao);
        }
    }
}
