package br.com.reqsys.govbi.infraestrutura.adapter.release;

import br.com.reqsys.govbi.dominio.modelo.ReleaseReadinessStatus;
import br.com.reqsys.govbi.dominio.porta.ReleaseReadinessPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReleaseReadinessConfigAdapter implements ReleaseReadinessPort {
    private final String versao;
    private final String modo;
    private final boolean oidcHabilitado;
    private final String persistenciaOperacional;
    private final String executorDados;
    private final boolean execucaoReal;
    private final String lockTipo;

    public ReleaseReadinessConfigAdapter(
            @Value("${release.corporativa.versao:1.0.0}") String versao,
            @Value("${release.corporativa.modo:release-candidate}") String modo,
            @Value("${govbi.seguranca.oidc-habilitado:false}") boolean oidcHabilitado,
            @Value("${produto-operacional.persistencia.operacional.tipo:memoria}") String persistenciaOperacional,
            @Value("${govbi.dados.executor:mock}") String executorDados,
            @Value("${govbi.dados.permitir-execucao-real:false}") boolean execucaoReal,
            @Value("${integracao-corporativa.lock.tipo:memoria}") String lockTipo
    ) {
        this.versao = versao;
        this.modo = modo;
        this.oidcHabilitado = oidcHabilitado;
        this.persistenciaOperacional = persistenciaOperacional;
        this.executorDados = executorDados;
        this.execucaoReal = execucaoReal;
        this.lockTipo = lockTipo;
    }

    @Override
    public ReleaseReadinessStatus verificar() {
        List<String> pendencias = new ArrayList<>();
        boolean producao = "producao".equalsIgnoreCase(modo);
        if (producao && !oidcHabilitado) {
            pendencias.add("OIDC/JWT deve estar habilitado em produção.");
        }
        if (producao && !"sqlserver".equalsIgnoreCase(persistenciaOperacional)) {
            pendencias.add("Persistência operacional deve usar SQL Server em produção.");
        }
        if (producao && ("mock".equalsIgnoreCase(executorDados) || !execucaoReal)) {
            pendencias.add("Executor de dados real deve estar explicitamente habilitado em produção.");
        }
        if (producao && !"sqlserver".equalsIgnoreCase(lockTipo)) {
            pendencias.add("Lock distribuído deve usar SQL Server em produção com múltiplas réplicas.");
        }
        String status = pendencias.isEmpty() ? "APTO" : "PENDENTE";
        return new ReleaseReadinessStatus(
                "GovBI IA",
                versao,
                status,
                Instant.now(),
                List.of("quality_check", "catalog_version", "nl_sql_evaluation", "release_v100", "e2e_contract"),
                Map.of(
                        "modo", modo,
                        "oidcHabilitado", String.valueOf(oidcHabilitado),
                        "persistenciaOperacional", persistenciaOperacional,
                        "executorDados", executorDados,
                        "execucaoReal", String.valueOf(execucaoReal),
                        "lockTipo", lockTipo
                ),
                pendencias
        );
    }
}
