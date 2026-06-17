package br.com.reqsys.govbi.infraestrutura.adapter.execucao.jdbc;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.DryRunConsulta;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;
import br.com.reqsys.govbi.dominio.porta.ExecutorConsultaPort;
import br.com.reqsys.govbi.infraestrutura.config.ConsultaDadosProperties;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

abstract class ExecutorConsultaJdbcBaseAdapter implements ExecutorConsultaPort {
    private final String nomeFonte;
    private final ConsultaDadosProperties properties;
    private final ConsultaDadosProperties.FonteJdbc fonte;

    protected ExecutorConsultaJdbcBaseAdapter(
            String nomeFonte,
            ConsultaDadosProperties properties,
            ConsultaDadosProperties.FonteJdbc fonte
    ) {
        this.nomeFonte = Objects.requireNonNull(nomeFonte, "nomeFonte é obrigatório");
        this.properties = Objects.requireNonNull(properties, "properties é obrigatório");
        this.fonte = Objects.requireNonNull(fonte, "fonte é obrigatória");
    }

    @Override
    public DryRunConsulta dryRun(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId) {
        List<String> errosProntos = validarPreCondicoes(consulta);
        if (!errosProntos.isEmpty()) {
            return DryRunConsulta.bloqueado(errosProntos);
        }

        if (properties.isBloquearSemFiltroTemporal() && !SqlGovernadoUtils.possuiFiltroTemporal(consulta.sql())) {
            return DryRunConsulta.bloqueado(List.of("Consulta real bloqueada: filtro temporal explícito é obrigatório."));
        }

        String sqlPreparado = prepararSqlParaExecucao(consulta.sql());
        String sqlDryRun = sqlDryRun(sqlPreparado);
        try (Connection connection = abrirConexao();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            configurarStatement(statement, 1);
            try (ResultSet resultSet = statement.executeQuery(sqlDryRun)) {
                int colunas = resultSet.getMetaData().getColumnCount();
                long linhasEstimadas = estimarLinhasConservadoramente(consulta.sql(), colunas);
                double custo = estimarCustoConservadoramente(consulta.sql(), linhasEstimadas, colunas);
                if (linhasEstimadas > properties.getLimiteLinhasDryRun()) {
                    return DryRunConsulta.bloqueado(List.of("Estimativa conservadora acima do limite de dry-run."));
                }
                if (custo > properties.getLimiteCustoDryRun()) {
                    return DryRunConsulta.bloqueado(List.of("Custo conservador acima do limite de dry-run."));
                }
                List<String> avisos = new ArrayList<>();
                avisos.add("Dry-run real executado em " + nomeFonte + " com leitura limitada e conexão read-only.");
                if (!SqlGovernadoUtils.possuiLimitador(consulta.sql())) {
                    avisos.add("Executor aplicará limite máximo de " + properties.getLimiteLinhasRetorno() + " linhas no JDBC.");
                }
                if (consulta.mascaramentoNecessario()) {
                    avisos.add("Mascaramento LGPD será aplicado sobre colunas sensíveis retornadas.");
                }
                return DryRunConsulta.aprovado(linhasEstimadas, custo, List.copyOf(avisos));
            }
        } catch (SQLException ex) {
            return DryRunConsulta.bloqueado(List.of("Dry-run real falhou em " + nomeFonte + ": " + mensagemSegura(ex)));
        }
    }

    @Override
    public ResultadoConsulta executar(ConsultaGerada consulta, UsuarioContexto usuarioContexto, String correlationId) {
        List<String> errosProntos = validarPreCondicoes(consulta);
        if (!errosProntos.isEmpty()) {
            throw new IllegalStateException(String.join(" ", errosProntos));
        }
        try (Connection connection = abrirConexao();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            configurarStatement(statement, properties.getLimiteLinhasRetorno());
            String sqlPreparado = prepararSqlParaExecucao(consulta.sql());
            try (ResultSet resultSet = statement.executeQuery(sqlPreparado)) {
                return mapearResultado(resultSet, consulta.mascaramentoNecessario());
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Execução real falhou em " + nomeFonte + ": " + mensagemSegura(ex), ex);
        }
    }

    protected abstract String sqlDryRun(String sqlOriginal);

    protected String prepararSqlParaExecucao(String sqlOriginal) {
        return sqlOriginal;
    }

    private Connection abrirConexao() throws SQLException {
        String driver = fonte.getDriverClassName();
        if (driver != null && !driver.isBlank()) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException ex) {
                throw new SQLException("Driver JDBC não encontrado no classpath: " + driver, ex);
            }
        }

        Properties credenciais = new Properties();
        if (fonte.getUsuario() != null && !fonte.getUsuario().isBlank()) {
            credenciais.put("user", fonte.getUsuario());
        }
        if (fonte.getSenha() != null && !fonte.getSenha().isBlank()) {
            credenciais.put("password", fonte.getSenha());
        }

        Connection connection = DriverManager.getConnection(fonte.getUrl(), credenciais);
        try {
            connection.setReadOnly(true);
        } catch (SQLException ignored) {
            // Alguns drivers tratam read-only como hint. A proteção principal permanece na validação SQL e no usuário de banco somente leitura.
        }
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ignored) {
            // Fontes analíticas como Databricks podem ignorar transações.
        }
        return connection;
    }

    private void configurarStatement(Statement statement, int maxRows) throws SQLException {
        statement.setQueryTimeout(properties.getQueryTimeoutSegundos());
        statement.setMaxRows(Math.max(1, maxRows));
        statement.setFetchSize(Math.max(1, properties.getFetchSize()));
    }

    private ResultadoConsulta mapearResultado(ResultSet resultSet, boolean mascaramentoNecessario) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int quantidadeColunas = metaData.getColumnCount();
        List<String> colunas = new ArrayList<>();
        for (int i = 1; i <= quantidadeColunas; i++) {
            String label = metaData.getColumnLabel(i);
            colunas.add(label == null || label.isBlank() ? metaData.getColumnName(i) : label);
        }

        List<Map<String, Object>> linhas = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> linha = new LinkedHashMap<>();
            for (int i = 1; i <= quantidadeColunas; i++) {
                String coluna = colunas.get(i - 1);
                Object valor = normalizarValor(resultSet.getObject(i));
                linha.put(coluna, MascaradorLgpdJdbc.mascararSeNecessario(coluna, valor, mascaramentoNecessario));
            }
            linhas.add(linha);
        }
        return new ResultadoConsulta(List.copyOf(colunas), List.copyOf(linhas));
    }

    private Object normalizarValor(Object valor) {
        if (valor instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant().toString();
        }
        if (valor instanceof java.sql.Date date) {
            return date.toLocalDate().toString();
        }
        if (valor instanceof java.sql.Time time) {
            return time.toLocalTime().toString();
        }
        if (valor instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toString();
        }
        if (valor instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros();
        }
        return valor;
    }

    private List<String> validarPreCondicoes(ConsultaGerada consulta) {
        List<String> erros = new ArrayList<>();
        if (!properties.isPermitirExecucaoReal()) {
            erros.add("Execução real desabilitada. Habilite govbi.dados.permitir-execucao-real=true somente com usuário read-only e allowlist revisada.");
        }
        if (fonte.getUrl() == null || fonte.getUrl().isBlank()) {
            erros.add("URL JDBC não configurada para " + nomeFonte + ".");
        }
        if (consulta == null || consulta.sql() == null || consulta.sql().isBlank()) {
            erros.add("Consulta SQL vazia.");
        }
        return erros;
    }

    private long estimarLinhasConservadoramente(String sql, int colunas) {
        long base = Math.max(1, properties.getLimiteLinhasRetorno());
        if (SqlGovernadoUtils.possuiFiltroTemporal(sql)) {
            base = Math.max(250L, base / 2L);
        }
        if (sql != null && sql.toLowerCase().contains("group by")) {
            base = Math.max(100L, base / 2L);
        }
        return Math.max(base, colunas * 10L);
    }

    private double estimarCustoConservadoramente(String sql, long linhasEstimadas, int colunas) {
        double custo = linhasEstimadas / 1000.0d + colunas;
        if (sql != null && sql.toLowerCase().contains(" join ")) {
            custo += 8.0d;
        }
        if (sql != null && sql.toLowerCase().contains(" group by ")) {
            custo += 6.0d;
        }
        return Math.round(custo * 100.0d) / 100.0d;
    }

    private String mensagemSegura(SQLException ex) {
        String mensagem = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        mensagem = mensagem.replaceAll("(?i)(password|pwd|token|secret)=([^;\\s]+)", "$1=***");
        if (mensagem.length() > 500) {
            return mensagem.substring(0, 500) + "...";
        }
        return mensagem;
    }
}
