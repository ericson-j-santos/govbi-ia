package br.com.reqsys.govbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import br.com.reqsys.govbi.infraestrutura.config.RateLimitProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(RateLimitProperties.class)
public class GovBiIaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovBiIaApplication.class, args);
    }
}
