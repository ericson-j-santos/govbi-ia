package br.com.reqsys.govbi.infraestrutura.adapter.notificacao;

import br.com.reqsys.govbi.dominio.modelo.TemplateNotificacao;
import br.com.reqsys.govbi.dominio.porta.TemplateNotificacaoPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class TemplateNotificacaoMemoriaAdapter implements TemplateNotificacaoPort {
    private final Map<String, TemplateNotificacao> templates = Map.of(
            key("RESULTADO_DISPONIVEL", "TEAMS"), new TemplateNotificacao("RESULTADO_DISPONIVEL", "TEAMS", "GovBI IA — resultado disponível", "Consulta {{correlationId}} processada. Resultado: {{resultadoId}}. Retenção limitada."),
            key("RESULTADO_DISPONIVEL", "EMAIL"), new TemplateNotificacao("RESULTADO_DISPONIVEL", "EMAIL", "Resultado GovBI IA disponível", "A consulta aprovada foi processada. Correlation ID: {{correlationId}}. Resultado: {{resultadoId}}."),
            key("FALHA_PROCESSAMENTO", "TEAMS"), new TemplateNotificacao("FALHA_PROCESSAMENTO", "TEAMS", "GovBI IA — falha de processamento", "Fila {{filaId}} falhou após tentativas controladas. DLQ: {{dlqId}}."),
            key("APROVACAO_EXPIRADA", "EMAIL"), new TemplateNotificacao("APROVACAO_EXPIRADA", "EMAIL", "Aprovação GovBI IA expirada", "A aprovação {{aprovacaoId}} expirou conforme SLA operacional.")
    );

    @Override
    public Optional<TemplateNotificacao> buscar(String tipo, String canal) {
        return Optional.ofNullable(templates.get(key(tipo, canal)));
    }

    private static String key(String tipo, String canal) { return (tipo + ":" + canal).toUpperCase(); }
}
