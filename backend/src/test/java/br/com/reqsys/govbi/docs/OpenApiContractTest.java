package br.com.reqsys.govbi.docs;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiContractTest {
    @Test
    void contratoOpenApiEstaticoDeveExistir() throws Exception {
        Path path = Path.of("..", "docs", "openapi", "govbi-ia-v0.6.0.yaml");
        assertTrue(Files.exists(path));
        String yaml = Files.readString(path);
        assertTrue(yaml.contains("/api/v1/perguntas"));
        assertTrue(yaml.contains("/api/v1/aprovacoes/pendentes"));
        assertTrue(yaml.contains("/api/v1/exportacoes"));
        assertTrue(yaml.contains("bearerAuth"));
    }
}
