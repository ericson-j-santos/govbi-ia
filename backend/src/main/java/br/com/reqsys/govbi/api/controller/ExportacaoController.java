package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.dto.ExportacaoRequest;
import br.com.reqsys.govbi.api.seguranca.UsuarioContextoFactory;
import br.com.reqsys.govbi.dominio.porta.ExportadorResultadoPort;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exportacoes")
public class ExportacaoController {
    private final ExportadorResultadoPort exportadorResultadoPort;
    private final UsuarioContextoFactory usuarioContextoFactory;

    public ExportacaoController(ExportadorResultadoPort exportadorResultadoPort, UsuarioContextoFactory usuarioContextoFactory) {
        this.exportadorResultadoPort = exportadorResultadoPort;
        this.usuarioContextoFactory = usuarioContextoFactory;
    }

    @PostMapping
    public ResponseEntity<byte[]> exportar(
            @Valid @RequestBody ExportacaoRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Usuario", defaultValue = "usuario-demo") String usuario,
            @RequestHeader(value = "X-Perfil", defaultValue = "ANALISTA") String perfil,
            @RequestHeader(value = "X-Escopo-Unidade", defaultValue = "GERAL") String escopoUnidade
    ) {
        var contexto = usuarioContextoFactory.resolver(authentication, usuario, perfil, escopoUnidade);
        String correlationId = request.correlationId() == null || request.correlationId().isBlank() ? UUID.randomUUID().toString() : request.correlationId();
        var arquivo = exportadorResultadoPort.exportar(request.resultado(), request.formato(), contexto, correlationId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(arquivo.nomeArquivo()).build().toString())
                .body(arquivo.conteudo());
    }
}
