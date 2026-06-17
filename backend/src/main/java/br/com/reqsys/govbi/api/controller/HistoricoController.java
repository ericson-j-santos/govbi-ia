package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.HistoricoConversacionalPort;
import br.com.reqsys.govbi.infraestrutura.util.HashSeguro;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {
    private final HistoricoConversacionalPort historicoConversacionalPort;

    public HistoricoController(HistoricoConversacionalPort historicoConversacionalPort) {
        this.historicoConversacionalPort = historicoConversacionalPort;
    }

    @GetMapping("/meu")
    public ResponseEntity<?> meuHistorico(
            @RequestHeader(value = "X-Usuario", defaultValue = "usuario-demo") String usuario,
            @RequestParam(defaultValue = "20") int limite
    ) {
        return ResponseEntity.ok(historicoConversacionalPort.listarPorUsuarioHash(HashSeguro.sha256(usuario), limite));
    }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(historicoConversacionalPort.listarRecentes(limite));
    }
}
