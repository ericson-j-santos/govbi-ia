package br.com.reqsys.govbi.dominio.modelo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public record UsuarioContexto(
        String usuario,
        String perfil,
        String escopoUnidade
) {
    public boolean possuiPerfilAnalitico() {
        return possuiAlgumPerfil("ANALISTA", "ADMIN", "BI_GOVERNADO");
    }

    public boolean possuiAlgumPerfil(String... perfisEsperados) {
        Set<String> atuais = perfisNormalizados();
        for (String esperado : perfisEsperados) {
            if (atuais.contains(normalizar(esperado))) {
                return true;
            }
        }
        return false;
    }

    public Set<String> perfisNormalizados() {
        Set<String> perfis = new LinkedHashSet<>();
        if (perfil == null || perfil.isBlank()) {
            return perfis;
        }
        Arrays.stream(perfil.split("[,; ]+"))
                .map(UsuarioContexto::normalizar)
                .filter(s -> !s.isBlank())
                .forEach(perfis::add);
        return perfis;
    }

    public boolean possuiEscopoGeral(String escopoGeral) {
        return escopoUnidade == null || escopoUnidade.isBlank() || normalizar(escopoUnidade).equals(normalizar(escopoGeral));
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }
}
