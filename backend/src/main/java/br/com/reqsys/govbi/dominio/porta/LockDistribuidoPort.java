package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.LockDistribuido;

import java.time.Duration;
import java.util.Optional;

public interface LockDistribuidoPort {
    Optional<LockDistribuido> tentarAdquirir(String chave, String dono, Duration ttl);
    void liberar(String chave, String dono);
}
