package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqlServerOperacionalProperties {
    private final String url;
    private final String usuario;
    private final String senha;
    private final String driverClassName;
    private final String schema;
    private final int queryTimeoutSegundos;
    private final int maxRowsAdmin;

    public SqlServerOperacionalProperties(
            @Value("${govbi.persistencia.operacional.sqlserver.url:}") String url,
            @Value("${govbi.persistencia.operacional.sqlserver.usuario:}") String usuario,
            @Value("${govbi.persistencia.operacional.sqlserver.senha:}") String senha,
            @Value("${govbi.persistencia.operacional.sqlserver.driver-class-name:com.microsoft.sqlserver.jdbc.SQLServerDriver}") String driverClassName,
            @Value("${govbi.persistencia.operacional.sqlserver.schema:govbi}") String schema,
            @Value("${govbi.persistencia.operacional.sqlserver.query-timeout-segundos:15}") int queryTimeoutSegundos,
            @Value("${govbi.persistencia.operacional.sqlserver.max-rows-admin:500}") int maxRowsAdmin
    ) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
        this.driverClassName = driverClassName;
        this.schema = schema;
        this.queryTimeoutSegundos = queryTimeoutSegundos;
        this.maxRowsAdmin = maxRowsAdmin;
    }

    public String url() { return url; }
    public String usuario() { return usuario; }
    public String senha() { return senha; }
    public String driverClassName() { return driverClassName; }
    public String schema() { return schema; }
    public int queryTimeoutSegundos() { return queryTimeoutSegundos; }
    public int maxRowsAdmin() { return maxRowsAdmin; }

    public void validarConfiguracao() {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("GOVBI_OPERACIONAL_SQLSERVER_URL é obrigatório quando a persistência operacional usa SQL Server.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalStateException("GOVBI_OPERACIONAL_SQLSERVER_USER é obrigatório quando a persistência operacional usa SQL Server.");
        }
    }
}
