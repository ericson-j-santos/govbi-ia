package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.DryRunConsulta;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface ExecutorConsultaPort {
    DryRunConsulta dryRun(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId);
    ResultadoConsulta executar(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId);
}
