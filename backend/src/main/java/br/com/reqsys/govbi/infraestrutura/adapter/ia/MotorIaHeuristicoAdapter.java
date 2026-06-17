package br.com.reqsys.govbi.infraestrutura.adapter.ia;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.PerguntaAnalitica;
import br.com.reqsys.govbi.dominio.modelo.PlanoConsulta;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;
import br.com.reqsys.govbi.dominio.porta.MotorIaPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotorIaHeuristicoAdapter implements MotorIaPort {
    private static final Pattern ANO_PATTERN = Pattern.compile("\\b(20\\d{2})\\b");

    @Override
    public PlanoConsulta criarPlano(PerguntaAnalitica pergunta, List<TrechoCatalogoSemantico> contextoSemantico) {
        String texto = pergunta.texto().toLowerCase(Locale.ROOT);
        List<String> dimensoes = new ArrayList<>();
        List<String> avisos = new ArrayList<>();
        Map<String, Object> filtros = new LinkedHashMap<>();

        if (texto.contains("mês") || texto.contains("mes") || texto.contains("mensal")) dimensoes.add("ano_mes");
        if (texto.contains("situação") || texto.contains("situacao") || texto.contains("status")) dimensoes.add("situacao");
        if (texto.contains("unidade") || texto.contains("agência") || texto.contains("agencia")) dimensoes.add("unidade");
        if (texto.contains("produto")) dimensoes.add("produto");
        if (dimensoes.isEmpty()) {
            dimensoes.add("ano_mes");
            avisos.add("Dimensão não informada; aplicado agrupamento padrão por mês.");
        }

        Matcher matcher = ANO_PATTERN.matcher(texto);
        if (matcher.find()) filtros.put("ano", Integer.parseInt(matcher.group(1)));

        boolean pii = texto.contains("cpf") || texto.contains("cliente") || texto.contains("nome") || texto.contains("email") || texto.contains("telefone");
        boolean individual = texto.contains("listar") || texto.contains("liste") || texto.contains("detalhe") || texto.contains("individual");
        if (pii) avisos.add("A pergunta menciona dado potencialmente pessoal; o resultado será agregado e mascarado por padrão.");

        return new PlanoConsulta(
                "consultar_indicador",
                "qtd_propostas_cadastradas",
                List.copyOf(new LinkedHashSet<>(dimensoes)),
                filtros,
                avisos,
                pii ? "SENSIVEL_PII" : "AGREGADO_SEM_PII_DIRETA",
                pii && individual
        );
    }

    @Override
    public ConsultaGerada gerarConsulta(PlanoConsulta plano, MetricaSemantica metrica, List<TrechoCatalogoSemantico> contextoSemantico, List<String> feedbackValidacao) {
        List<String> selects = new ArrayList<>();
        List<String> groups = new ArrayList<>();
        Set<String> joins = new LinkedHashSet<>();

        for (String dimensao : plano.dimensoes()) {
            if (!metrica.permiteDimensao(dimensao)) throw new IllegalArgumentException("Dimensão não permitida para a métrica: " + dimensao);
            switch (dimensao) {
                case "ano_mes" -> { selects.add("t.ano_mes"); groups.add("t.ano_mes"); joins.add(metrica.joinsPorDimensao().get("ano_mes")); }
                case "situacao" -> { selects.add("s.descricao_situacao AS situacao"); groups.add("s.descricao_situacao"); joins.add(metrica.joinsPorDimensao().get("situacao")); }
                case "unidade" -> { selects.add("u.nome_unidade AS unidade"); groups.add("u.nome_unidade"); joins.add(metrica.joinsPorDimensao().get("unidade")); }
                case "produto" -> { selects.add("pr.nome_produto AS produto"); groups.add("pr.nome_produto"); joins.add(metrica.joinsPorDimensao().get("produto")); }
                default -> throw new IllegalArgumentException("Dimensão sem mapeamento SQL: " + dimensao);
            }
        }
        if (plano.filtros().containsKey("ano")) joins.add(metrica.joinsPorDimensao().get("ano_mes"));

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n    ").append(String.join(",\n    ", selects)).append(",\n    ")
                .append(metrica.agregacao()).append(" AS valor\nFROM ").append(metrica.tabelaFato()).append("\n");
        joins.forEach(join -> sql.append(join).append("\n"));
        sql.append("WHERE ").append(metrica.filtroPadrao()).append("\n");
        if (plano.filtros().containsKey("ano")) sql.append("  AND t.ano = ").append(plano.filtros().get("ano")).append("\n");
        sql.append("GROUP BY\n    ").append(String.join(",\n    ", groups)).append("\n");
        sql.append("ORDER BY\n    ").append(String.join(",\n    ", groups)).append("\n");
        sql.append("FETCH FIRST 500 ROWS ONLY");

        return new ConsultaGerada(sql.toString(), true, "Consulta gerada pelo fallback heurístico governado.");
    }
}
