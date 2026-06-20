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

/**
 * Adapter para Groq (https://console.groq.com) — API gratuita OpenAI-compatível.
 */
@Component
@ConditionalOnProperty(name = "govbi.ia.modo", havingValue = "groq")
public class ClienteLlmGroqAdapter implements ClienteLlmPort {

    private static final String GROQ_BASE_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpLlmResilienteClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClienteLlmGroqAdapter(
            @Value("${govbi.ia.groq.api-key:}") String apiKey,
            @Value("${govbi.ia.groq.model:llama-3.3-70b-versatile}") String model,
            @Value("${govbi.ia.groq.timeout-segundos:30}") int timeoutSegundos,
            @Value("${govbi.ia.http-max-tentativas:3}") int httpMaxTentativas,
            @Value("${govbi.ia.http-backoff-inicial-ms:500}") long httpBackoffInicialMs
    ) {
        this.apiKey = apiKey;
        this.model = model;
        Duration timeout = Duration.ofSeconds(timeoutSegundos);
        HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).build();
        this.httpClient = new HttpLlmResilienteClient(client, timeout, httpMaxTentativas, httpBackoffInicialMs);
    }

    @Override
    public RespostaLlm gerarPlano(PromptLlm prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GOVBI_GROQ_API_KEY não configurada. Cadastre-se em https://console.groq.com para obter uma chave gratuita.");
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

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(GROQ_BASE_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)));

            HttpResponse<String> response = httpClient.enviarComRetry(requestBuilder);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Groq retornou status HTTP " + response.statusCode() + ". Verifique a chave e os limites do plano gratuito.");
            }

            JsonNode body = mapper.readTree(response.body());
            String content = body.path("choices").path(0).path("message").path("content").asText();
            JsonNode plano = mapper.readTree(content);

            return new RespostaLlm(
                    plano.path("metrica").asText("qtd_propostas_cadastradas"),
                    mapper.convertValue(plano.path("dimensoes"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    mapper.convertValue(plano.path("filtros"), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)),
                    mapper.convertValue(plano.path("avisos"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)),
                    "Plano produzido por Groq (" + model + ") via API OpenAI-compatível. Restrito ao contrato JSON governado."
            );
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao chamar Groq LLM. Verifique GOVBI_GROQ_API_KEY e conectividade.", e);
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
