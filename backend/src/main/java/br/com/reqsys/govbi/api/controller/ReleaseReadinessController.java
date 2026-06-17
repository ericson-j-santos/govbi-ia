package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.ReleaseReadinessPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/release")
public class ReleaseReadinessController {
    private final ReleaseReadinessPort releaseReadinessPort;

    public ReleaseReadinessController(ReleaseReadinessPort releaseReadinessPort) {
        this.releaseReadinessPort = releaseReadinessPort;
    }

    @GetMapping("/readiness")
    public ResponseEntity<?> readiness() {
        return ResponseEntity.ok(releaseReadinessPort.verificar());
    }
}
