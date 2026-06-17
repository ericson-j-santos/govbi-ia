package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MascaradorLgpdJdbcTest {
    @Test
    void deveMascararCpfEmailTelefoneENome() {
        assertEquals("***.***.***-01", MascaradorLgpdJdbc.mascararSeNecessario("cpf", "12345678901", true));
        assertEquals("e***n@empresa.com", MascaradorLgpdJdbc.mascararSeNecessario("email", "ericson@empresa.com", true));
        assertEquals("(**) *****-8888", MascaradorLgpdJdbc.mascararSeNecessario("telefone", "11999998888", true));
        assertEquals("E***", MascaradorLgpdJdbc.mascararSeNecessario("nome_cliente", "Ericson Santos", true));
    }
}
