package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;
import java.util.Map;

public record NotificacaoOperacional(
        String id,
        String tipo,
        String canal,
        String destinatario,
        String titulo,
        String mensagem,
        Map<String, Object> metadados,
        String status,
        Instant criadaEm,
        Instant enviadaEm,
        String erro
) {
    public NotificacaoOperacional comoEnviada() {
        return new NotificacaoOperacional(id, tipo, canal, destinatario, titulo, mensagem, metadados, "ENVIADA", criadaEm, Instant.now(), null);
    }

    public NotificacaoOperacional comoFalha(String erro) {
        return new NotificacaoOperacional(id, tipo, canal, destinatario, titulo, mensagem, metadados, "FALHA", criadaEm, Instant.now(), erro);
    }
}
