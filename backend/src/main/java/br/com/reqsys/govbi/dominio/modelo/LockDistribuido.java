package br.com.reqsys.govbi.dominio.modelo;

import java.time.Instant;

public record LockDistribuido(
        String chave,
        String dono,
        Instant adquiridoEm,
        Instant expiraEm
) {
    public boolean expirado(Instant agora) {
        return expiraEm != null && expiraEm.isBefore(agora);
    }
}
