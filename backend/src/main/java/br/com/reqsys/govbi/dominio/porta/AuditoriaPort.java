package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface AuditoriaPort {
    void registrar(String correlationId, UsuarioContexto usuarioContexto, PerguntaAnalitica pergunta, ConsultaGerada consulta, ResultadoConsulta resultado);
}
