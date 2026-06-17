package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.seguranca.UsuarioContextoFactory;
import br.com.reqsys.govbi.dominio.porta.DownloadResultadoPort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/downloads")
public class DownloadResultadoController {
    private final DownloadResultadoPort downloadPort;
    private final UsuarioContextoFactory usuarioFactory;

    public DownloadResultadoController(DownloadResultadoPort downloadPort, UsuarioContextoFactory usuarioFactory) {
        this.downloadPort = downloadPort;
        this.usuarioFactory = usuarioFactory;
    }

    @GetMapping("/resultados/{resultadoId}")
    public ResponseEntity<byte[]> baixar(@PathVariable String resultadoId,
                                         @RequestParam(defaultValue = "csv") String formato,
                                         @RequestHeader(value = "X-Usuario", defaultValue = "analista.demo") String usuario,
                                         @RequestHeader(value = "X-Perfil", defaultValue = "ANALISTA") String perfil,
                                         @RequestHeader(value = "X-Escopo-Unidade", defaultValue = "GERAL") String escopo) {
        var ctx = usuarioFactory.resolver(null, usuario, perfil, escopo);
        var download = downloadPort.gerar(resultadoId, formato, ctx);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(download.nomeArquivo()).build().toString())
                .header("X-Correlation-Id", download.correlationId())
                .body(download.conteudo());
    }
}
