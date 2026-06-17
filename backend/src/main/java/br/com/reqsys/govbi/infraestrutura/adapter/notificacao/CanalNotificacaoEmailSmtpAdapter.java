package br.com.reqsys.govbi.infraestrutura.adapter.notificacao;

import br.com.reqsys.govbi.dominio.modelo.NotificacaoOperacional;
import br.com.reqsys.govbi.dominio.porta.CanalNotificacaoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "integracao-corporativa.notificacao.email", name = "habilitado", havingValue = "true")
public class CanalNotificacaoEmailSmtpAdapter implements CanalNotificacaoPort {
    private final JavaMailSender mailSender;
    private final String remetente;
    private final String prefixo;

    public CanalNotificacaoEmailSmtpAdapter(JavaMailSender mailSender,
                                            @Value("${integracao-corporativa.notificacao.email.remetente:govbi@empresa.local}") String remetente,
                                            @Value("${integracao-corporativa.notificacao.email.assunto-prefixo:[GovBI IA]}") String prefixo) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.prefixo = prefixo;
    }

    @Override public boolean suporta(String canal) { return "EMAIL".equalsIgnoreCase(canal); }

    @Override
    public void enviar(NotificacaoOperacional n) {
        var msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(n.destinatario());
        msg.setSubject(prefixo + " " + n.titulo());
        msg.setText(n.mensagem());
        mailSender.send(msg);
    }
}
