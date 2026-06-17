package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import java.util.Locale;
import java.util.regex.Pattern;

final class SqlGovernadoUtils {
    private static final Pattern ORDER_BY_FINAL = Pattern.compile("(?is)\\border\\s+by\\b[\\s\\S]*$");
    private static final Pattern LIMITADOR_FINAL = Pattern.compile("(?is)\\b(limit|fetch\\s+first|offset|top)\\b");

    private SqlGovernadoUtils() {
    }

    static String limparParaSubconsulta(String sql) {
        String limpo = sql == null ? "" : sql.strip();
        while (limpo.endsWith(";")) {
            limpo = limpo.substring(0, limpo.length() - 1).strip();
        }
        return ORDER_BY_FINAL.matcher(limpo).replaceFirst("").strip();
    }

    static boolean possuiFiltroTemporal(String sql) {
        String normalizado = sql == null ? "" : sql.toLowerCase(Locale.ROOT);
        return normalizado.contains("t.ano =")
                || normalizado.contains("ano =")
                || normalizado.contains("ano_mes")
                || normalizado.contains("data_cadastro")
                || normalizado.contains("dt_");
    }

    static boolean possuiLimitador(String sql) {
        return LIMITADOR_FINAL.matcher(sql == null ? "" : sql).find();
    }
}
