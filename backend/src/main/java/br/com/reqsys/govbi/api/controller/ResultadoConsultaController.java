package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.dominio.porta.ResultadoConsultaPersistidaPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resultados")
public class ResultadoConsultaController {
    private final ResultadoConsultaPersistidaPort resultadoConsultaPersistidaPort;

    public ResultadoConsultaController(ResultadoConsultaPersistidaPort resultadoConsultaPersistidaPort) {
        this.resultadoConsultaPersistidaPort = resultadoConsultaPersistidaPort;
    }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(resultadoConsultaPersistidaPort.listarRecentes(limite));
    }

    @GetMapping("/aprovacoes/{aprovacaoId}")
    public ResponseEntity<?> porAprovacao(@PathVariable String aprovacaoId, @RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(resultadoConsultaPersistidaPort.listarPorAprovacao(aprovacaoId, limite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return resultadoConsultaPersistidaPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
