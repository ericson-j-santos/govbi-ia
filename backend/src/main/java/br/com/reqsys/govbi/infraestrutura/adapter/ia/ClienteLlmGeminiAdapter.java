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
@ConditionalOnProperty(name = "govbi.ia.modo", havingValue = "gemini")
public class ClienteLlmGeminiAdapter implements ClienteLlmPort {

    private static final String GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClienteLlmGeminiAdapter(
            @Value("${govbi.ia.gemini.api-key:}") String apiKey,
            @Value("${govbi.ia.gemini.model:gemini-2.0-flash}") String model,
            @Value("${govbi.ia.gemini.timeout-segundos:30}") int timeoutSegundos
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSegundos))
                .build();
    }

    @Override
    public RespostaLlm gerarPlano(PromptLlm prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("govbi.ia.gemini.api-key não configurada. Obtenha uma chave gratuita em https://aistudio.google.com");
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("temperature", 0);
            payload.put("messages", List.of(
                    Map.of("role", "user", "content",
                            contratoSistema() + "\n\n" + montarPrompt(prompt))
            ));

            HttpRequest request = HttpRequest.newBuilder(URI.create(GEMINI_BASE_URL))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Gemini retornou HTTP " + response.statusCode() + ": " + response.body());
            }

            JsonNode body = mapper.readTree(response.body());
            String content = body.path("choices").path(0).path("message").path("content").asText();
            // Gemini às vezes envolve o JSON em ```json ... ```
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*$", "").trim();
            JsonNode plano = mapper.readTree(content);

            return new RespostaLlm(
                    plano.path("metrica").asText(""),
                    mapper.convertValue(plano.path("dimensoes"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    mapper.convertValue(plano.path("filtros"), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)),
                    mapper.convertValue(plano.path("avisos"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    "Plano produzido por Gemini (" + model + ") via endpoint OpenAI-compatível."
            );
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao chamar Gemini LLM. Verifique govbi.ia.gemini.api-key e conectividade.", e);
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
