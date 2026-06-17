package br.com.reqsys.govbi.aplicacao.caso_uso;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.CanalNotificacaoPort;
import br.com.reqsys.govbi.dominio.porta.NotificacaoOperacionalPort;
import br.com.reqsys.govbi.dominio.porta.TemplateNotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificarOperacaoUseCase {
    private static final Logger log = LoggerFactory.getLogger(NotificarOperacaoUseCase.class);
    private final NotificacaoOperacionalPort notificacaoOperacionalPort;
    private final TemplateNotificacaoPort templatePort;
    private final List<CanalNotificacaoPort> canais;
    private final boolean habilitada;

    public NotificarOperacaoUseCase(NotificacaoOperacionalPort notificacaoOperacionalPort,
                                    TemplateNotificacaoPort templatePort,
                                    List<CanalNotificacaoPort> canais,
                                    @Value("${govbi.notificacao.habilitada:true}") boolean habilitada) {
        this.notificacaoOperacionalPort = notificacaoOperacionalPort;
        this.templatePort = templatePort;
        this.canais = canais;
        this.habilitada = habilitada;
    }

    public NotificacaoOperacional registrar(String tipo, String canal, String destinatario, String titulo, String mensagem, Map<String, Object> metadados) {
        String canalNormalizado = canal == null || canal.isBlank() ? "LOG" : canal.toUpperCase();
        var template = templatePort.buscar(tipo, canalNormalizado);
        String tituloFinal = template.map(t -> t.renderizarTitulo(metadados)).filter(s -> !s.isBlank()).orElse(titulo);
        String mensagemFinal = template.map(t -> t.renderizarCorpo(metadados)).filter(s -> !s.isBlank()).orElse(mensagem);
        var notificacao = new NotificacaoOperacional(UUID.randomUUID().toString(), tipo, canalNormalizado, destinatario, tituloFinal, mensagemFinal, metadados, "PENDENTE", Instant.now(), null, null);
        notificacaoOperacionalPort.registrar(notificacao);
        if (!habilitada) {
            log.info("notificacao_registrada_sem_envio tipo={} canal={} id={}", tipo, canalNormalizado, notificacao.id());
            return notificacao;
        }
        try {
            var canalEntrega = canais.stream().filter(c -> c.suporta(canalNormalizado)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Canal de notificação não suportado: " + canalNormalizado));
            canalEntrega.enviar(notificacao);
            return notificacaoOperacionalPort.marcarEnviada(notificacao.id());
        } catch (RuntimeException e) {
            log.warn("notificacao_falhou id={} canal={} motivo={}", notificacao.id(), canalNormalizado, e.getMessage());
            return notificacaoOperacionalPort.marcarFalha(notificacao.id(), sanitizar(e));
        }
    }

    private String sanitizar(RuntimeException e) {
        var msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        return msg.replaceAll("(?i)(password|senha|token|secret)=\\S+", "$1=***");
    }
}
