package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.dto.PerguntaAnaliticaRequest;
import br.com.reqsys.govbi.api.seguranca.UsuarioContextoFactory;
import br.com.reqsys.govbi.aplicacao.caso_uso.ResponderPerguntaAnaliticaUseCase;
import br.com.reqsys.govbi.aplicacao.dto.RespostaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/perguntas")
public class PerguntaAnaliticaController {
    private final ResponderPerguntaAnaliticaUseCase useCase;
    private final UsuarioContextoFactory usuarioContextoFactory;

    public PerguntaAnaliticaController(ResponderPerguntaAnaliticaUseCase useCase, UsuarioContextoFactory usuarioContextoFactory) {
        this.useCase = useCase;
        this.usuarioContextoFactory = usuarioContextoFactory;
    }

    @PostMapping
    public ResponseEntity<RespostaAnalitica> perguntar(
            @Valid @RequestBody PerguntaAnaliticaRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Usuario", defaultValue = "usuario-demo") String usuario,
            @RequestHeader(value = "X-Perfil", defaultValue = "ANALISTA") String perfil,
            @RequestHeader(value = "X-Escopo-Unidade", defaultValue = "GERAL") String escopoUnidade
    ) {
        var pergunta = new PerguntaAnalitica(
                request.pergunta(),
                request.formatoResposta(),
                request.exibirSql() == null || request.exibirSql(),
                Instant.now()
        );
        var contexto = usuarioContextoFactory.resolver(authentication, usuario, perfil, escopoUnidade);
        return ResponseEntity.ok(useCase.executar(pergunta, contexto));
    }
}
