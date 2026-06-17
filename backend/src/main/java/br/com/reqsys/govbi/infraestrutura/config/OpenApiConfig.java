package br.com.reqsys.govbi.infraestrutura.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI govBiOpenApi() {
        return new OpenAPI().info(new Info()
                .title("GovBI IA — API de BI Conversacional Governado")
                .version("0.5.0")
                .description("API para transformar perguntas em linguagem natural em consultas analíticas governadas, auditáveis e seguras.")
                .license(new License().name("Uso interno corporativo")));
    }
}
