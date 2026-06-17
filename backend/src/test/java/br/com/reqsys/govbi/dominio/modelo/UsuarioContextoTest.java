package br.com.reqsys.govbi.dominio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioContextoTest {
    @Test
    void deveAceitarMultiplosPerfis() {
        var ctx = new UsuarioContexto("u", "VISUALIZADOR,BI_GOVERNADO", "GERAL");
        assertTrue(ctx.possuiPerfilAnalitico());
        assertTrue(ctx.possuiAlgumPerfil("BI_GOVERNADO"));
    }
}
