package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Cliente HTTP com retry exponencial para chamadas LLM (timeout, 429, 5xx).
 */
public final class HttpLlmResilienteClient {

    private final HttpClient httpClient;
    private final Duration requestTimeout;
    private final int maxTentativas;
    private final long backoffInicialMs;

    public HttpLlmResilienteClient(HttpClient httpClient, Duration requestTimeout, int maxTentativas, long backoffInicialMs) {
        this.httpClient = httpClient;
        this.requestTimeout = requestTimeout;
        this.maxTentativas = Math.max(1, maxTentativas);
        this.backoffInicialMs = Math.max(100, backoffInicialMs);
    }

    public HttpResponse<String> enviarComRetry(HttpRequest.Builder requestBuilder) throws InterruptedException, IOException {
        IOException ultimaIo = null;
        for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            try {
                HttpResponse<String> response = httpClient.send(
                        requestBuilder.copy().timeout(requestTimeout).build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                if (deveRetentar(response.statusCode()) && tentativa < maxTentativas) {
                    aguardarBackoff(tentativa);
                    continue;
                }
                return response;
            } catch (IOException e) {
                ultimaIo = e;
                if (tentativa < maxTentativas && (e instanceof java.net.http.HttpTimeoutException || causaTimeout(e))) {
                    aguardarBackoff(tentativa);
                    continue;
                }
                throw e;
            }
        }
        throw ultimaIo != null ? ultimaIo : new IOException("Falha HTTP após " + maxTentativas + " tentativas");
    }

    private static boolean deveRetentar(int status) {
        return status == 429 || status >= 500;
    }

    private static boolean causaTimeout(Throwable e) {
        Throwable atual = e;
        while (atual != null) {
            if (atual instanceof TimeoutException || atual instanceof java.net.http.HttpTimeoutException) {
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }

    private void aguardarBackoff(int tentativa) throws InterruptedException {
        Thread.sleep(backoffInicialMs * (1L << (tentativa - 1)));
    }
}
