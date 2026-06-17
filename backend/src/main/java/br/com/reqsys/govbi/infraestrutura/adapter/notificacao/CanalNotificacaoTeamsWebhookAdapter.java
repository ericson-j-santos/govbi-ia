package br.com.reqsys.govbi.infraestrutura.adapter.notificacao;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.CanalNotificacaoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "integracao-corporativa.notificacao.teams", name = "habilitado", havingValue = "true")
public class CanalNotificacaoTeamsWebhookAdapter implements CanalNotificacaoPort {
    private final RestClient restClient;
    private final String webhookUrl;

    public CanalNotificacaoTeamsWebhookAdapter(@Value("${integracao-corporativa.notificacao.teams.webhook-url:}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.restClient = RestClient.builder().build();
    }

    @Override public boolean suporta(String canal) { return "TEAMS".equalsIgnoreCase(canal); }

    @Override
    public void enviar(NotificacaoOperacional n) {
        if (webhookUrl == null || webhookUrl.isBlank()) throw new IllegalStateException("Webhook Teams não configurado.");
        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("text", "**" + n.titulo() + "**\\n\\n" + n.mensagem()))
                .retrieve()
                .toBodilessEntity();
    }
}
