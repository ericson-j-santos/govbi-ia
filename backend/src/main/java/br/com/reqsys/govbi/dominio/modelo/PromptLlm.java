package br.com.reqsys.govbi.dominio.modelo;

import java.util.List;

public record PromptLlm(
        String tarefa,
        String perguntaUsuario,
        List<TrechoCatalogoSemantico> contextoSemantico,
        List<String> feedbackValidacao
) {
}
