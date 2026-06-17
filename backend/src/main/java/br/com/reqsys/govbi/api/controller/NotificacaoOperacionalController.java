package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.NotificacaoOperacionalPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoOperacionalController {
    private final NotificacaoOperacionalPort notificacaoOperacionalPort;

    public NotificacaoOperacionalController(NotificacaoOperacionalPort notificacaoOperacionalPort) {
        this.notificacaoOperacionalPort = notificacaoOperacionalPort;
    }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(notificacaoOperacionalPort.listarRecentes(limite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return notificacaoOperacionalPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
