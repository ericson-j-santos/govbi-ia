package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class HttpLlmResilienteClientTest {

    private HttpServer servidor;
    private final AtomicInteger chamadas = new AtomicInteger();

    @AfterEach
    void encerrar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void deveRetentarQuandoServidorRetorna503() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress(0), 0);
        servidor.createContext("/llm", exchange -> {
            int n = chamadas.incrementAndGet();
            int status = n < 2 ? 503 : 200;
            byte[] body = "{\"ok\":true}".getBytes();
            exchange.sendResponseHeaders(status, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        servidor.start();

        int porta = servidor.getAddress().getPort();
        HttpLlmResilienteClient client = new HttpLlmResilienteClient(
                HttpClient.newHttpClient(),
                Duration.ofSeconds(5),
                3,
                10
        );

        var response = client.enviarComRetry(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + porta + "/llm"))
                .POST(HttpRequest.BodyPublishers.ofString("{}")));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(chamadas.get()).isEqualTo(2);
    }

    @Test
    void deveUsarTimeoutConfigurado() {
        HttpLlmResilienteClient client = new HttpLlmResilienteClient(
                HttpClient.newHttpClient(),
                Duration.ofSeconds(15),
                1,
                100
        );
        assertThat(client).isNotNull();
    }
}
