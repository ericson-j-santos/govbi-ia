package br.com.reqsys.govbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GovBiIaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovBiIaApplication.class, args);
    }
}
