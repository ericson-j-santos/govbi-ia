package br.com.reqsys.govbi.api.seguranca;

import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UsuarioContextoFactory {
    private final String claimUsuario;
    private final String claimPerfis;
    private final String claimUnidade;

    public UsuarioContextoFactory(
            @Value("${govbi.seguranca.claim-usuario:preferred_username}") String claimUsuario,
            @Value("${govbi.seguranca.claim-perfis:roles}") String claimPerfis,
            @Value("${govbi.seguranca.claim-unidade:escopo_unidade}") String claimUnidade
    ) {
        this.claimUsuario = claimUsuario;
        this.claimPerfis = claimPerfis;
        this.claimUnidade = claimUnidade;
    }

    public UsuarioContexto resolver(Authentication authentication, String usuarioHeader, String perfilHeader, String escopoHeader) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String usuario = valorClaim(jwt, claimUsuario, authentication.getName());
            String perfis = perfisClaim(jwt.getClaim(claimPerfis), perfilHeader);
            String escopo = valorClaim(jwt, claimUnidade, escopoHeader);
            return new UsuarioContexto(usuario, perfis, escopo);
        }
        return new UsuarioContexto(usuarioHeader, perfilHeader, escopoHeader);
    }

    private String valorClaim(Jwt jwt, String claim, String padrao) {
        Object valor = jwt.getClaim(claim);
        return valor == null || valor.toString().isBlank() ? padrao : valor.toString();
    }

    private String perfisClaim(Object valor, String padrao) {
        if (valor instanceof Collection<?> itens) {
            return itens.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(","));
        }
        if (valor instanceof String texto && !texto.isBlank()) {
            return texto;
        }
        if (valor instanceof String[] arr) {
            return String.join(",", arr);
        }
        if (valor instanceof List<?> list) {
            return list.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(","));
        }
        return padrao;
    }
}
