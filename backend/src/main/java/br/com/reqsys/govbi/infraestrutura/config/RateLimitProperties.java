package br.com.reqsys.govbi.infraestrutura.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "govbi.seguranca.rate-limit")
public class RateLimitProperties {

    private boolean habilitado = true;
    private int requisicoesPorMinuto = 60;

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public int getRequisicoesPorMinuto() {
        return requisicoesPorMinuto;
    }

    public void setRequisicoesPorMinuto(int requisicoesPorMinuto) {
        this.requisicoesPorMinuto = requisicoesPorMinuto;
    }
}
