package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import br.com.reqsys.govbi.dominio.modelo.LockDistribuido;
import br.com.reqsys.govbi.dominio.porta.LockDistribuidoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "integracao-corporativa.lock", name = "tipo", havingValue = "sqlserver")
public class LockDistribuidoSqlServerAdapter extends SqlServerOperacionalJdbc implements LockDistribuidoPort {
    public LockDistribuidoSqlServerAdapter(SqlServerOperacionalProperties properties) { super(properties); }

    @Override
    public Optional<LockDistribuido> tentarAdquirir(String chave, String dono, Duration ttl) {
        var agora = Instant.now();
        var expira = agora.plus(ttl);
        int updated = jdbcTemplate.update("UPDATE " + table("lock_distribuido") + " SET dono=?, adquirido_em=SYSUTCDATETIME(), expira_em=? WHERE chave=? AND (expira_em < SYSUTCDATETIME() OR dono=?)",
                dono, Timestamp.from(expira), chave, dono);
        if (updated == 0) {
            try {
                updated = jdbcTemplate.update("INSERT INTO " + table("lock_distribuido") + " (chave, dono, adquirido_em, expira_em) VALUES (?, ?, SYSUTCDATETIME(), ?)", chave, dono, Timestamp.from(expira));
            } catch (RuntimeException ignored) { updated = 0; }
        }
        return updated > 0 ? Optional.of(new LockDistribuido(chave, dono, agora, expira)) : Optional.empty();
    }

    @Override
    public void liberar(String chave, String dono) {
        jdbcTemplate.update("DELETE FROM " + table("lock_distribuido") + " WHERE chave=? AND dono=?", chave, dono);
    }
}
