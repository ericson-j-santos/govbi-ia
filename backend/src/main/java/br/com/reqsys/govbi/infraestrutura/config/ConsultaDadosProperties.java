package br.com.reqsys.govbi.infraestrutura.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "govbi.dados")
public class ConsultaDadosProperties {
    private String executor = "mock";
    private int queryTimeoutSegundos = 30;
    private int limiteLinhasRetorno = 1000;
    private long limiteLinhasDryRun = 50_000L;
    private double limiteCustoDryRun = 75.0d;
    private boolean permitirExecucaoReal = false;
    private boolean bloquearSemFiltroTemporal = true;
    private int fetchSize = 250;
    private FonteJdbc sqlserver = new FonteJdbc();
    private FonteJdbc databricks = new FonteJdbc();
    private FonteJdbc postgres = new FonteJdbc();

    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }

    public int getQueryTimeoutSegundos() { return queryTimeoutSegundos; }
    public void setQueryTimeoutSegundos(int queryTimeoutSegundos) { this.queryTimeoutSegundos = queryTimeoutSegundos; }

    public int getLimiteLinhasRetorno() { return limiteLinhasRetorno; }
    public void setLimiteLinhasRetorno(int limiteLinhasRetorno) { this.limiteLinhasRetorno = limiteLinhasRetorno; }

    public long getLimiteLinhasDryRun() { return limiteLinhasDryRun; }
    public void setLimiteLinhasDryRun(long limiteLinhasDryRun) { this.limiteLinhasDryRun = limiteLinhasDryRun; }

    public double getLimiteCustoDryRun() { return limiteCustoDryRun; }
    public void setLimiteCustoDryRun(double limiteCustoDryRun) { this.limiteCustoDryRun = limiteCustoDryRun; }

    public boolean isPermitirExecucaoReal() { return permitirExecucaoReal; }
    public void setPermitirExecucaoReal(boolean permitirExecucaoReal) { this.permitirExecucaoReal = permitirExecucaoReal; }

    public boolean isBloquearSemFiltroTemporal() { return bloquearSemFiltroTemporal; }
    public void setBloquearSemFiltroTemporal(boolean bloquearSemFiltroTemporal) { this.bloquearSemFiltroTemporal = bloquearSemFiltroTemporal; }

    public int getFetchSize() { return fetchSize; }
    public void setFetchSize(int fetchSize) { this.fetchSize = fetchSize; }

    public FonteJdbc getSqlserver() { return sqlserver; }
    public void setSqlserver(FonteJdbc sqlserver) { this.sqlserver = sqlserver; }

    public FonteJdbc getDatabricks() { return databricks; }
    public void setDatabricks(FonteJdbc databricks) { this.databricks = databricks; }

    public FonteJdbc getPostgres() { return postgres; }
    public void setPostgres(FonteJdbc postgres) { this.postgres = postgres; }

    public static class FonteJdbc {
        private String url = "";
        private String usuario = "";
        private String senha = "";
        private String driverClassName = "";
        private String catalogo = "";
        private String schema = "gold";

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public String getDriverClassName() { return driverClassName; }
        public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }

        public String getCatalogo() { return catalogo; }
        public void setCatalogo(String catalogo) { this.catalogo = catalogo; }

        public String getSchema() { return schema; }
        public void setSchema(String schema) { this.schema = schema; }
    }
}
