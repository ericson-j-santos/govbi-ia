package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import br.com.reqsys.govbi.dominio.modelo.PromptLlm;
import br.com.reqsys.govbi.dominio.modelo.RespostaLlm;
import br.com.reqsys.govbi.dominio.porta.ClienteLlmPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "govbi.ia.modo", havingValue = "openai")
public class ClienteLlmOpenAiAdapter implements ClienteLlmPort {
    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClienteLlmOpenAiAdapter(
            @Value("${govbi.ia.openai.base-url}") String baseUrl,
            @Value("${govbi.ia.openai.api-key}") String apiKey,
            @Value("${govbi.ia.openai.model}") String model,
            @Value("${govbi.ia.openai.timeout-segundos:30}") int timeoutSegundos
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeoutSegundos)).build();
    }

    @Override
    public RespostaLlm gerarPlano(PromptLlm prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GOVBI_OPENAI_API_KEY não configurada para modo openai.");
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("temperature", 0);
            payload.put("response_format", Map.of("type", "json_object"));
            payload.put("messages", List.of(
                    Map.of("role", "system", "content", contratoSistema()),
                    Map.of("role", "user", "content", montarPrompt(prompt))
            ));
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("LLM retornou status HTTP não aprovado: " + response.statusCode());
            }
            JsonNode body = mapper.readTree(response.body());
            String content = body.path("choices").path(0).path("message").path("content").asText();
            JsonNode plano = mapper.readTree(content);
            return new RespostaLlm(
                    plano.path("metrica").asText("qtd_propostas_cadastradas"),
                    mapper.convertValue(plano.path("dimensoes"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    mapper.convertValue(plano.path("filtros"), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)),
                    mapper.convertValue(plano.path("avisos"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    "Plano produzido por LLM real via adapter OpenAI compatível, restrito ao contrato JSON governado."
            );
        } catch (Exception e) {
            throw new IllegalStateException("Falha sanitizada ao chamar LLM real. Verifique endpoint, credenciais e contrato JSON.", e);
        }
    }

    private String contratoSistema() {
        return "Você é um planejador de BI conversacional governado. Responda somente JSON válido com: metrica:string, dimensoes:string[], filtros:object, avisos:string[]. Não gere SQL. Não invente métricas fora do catálogo.";
    }

    private String montarPrompt(PromptLlm prompt) throws Exception {
        return mapper.writeValueAsString(Map.of(
                "tarefa", prompt.tarefa(),
                "pergunta", prompt.perguntaUsuario(),
                "contexto_semantico", prompt.contextoSemantico(),
                "feedback_validacao", prompt.feedbackValidacao()
        ));
    }
}
