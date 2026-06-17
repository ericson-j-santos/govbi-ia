package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.AuditoriaConsultavelPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaConsultaController {
    private final AuditoriaConsultavelPort auditoriaConsultavelPort;

    public AuditoriaConsultaController(AuditoriaConsultavelPort auditoriaConsultavelPort) {
        this.auditoriaConsultavelPort = auditoriaConsultavelPort;
    }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "100") int limite) {
        return ResponseEntity.ok(auditoriaConsultavelPort.listarRecentes(limite));
    }

    @GetMapping("/{correlationId}")
    public ResponseEntity<?> porCorrelationId(@PathVariable String correlationId) {
        return ResponseEntity.ok(auditoriaConsultavelPort.buscarPorCorrelationId(correlationId));
    }
}
