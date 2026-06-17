package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ReleaseReadinessStatus;

public interface ReleaseReadinessPort {
    ReleaseReadinessStatus verificar();
}
