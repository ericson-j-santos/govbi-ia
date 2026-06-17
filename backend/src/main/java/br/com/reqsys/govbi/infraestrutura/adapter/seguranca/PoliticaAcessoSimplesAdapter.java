package br.com.reqsys.govbi.infraestrutura.adapter.seguranca;

import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.PoliticaAcessoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "govbi.seguranca.modo", havingValue = "simples")
public class PoliticaAcessoSimplesAdapter implements PoliticaAcessoPort {
    @Override
    public void validarAcesso(UsuarioContexto usuarioContexto, MetricaSemantica metrica) {
        if (!usuarioContexto.possuiPerfilAnalitico()) {
            throw new SecurityException("Usuário sem perfil analítico para consultar a métrica: " + metrica.nome());
        }
    }
}
