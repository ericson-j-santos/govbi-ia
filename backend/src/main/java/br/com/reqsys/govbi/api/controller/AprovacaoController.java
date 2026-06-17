package br.com.reqsys.govbi.api.controller;

import br.com.reqsys.govbi.api.dto.DecisaoAprovacaoRequest;
import br.com.reqsys.govbi.dominio.modelo.StatusAprovacao;
import br.com.reqsys.govbi.dominio.porta.AprovacaoHumanaPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/aprovacoes")
public class AprovacaoController {
    private final AprovacaoHumanaPort aprovacaoHumanaPort;

    public AprovacaoController(AprovacaoHumanaPort aprovacaoHumanaPort) {
        this.aprovacaoHumanaPort = aprovacaoHumanaPort;
    }

    @GetMapping("/pendentes")
    public ResponseEntity<?> pendentes() {
        return ResponseEntity.ok(aprovacaoHumanaPort.listarPendentes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return aprovacaoHumanaPort.buscar(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/decisao")
    public ResponseEntity<?> decidir(
            @PathVariable String id,
            @Valid @RequestBody DecisaoAprovacaoRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "aprovador-demo") String usuario
    ) {
        var status = StatusAprovacao.valueOf(request.decisao().trim().toUpperCase());
        if (status != StatusAprovacao.APROVADA && status != StatusAprovacao.REJEITADA) {
            throw new IllegalArgumentException("Decisão deve ser APROVADA ou REJEITADA.");
        }
        return ResponseEntity.ok(aprovacaoHumanaPort.decidir(id, status, usuario, request.justificativa()));
    }
}
