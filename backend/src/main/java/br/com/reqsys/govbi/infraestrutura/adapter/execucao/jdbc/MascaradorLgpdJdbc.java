package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import java.util.Locale;

final class MascaradorLgpdJdbc {
    private MascaradorLgpdJdbc() {
    }

    static Object mascararSeNecessario(String coluna, Object valor, boolean mascaramentoNecessario) {
        if (valor == null || !mascaramentoNecessario) {
            return valor;
        }
        String nome = coluna == null ? "" : coluna.toLowerCase(Locale.ROOT);
        String texto = String.valueOf(valor);
        if (nome.contains("cpf")) {
            return mascararCpf(texto);
        }
        if (nome.contains("email")) {
            return mascararEmail(texto);
        }
        if (nome.contains("telefone") || nome.contains("celular")) {
            return mascararTelefone(texto);
        }
        if (nome.contains("nome_cliente") || nome.equals("nome") || nome.contains("cliente")) {
            return mascararNome(texto);
        }
        return valor;
    }

    private static String mascararCpf(String texto) {
        String digitos = texto.replaceAll("\\D", "");
        if (digitos.length() < 4) {
            return "***";
        }
        String finais = digitos.substring(Math.max(0, digitos.length() - 2));
        return "***.***.***-" + finais;
    }

    private static String mascararEmail(String texto) {
        int idx = texto.indexOf('@');
        if (idx <= 1) {
            return "***";
        }
        String usuario = texto.substring(0, idx);
        String dominio = texto.substring(idx);
        return usuario.charAt(0) + "***" + usuario.charAt(usuario.length() - 1) + dominio;
    }

    private static String mascararTelefone(String texto) {
        String digitos = texto.replaceAll("\\D", "");
        if (digitos.length() < 4) {
            return "(**) *****-****";
        }
        return "(**) *****-" + digitos.substring(digitos.length() - 4);
    }

    private static String mascararNome(String texto) {
        String limpo = texto.strip();
        if (limpo.length() <= 2) {
            return "***";
        }
        return limpo.charAt(0) + "***";
    }
}
