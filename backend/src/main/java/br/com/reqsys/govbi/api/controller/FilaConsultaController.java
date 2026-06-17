package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.dto.ReprocessarAprovacaoRequest;
import br.com.reqsys.govbi.aplicacao.caso_uso.ReprocessarAprovacaoUseCase;
import br.com.reqsys.govbi.aplicacao.caso_uso.ProcessarFilaConsultaUseCase;
import br.com.reqsys.govbi.dominio.porta.FilaConsultaPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fila-consultas")
public class FilaConsultaController {
    private final FilaConsultaPort filaConsultaPort;
    private final ReprocessarAprovacaoUseCase reprocessarAprovacaoUseCase;
    private final ProcessarFilaConsultaUseCase processarFilaConsultaUseCase;

    public FilaConsultaController(FilaConsultaPort filaConsultaPort, ReprocessarAprovacaoUseCase reprocessarAprovacaoUseCase, ProcessarFilaConsultaUseCase processarFilaConsultaUseCase) {
        this.filaConsultaPort = filaConsultaPort;
        this.reprocessarAprovacaoUseCase = reprocessarAprovacaoUseCase;
        this.processarFilaConsultaUseCase = processarFilaConsultaUseCase;
    }

    @GetMapping("/pendentes")
    public ResponseEntity<?> pendentes(@RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(filaConsultaPort.listarPendentes(limite));
    }

    @GetMapping("/recentes")
    public ResponseEntity<?> recentes(@RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(filaConsultaPort.listarRecentes(limite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return filaConsultaPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/processar")
    public ResponseEntity<?> processarAgora(@PathVariable String id) {
        processarFilaConsultaUseCase.processarItem(id);
        return filaConsultaPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/processar-pendentes")
    public ResponseEntity<?> processarPendentes(@RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(java.util.Map.of("processados", processarFilaConsultaUseCase.processarPendentes(limite)));
    }

    @PostMapping("/aprovacoes/{aprovacaoId}/reprocessar")
    public ResponseEntity<?> reprocessar(
            @PathVariable String aprovacaoId,
            @RequestBody(required = false) ReprocessarAprovacaoRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "operador-demo") String usuario
    ) {
        return ResponseEntity.ok(reprocessarAprovacaoUseCase.reprocessar(aprovacaoId, usuario));
    }
}


// v0.8.0: processamento assíncrono controlado também pode ser disparado manualmente em operação assistida.
