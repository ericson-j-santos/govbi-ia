package br.com.reqsys.govbi.infraestrutura.adapter.execucao;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.DryRunConsulta;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.ExecutorConsultaPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "govbi.dados.executor", havingValue = "mock", matchIfMissing = true)
public class ExecutorConsultaMockAdapter implements ExecutorConsultaPort {
    private static final long LIMITE_LINHAS_ESTIMADAS = 50_000L;
    private static final double LIMITE_CUSTO_ESTIMADO = 75.0d;

    @Override
    public DryRunConsulta dryRun(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId) {
        String sql = consulta.sql().toLowerCase(Locale.ROOT);
        long linhasEstimadas = estimarLinhas(sql);
        double custo = estimarCusto(sql, linhasEstimadas);
        List<String> avisos = new ArrayList<>();
        List<String> erros = new ArrayList<>();

        if (linhasEstimadas > LIMITE_LINHAS_ESTIMADAS) {
            erros.add("Estimativa de linhas acima do limite permitido para execução conversacional.");
        }
        if (custo > LIMITE_CUSTO_ESTIMADO) {
            erros.add("Custo estimado acima do limite permitido; refine filtros ou solicite aprovação.");
        }
        if (!sql.contains("t.ano =")) {
            avisos.add("Consulta sem filtro anual explícito; em produção recomenda-se filtro temporal obrigatório para tabelas volumosas.");
        }
        if (consulta.mascaramentoNecessario()) {
            avisos.add("Mascaramento LGPD marcado como obrigatório para a resposta.");
        }

        if (!erros.isEmpty()) {
            return DryRunConsulta.bloqueado(erros);
        }
        return DryRunConsulta.aprovado(linhasEstimadas, custo, avisos);
    }

    @Override
    public ResultadoConsulta executar(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId) {
        List<String> colunas = new ArrayList<>();
        String sql = consulta.sql().toLowerCase(Locale.ROOT);

        if (sql.contains("t.ano_mes")) colunas.add("ano_mes");
        if (sql.contains("situacao")) colunas.add("situacao");
        if (sql.contains("unidade")) colunas.add("unidade");
        if (sql.contains("produto")) colunas.add("produto");
        colunas.add("valor");

        List<Map<String, Object>> linhas = new ArrayList<>();
        linhas.add(linha(colunas, "2025-01", "Em análise", "Unidade Centro", "Crédito", 128));
        linhas.add(linha(colunas, "2025-02", "Aprovada", "Unidade Sul", "Crédito", 176));
        linhas.add(linha(colunas, "2025-03", "Reprovada", "Unidade Norte", "Crédito", 42));

        return new ResultadoConsulta(colunas, linhas);
    }

    private long estimarLinhas(String sql) {
        long linhas = 12_000L;
        if (sql.contains("t.ano =")) linhas /= 4;
        if (sql.contains("group by")) linhas /= 3;
        if (sql.contains("unidade")) linhas *= 2;
        if (sql.contains("produto")) linhas *= 2;
        return Math.max(linhas, 250L);
    }

    private double estimarCusto(String sql, long linhasEstimadas) {
        double custo = linhasEstimadas / 1000.0d;
        if (sql.contains("join")) custo += 8.0d;
        if (sql.contains("group by")) custo += 6.0d;
        return Math.round(custo * 100.0d) / 100.0d;
    }

    private Map<String, Object> linha(List<String> colunas, String anoMes, String situacao, String unidade, String produto, int valor) {
        Map<String, Object> linha = new LinkedHashMap<>();
        for (String coluna : colunas) {
            switch (coluna) {
                case "ano_mes" -> linha.put(coluna, anoMes);
                case "situacao" -> linha.put(coluna, situacao);
                case "unidade" -> linha.put(coluna, unidade);
                case "produto" -> linha.put(coluna, produto);
                case "valor" -> linha.put(coluna, valor);
                default -> linha.put(coluna, null);
            }
        }
        return linha;
    }
}
