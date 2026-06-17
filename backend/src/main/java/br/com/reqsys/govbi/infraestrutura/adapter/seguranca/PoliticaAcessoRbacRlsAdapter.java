package br.com.reqsys.govbi.infraestrutura.adapter.seguranca;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.RlsMetrica;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.PoliticaAcessoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "govbi.seguranca.modo", havingValue = "governado", matchIfMissing = true)
public class PoliticaAcessoRbacRlsAdapter implements PoliticaAcessoPort {
    private static final Pattern ESCOPO_SEGURO = Pattern.compile("^[A-Za-z0-9_.-]{1,40}$");
    private final boolean aplicarRlsUnidade;

    public PoliticaAcessoRbacRlsAdapter(@Value("${govbi.seguranca.aplicar-rls-unidade:true}") boolean aplicarRlsUnidade) {
        this.aplicarRlsUnidade = aplicarRlsUnidade;
    }

    @Override
    public void validarAcesso(UsuarioContexto usuarioContexto, MetricaSemantica metrica) {
        Set<String> perfisUsuario = usuarioContexto.perfisNormalizados();
        boolean permitido = metrica.politicaAcesso().perfisPermitidos().stream()
                .map(p -> p.toUpperCase(Locale.ROOT))
                .anyMatch(perfisUsuario::contains);
        if (!permitido) {
            throw new SecurityException("Usuário sem perfil analítico autorizado para consultar a métrica: " + metrica.nome());
        }
    }

    @Override
    public ConsultaGerada aplicarRestricoesLinha(UsuarioContexto usuarioContexto, MetricaSemantica metrica, ConsultaGerada consulta) {
        if (!aplicarRlsUnidade) {
            return consulta;
        }
        RlsMetrica rls = metrica.politicaAcesso().rls();
        if (usuarioContexto.possuiEscopoGeral(rls.escopoGeral())) {
            return consulta;
        }
        String escopo = usuarioContexto.escopoUnidade() == null ? "" : usuarioContexto.escopoUnidade().trim();
        if (!ESCOPO_SEGURO.matcher(escopo).matches()) {
            throw new SecurityException("Escopo de unidade inválido para aplicação de RLS.");
        }
        String sql = consulta.sql();
        if (!sql.contains(rls.joinObrigatorio())) {
            sql = sql.replace("\nWHERE ", "\n" + rls.joinObrigatorio() + "\nWHERE ");
        }
        String predicado = "  AND " + rls.campoUnidade() + " = '" + escopo.replace("'", "''") + "'\n";
        if (!sql.contains(predicado.trim())) {
            sql = sql.replace("\nGROUP BY", "\n" + predicado + "GROUP BY");
        }
        return new ConsultaGerada(sql, consulta.mascaramentoNecessario(), consulta.explicacao() + " RLS por unidade aplicado ao SQL antes da validação.");
    }
}
