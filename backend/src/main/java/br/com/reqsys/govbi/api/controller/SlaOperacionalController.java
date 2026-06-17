package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.aplicacao.caso_uso.ExpirarAprovacoesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sla")
public class SlaOperacionalController {
    private final ExpirarAprovacoesUseCase expirarAprovacoesUseCase;

    public SlaOperacionalController(ExpirarAprovacoesUseCase expirarAprovacoesUseCase) {
        this.expirarAprovacoesUseCase = expirarAprovacoesUseCase;
    }

    @PostMapping("/executar-expiracao")
    public ResponseEntity<?> executarExpiracao() {
        return ResponseEntity.ok(expirarAprovacoesUseCase.executar());
    }
}
