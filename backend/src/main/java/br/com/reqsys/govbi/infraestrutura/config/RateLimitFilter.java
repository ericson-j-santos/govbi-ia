package br.com.reqsys.govbi.infraestrutura.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isHabilitado()) {
            return true;
        }
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String chave = chaveCliente(request);
        Janela janela = janelas.compute(chave, (k, atual) -> {
            long agora = Instant.now().getEpochSecond();
            if (atual == null || agora - atual.inicioSegundo >= 60) {
                return new Janela(agora);
            }
            return atual;
        });

        int limite = Math.max(1, properties.getRequisicoesPorMinuto());
        if (janela.contador.incrementAndGet() > limite) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"codigo\":\"RATE_LIMIT_EXCEDIDO\",\"mensagem\":\"Limite de requisições por minuto excedido.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String chaveCliente(HttpServletRequest request) {
        String usuario = request.getHeader("X-Usuario");
        if (usuario != null && !usuario.isBlank()) {
            return "user:" + usuario;
        }
        return "ip:" + request.getRemoteAddr();
    }

    private static final class Janela {
        private final long inicioSegundo;
        private final AtomicInteger contador = new AtomicInteger(0);

        private Janela(long inicioSegundo) {
            this.inicioSegundo = inicioSegundo;
        }
    }
}
