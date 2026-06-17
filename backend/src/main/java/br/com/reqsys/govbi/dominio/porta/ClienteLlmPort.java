package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.PromptLlm;
import br.com.reqsys.govbi.dominio.modelo.RespostaLlm;

public interface ClienteLlmPort {
    RespostaLlm gerarPlano(PromptLlm prompt);
}
