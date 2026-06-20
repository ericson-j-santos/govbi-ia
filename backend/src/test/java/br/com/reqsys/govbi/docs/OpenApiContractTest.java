package br.com.reqsys.govbi.docs;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiContractTest {
    @Test
    void contratoOpenApiEstaticoDeveExistir() throws Exception {
        Path path = Path.of("..", "docs", "openapi", "govbi-ia-v1.0.1.yaml");
        assertTrue(Files.exists(path), "OpenAPI contract file not found: " + path.toAbsolutePath());
        String yaml = Files.readString(path);
        assertTrue(yaml.contains("/api/v1/perguntas"), "Contract must expose /api/v1/perguntas");
        assertTrue(yaml.contains("/api/v1/aprovacoes/pendentes"), "Contract must expose /api/v1/aprovacoes/pendentes");
        assertTrue(yaml.contains("/api/v1/exportacoes"), "Contract must expose /api/v1/exportacoes");
        assertTrue(yaml.contains("bearerAuth"), "Contract must declare bearerAuth security scheme");
    }
}
