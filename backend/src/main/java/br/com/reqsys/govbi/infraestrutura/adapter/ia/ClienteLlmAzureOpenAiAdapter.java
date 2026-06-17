package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import br.com.reqsys.govbi.dominio.modelo.PromptLlm;
import br.com.reqsys.govbi.dominio.modelo.RespostaLlm;
import br.com.reqsys.govbi.dominio.porta.ClienteLlmPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "govbi.ia.modo", havingValue = "azure-openai")
public class ClienteLlmAzureOpenAiAdapter implements ClienteLlmPort {
    private final ClienteLlmOpenAiAdapter delegate;

    public ClienteLlmAzureOpenAiAdapter(
            @Value("${govbi.ia.azure-openai.endpoint}") String endpoint,
            @Value("${govbi.ia.azure-openai.deployment}") String deployment,
            @Value("${govbi.ia.azure-openai.api-key}") String apiKey,
            @Value("${govbi.ia.azure-openai.api-version}") String apiVersion,
            @Value("${govbi.ia.azure-openai.timeout-segundos:30}") int timeoutSegundos
    ) {
        String url = endpoint == null || endpoint.isBlank()
                ? ""
                : endpoint.replaceAll("/$", "") + "/openai/deployments/" + deployment + "/chat/completions?api-version=" + apiVersion;
        this.delegate = new ClienteLlmOpenAiAdapter(url, apiKey, deployment, timeoutSegundos);
    }

    @Override
    public RespostaLlm gerarPlano(PromptLlm prompt) {
        return delegate.gerarPlano(prompt);
    }
}
