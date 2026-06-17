package br.com.reqsys.govbi.infraestrutura.adapter.observabilidade;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.ObservabilidadePort;

public class ObservabilidadeNulaAdapter implements ObservabilidadePort {
    @Override
    public long iniciarMedicao() {
        return System.nanoTime();
    }

    @Override
    public void registrarSucesso(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, ConsultaGerada consultaGerada,
                                 ResultadoConsulta resultadoConsulta, String nivelSensibilidade, long inicioNanos) {
        // Sem efeito. Usado em testes unitários puros.
    }

    @Override
    public void registrarBloqueio(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, String motivo,
                                  String nivelSensibilidade, long inicioNanos) {
        // Sem efeito. Usado em testes unitários puros.
    }

    @Override
    public void registrarErro(PerguntaAnalitica pergunta, UsuarioContexto usuarioContexto, String classeErro,
                              String nivelSensibilidade, long inicioNanos) {
        // Sem efeito. Usado em testes unitários puros.
    }
}
