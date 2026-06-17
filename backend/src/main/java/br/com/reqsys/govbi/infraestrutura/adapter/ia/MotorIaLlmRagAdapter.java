package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.PlanoConsulta;
import br.com.reqsys.govbi.dominio.modelo.PromptLlm;
import br.com.reqsys.govbi.dominio.modelo.RespostaLlm;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;
import br.com.reqsys.govbi.dominio.porta.ClienteLlmPort;
import br.com.reqsys.govbi.dominio.porta.MotorIaPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Primary
@Component
public class MotorIaLlmRagAdapter implements MotorIaPort {
    private final ClienteLlmPort clienteLlmPort;

    public MotorIaLlmRagAdapter(ClienteLlmPort clienteLlmPort) {
        this.clienteLlmPort = clienteLlmPort;
    }

    @Override
    public PlanoConsulta criarPlano(PerguntaAnalitica pergunta, List<TrechoCatalogoSemantico> contextoSemantico) {
        RespostaLlm resposta = clienteLlmPort.gerarPlano(new PromptLlm(
                "Converter pergunta de negócio em plano analítico governado",
                pergunta.texto(),
                contextoSemantico,
                List.of()
        ));

        String texto = normalizar(pergunta.texto());
        List<String> avisos = new ArrayList<>(resposta.avisos());
        boolean pedeDadoPessoal = contem(texto, "cpf", "nome", "email", "telefone");
        boolean pedeListagemIndividual = contem(texto, "liste", "listar", "detalhe", "detalhar", "individual", "clientes", "cliente");
        boolean requerAprovacao = pedeDadoPessoal && pedeListagemIndividual;
        String nivelSensibilidade = pedeDadoPessoal ? "SENSIVEL_PII" : "AGREGADO_SEM_PII_DIRETA";

        if (requerAprovacao) {
            avisos.add("Consulta individualizada com dado pessoal exige aprovação humana e não será executada automaticamente.");
        }

        return new PlanoConsulta(
                "consultar_indicador",
                resposta.metrica(),
                List.copyOf(new LinkedHashSet<>(resposta.dimensoes())),
                Map.copyOf(resposta.filtros()),
                List.copyOf(avisos),
                nivelSensibilidade,
                requerAprovacao
        );
    }

    @Override
    public ConsultaGerada gerarConsulta(PlanoConsulta plano, MetricaSemantica metrica, List<TrechoCatalogoSemantico> contextoSemantico, List<String> feedbackValidacao) {
        List<String> selects = new ArrayList<>();
        List<String> groups = new ArrayList<>();
        Set<String> joins = new LinkedHashSet<>();

        for (String dimensao : plano.dimensoes()) {
            if (!metrica.permiteDimensao(dimensao)) {
                throw new IllegalArgumentException("Dimensão não permitida para a métrica: " + dimensao);
            }
            mapearDimensao(metrica, selects, groups, joins, dimensao);
        }

        if (plano.filtros().containsKey("ano")) {
            joins.add(metrica.joinsPorDimensao().get("ano_mes"));
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n    ")
                .append(String.join(",\n    ", selects))
                .append(",\n    ")
                .append(metrica.agregacao())
                .append(" AS valor\nFROM ")
                .append(metrica.tabelaFato())
                .append("\n");

        joins.stream().filter(join -> join != null && !join.isBlank()).forEach(join -> sql.append(join).append("\n"));
        sql.append("WHERE ").append(metrica.filtroPadrao()).append("\n");

        if (plano.filtros().containsKey("ano")) {
            sql.append("  AND t.ano = ").append(plano.filtros().get("ano")).append("\n");
        }

        sql.append("GROUP BY\n    ").append(String.join(",\n    ", groups)).append("\n");
        sql.append("ORDER BY\n    ").append(String.join(",\n    ", groups)).append("\n");
        sql.append("FETCH FIRST 500 ROWS ONLY");

        String explicacao = "Consulta gerada por fluxo LLM/RAG: contexto semântico recuperado, métrica governada selecionada, SQL validável e feedback de validação incorporado.";
        if (!feedbackValidacao.isEmpty()) {
            explicacao += " Ajustes aplicados após feedback: " + String.join(" | ", feedbackValidacao);
        }

        return new ConsultaGerada(sql.toString(), true, explicacao);
    }

    private void mapearDimensao(MetricaSemantica metrica, List<String> selects, List<String> groups, Set<String> joins, String dimensao) {
        switch (dimensao) {
            case "ano_mes" -> {
                selects.add("t.ano_mes");
                groups.add("t.ano_mes");
                joins.add(metrica.joinsPorDimensao().get("ano_mes"));
            }
            case "situacao" -> {
                selects.add("s.descricao_situacao AS situacao");
                groups.add("s.descricao_situacao");
                joins.add(metrica.joinsPorDimensao().get("situacao"));
            }
            case "unidade" -> {
                selects.add("u.nome_unidade AS unidade");
                groups.add("u.nome_unidade");
                joins.add(metrica.joinsPorDimensao().get("unidade"));
            }
            case "produto" -> {
                selects.add("pr.nome_produto AS produto");
                groups.add("pr.nome_produto");
                joins.add(metrica.joinsPorDimensao().get("produto"));
            }
            default -> throw new IllegalArgumentException("Dimensão sem mapeamento SQL: " + dimensao);
        }
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
