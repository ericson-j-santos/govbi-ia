package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface ObservabilidadePort {
    long iniciarMedicao();

    void registrarSucesso(
            PerguntaAnalitica pergunta,
            UsuarioContexto usuarioContexto,
            ConsultaGerada consultaGerada,
            ResultadoConsulta resultadoConsulta,
            String nivelSensibilidade,
            long inicioNanos
    );

    void registrarBloqueio(
            PerguntaAnalitica pergunta,
            UsuarioContexto usuarioContexto,
            String motivo,
            String nivelSensibilidade,
            long inicioNanos
    );

    void registrarErro(
            PerguntaAnalitica pergunta,
            UsuarioContexto usuarioContexto,
            String classeErro,
            String nivelSensibilidade,
            long inicioNanos
    );
}
