package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.dto.CatalogoAlteracaoRequest;
import br.com.reqsys.govbi.dominio.porta.CatalogoAdminPort;
import br.com.reqsys.govbi.dominio.porta.CatalogoSemanticoPort;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoAdminController {
    private final CatalogoSemanticoPort catalogoSemanticoPort;
    private final CatalogoAdminPort catalogoAdminPort;

    public CatalogoAdminController(CatalogoSemanticoPort catalogoSemanticoPort, CatalogoAdminPort catalogoAdminPort) {
        this.catalogoSemanticoPort = catalogoSemanticoPort;
        this.catalogoAdminPort = catalogoAdminPort;
    }

    @GetMapping("/metricas")
    public ResponseEntity<?> metricas() {
        return ResponseEntity.ok(catalogoSemanticoPort.listarMetricas());
    }

    @GetMapping(value = "/yaml", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> yaml() {
        return ResponseEntity.ok(catalogoAdminPort.obterYamlAtual());
    }

    @PostMapping("/alteracoes")
    public ResponseEntity<?> proporAlteracao(
            @Valid @RequestBody CatalogoAlteracaoRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "catalogo-admin-demo") String usuario
    ) {
        return ResponseEntity.ok(catalogoAdminPort.proporAlteracao(usuario, request.descricao(), request.novoYaml()));
    }

    @GetMapping("/alteracoes")
    public ResponseEntity<?> listarAlteracoes(@RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(catalogoAdminPort.listarAlteracoes(limite));
    }
}
