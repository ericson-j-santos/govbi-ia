package br.com.reqsys.govbi.dominio.modelo;

import java.util.Map;

public record TemplateNotificacao(
        String tipo,
        String canal,
        String tituloTemplate,
        String corpoTemplate
) {
    public String renderizarTitulo(Map<String, Object> valores) { return renderizar(tituloTemplate, valores); }
    public String renderizarCorpo(Map<String, Object> valores) { return renderizar(corpoTemplate, valores); }
    private String renderizar(String texto, Map<String, Object> valores) {
        String saida = texto == null ? "" : texto;
        for (var e : valores.entrySet()) {
            saida = saida.replace("{{" + e.getKey() + "}}", String.valueOf(e.getValue()));
        }
        return saida;
    }
}
