package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ExportacaoResultado;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface ExportadorResultadoPort {
    ExportacaoResultado exportar(ResultadoConsulta resultado, String formato, UsuarioContexto usuarioContexto, String correlationId);
}
