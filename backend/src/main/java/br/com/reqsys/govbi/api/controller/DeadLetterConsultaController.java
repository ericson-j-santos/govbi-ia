package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.DeadLetterConsultaPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dlq-consultas")
public class DeadLetterConsultaController {
    private final DeadLetterConsultaPort dlqPort;
    public DeadLetterConsultaController(DeadLetterConsultaPort dlqPort) { this.dlqPort = dlqPort; }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "50") int limite) { return ResponseEntity.ok(dlqPort.listarRecentes(limite)); }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) { return dlqPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }

    @PostMapping("/{id}/marcar-reprocessamento-solicitado")
    public ResponseEntity<?> marcar(@PathVariable String id) { return ResponseEntity.ok(dlqPort.atualizarStatus(id, "REPROCESSAMENTO_SOLICITADO")); }
}
