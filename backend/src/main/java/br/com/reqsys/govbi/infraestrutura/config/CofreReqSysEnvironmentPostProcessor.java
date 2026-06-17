package br.com.reqsys.govbi.infraestrutura.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Busca segredos do cofre centralizado do ReqSys antes do Spring resolver @Value.
 * Ativa quando govbi.cofre.habilitado=true (ou GOVBI_COFRE_HABILITADO=true).
 *
 * As chaves buscadas são mapeadas para propriedades Spring, por exemplo:
 *   GOVBI_GROQ_API_KEY (cofre) → govbi.ia.groq.api-key (Spring property)
 */
public class CofreReqSysEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CofreReqSysEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "cofreReqSys";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final List<String[]> CHAVES_MAPEADAS = List.of(
            // { chave no cofre, propriedade Spring que será populada }
            new String[]{"GOVBI_GEMINI_API_KEY",         "govbi.ia.gemini.api-key"},
            new String[]{"GOVBI_GROQ_API_KEY",           "govbi.ia.groq.api-key"},
            new String[]{"GOVBI_OPENAI_API_KEY",         "govbi.ia.openai.api-key"},
            new String[]{"GOVBI_AZURE_OPENAI_API_KEY",   "govbi.ia.azure-openai.api-key"},
            new String[]{"GOVBI_SQLSERVER_PASSWORD",     "govbi.dados.sqlserver.senha"},
            new String[]{"GOVBI_POSTGRES_PASSWORD",      "govbi.dados.postgres.senha"},
            new String[]{"GOVBI_OPERACIONAL_SQLSERVER_PASSWORD", "produto-operacional.persistencia.operacional.sqlserver.senha"},
            new String[]{"GOVBI_TEAMS_WEBHOOK_URL",      "integracao-corporativa.notificacao.teams.webhook-url"}
    );

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String habilitado = environment.getProperty("govbi.cofre.habilitado",
                System.getenv().getOrDefault("GOVBI_COFRE_HABILITADO", "false"));
        if (!"true".equalsIgnoreCase(habilitado)) {
            return;
        }

        String url = resolve(environment, "govbi.cofre.url", "GOVBI_COFRE_URL", "");
        String token = resolve(environment, "govbi.cofre.token", "GOVBI_COFRE_TOKEN", "");

        if (url == null || url.isBlank()) {
            log.warn("[Cofre] govbi.cofre.habilitado=true mas GOVBI_COFRE_URL não configurada. Segredos do cofre ignorados.");
            return;
        }

        log.info("[Cofre] Buscando segredos no ReqSys: {}", url);
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        Map<String, Object> segredos = new HashMap<>();

        for (String[] entrada : CHAVES_MAPEADAS) {
            String chaveRemota = entrada[0];
            String propriedadeSpring = entrada[1];
            try {
                String endpoint = url.replaceAll("/+$", "") + "/api/v1/cofre/segredo/" + chaveRemota;
                HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(endpoint))
                        .timeout(Duration.ofSeconds(5))
                        .GET();
                if (token != null && !token.isBlank()) {
                    builder.header("X-Cofre-Token", token);
                }
                HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode json = MAPPER.readTree(response.body());
                    String valor = json.path("valor").asText(null);
                    if (valor != null && !valor.isBlank()) {
                        segredos.put(propriedadeSpring, valor);
                        log.info("[Cofre] {} carregado com sucesso.", chaveRemota);
                    }
                } else if (response.statusCode() != 404) {
                    log.warn("[Cofre] {} retornou HTTP {}. Chave ignorada.", chaveRemota, response.statusCode());
                }
            } catch (Exception e) {
                log.warn("[Cofre] Falha ao buscar {} no cofre: {}. Usando valor local se disponível.", chaveRemota, e.getMessage());
            }
        }

        if (!segredos.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, segredos));
            log.info("[Cofre] {} segredo(s) carregado(s) do ReqSys.", segredos.size());
        }
    }

    private String resolve(ConfigurableEnvironment env, String springKey, String envVar, String fallback) {
        String v = env.getProperty(springKey);
        if (v == null || v.isBlank()) {
            String fromEnv = System.getenv(envVar);
            v = (fromEnv != null) ? fromEnv : "";
        }
        return v.isBlank() ? fallback : v;
    }
}
