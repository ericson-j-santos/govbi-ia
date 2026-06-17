package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import br.com.reqsys.govbi.dominio.modelo.PromptLlm;
import br.com.reqsys.govbi.dominio.modelo.RespostaLlm;
import br.com.reqsys.govbi.dominio.porta.ClienteLlmPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "govbi.ia.modo", havingValue = "mock-rag", matchIfMissing = true)
public class ClienteLlmMockAdapter implements ClienteLlmPort {
    private static final Pattern ANO_PATTERN = Pattern.compile("\\b(20\\d{2})\\b");

    @Override
    public RespostaLlm gerarPlano(PromptLlm prompt) {
        String texto = normalizar(prompt.perguntaUsuario());
        List<String> dimensoes = new ArrayList<>();
        List<String> avisos = new ArrayList<>();
        Map<String, Object> filtros = new LinkedHashMap<>();

        if (contem(texto, "mes", "mensal", "evolucao", "historico", "periodo")) dimensoes.add("ano_mes");
        if (contem(texto, "situacao", "status", "aprovada", "reprovada", "analise")) dimensoes.add("situacao");
        if (contem(texto, "unidade", "agencia", "dependencia", "regiao")) dimensoes.add("unidade");
        if (contem(texto, "produto", "modalidade", "linha")) dimensoes.add("produto");

        if (dimensoes.isEmpty()) {
            dimensoes.add("ano_mes");
            avisos.add("Dimensão não informada; aplicada dimensão padrão ano_mes por governança analítica.");
        }

        Matcher matcher = ANO_PATTERN.matcher(texto);
        if (matcher.find()) {
            filtros.put("ano", Integer.parseInt(matcher.group(1)));
        }

        if (contem(texto, "cpf", "cliente", "nome", "email", "telefone")) {
            avisos.add("A pergunta menciona dados pessoais. A resposta será tratada como sensível e agregada por padrão.");
        }

        return new RespostaLlm(
                "qtd_propostas_cadastradas",
                List.copyOf(dimensoes),
                filtros,
                avisos,
                "Plano produzido por adapter mock determinístico, usando contexto semântico recuperado por RAG."
        );
    }

    private boolean contem(String texto, String... termos) {
        for (String termo : termos) {
            if (texto.contains(termo)) return true;
        }
        return false;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }
}
