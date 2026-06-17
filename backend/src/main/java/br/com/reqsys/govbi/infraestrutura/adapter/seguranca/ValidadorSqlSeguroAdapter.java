package br.com.reqsys.govbi.infraestrutura.adapter.seguranca;

import br.com.reqsys.govbi.dominio.modelo.ConsultaGerada;
import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.ValidacaoConsulta;
import br.com.reqsys.govbi.dominio.porta.ValidadorConsultaPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidadorSqlSeguroAdapter implements ValidadorConsultaPort {
    private static final Pattern COMANDOS_PROIBIDOS = Pattern.compile("\\b(insert|update|delete|drop|alter|truncate|merge|create|grant|revoke|execute|exec|call)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern COMENTARIOS = Pattern.compile("(--|/\\*|\\*/)");
    private static final Pattern OBJETOS_SQL = Pattern.compile("\\b(from|join)\\s+([a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern FUNCOES_RISCO = Pattern.compile("\\b(xp_|sp_|openrowset|opendatasource|bulk|copy)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public ValidacaoConsulta validar(ConsultaGerada consulta, MetricaSemantica metrica) {
        List<String> erros = new ArrayList<>();
        List<String> avisos = new ArrayList<>();
        String sqlOriginal = consulta.sql() == null ? "" : consulta.sql();
        String sqlNormalizado = sqlOriginal.strip().toLowerCase(Locale.ROOT);

        if (!sqlNormalizado.startsWith("select")) {
            erros.add("Somente consultas SELECT são permitidas.");
        }
        if (COMANDOS_PROIBIDOS.matcher(sqlNormalizado).find()) {
            erros.add("Consulta contém comando proibido para execução read-only.");
        }
        if (COMENTARIOS.matcher(sqlNormalizado).find()) {
            erros.add("Comentários SQL são bloqueados para reduzir risco de bypass.");
        }
        if (FUNCOES_RISCO.matcher(sqlNormalizado).find()) {
            erros.add("Consulta contém função ou extensão SQL de risco.");
        }
        if (sqlNormalizado.contains(";")) {
            erros.add("Múltiplas instruções SQL não são permitidas.");
        }
        if (Pattern.compile("select\\s+\\*", Pattern.CASE_INSENSITIVE).matcher(sqlOriginal).find()) {
            erros.add("SELECT * não é permitido; informe colunas e métricas explicitamente.");
        }
        if (!sqlNormalizado.contains("from gold.")) {
            erros.add("Consulta deve usar somente objetos governados da camada Gold.");
        }
        if (!(sqlNormalizado.contains("fetch first") || sqlNormalizado.contains("limit") || sqlNormalizado.contains("top"))) {
            avisos.add("Consulta sem limitador explícito detectado; recomenda-se impor limite no executor.");
        }
        if (sqlNormalizado.contains("cpf") || sqlNormalizado.contains("nome_cliente") || sqlNormalizado.contains("email") || sqlNormalizado.contains("telefone")) {
            avisos.add("Consulta menciona campos sensíveis; aplicar mascaramento no resultado e exigir autorização específica se houver detalhamento individual.");
        }

        validarAllowlistObjetos(metrica, sqlOriginal, erros);

        if (!erros.isEmpty()) {
            return ValidacaoConsulta.erro(erros);
        }
        return ValidacaoConsulta.ok(avisos);
    }

    private void validarAllowlistObjetos(MetricaSemantica metrica, String sqlOriginal, List<String> erros) {
        Set<String> permitidos = new LinkedHashSet<>();
        permitidos.add(extrairObjetoBase(metrica.tabelaFato()));
        for (String join : metrica.joinsPorDimensao().values()) {
            String objeto = extrairObjetoDeJoin(join);
            if (objeto != null) {
                permitidos.add(objeto.toLowerCase(Locale.ROOT));
            }
        }

        Matcher matcher = OBJETOS_SQL.matcher(sqlOriginal);
        while (matcher.find()) {
            String objeto = matcher.group(2).toLowerCase(Locale.ROOT);
            if (!permitidos.contains(objeto)) {
                erros.add("Objeto SQL fora da allowlist semântica: " + objeto);
            }
        }
    }

    private String extrairObjetoBase(String tabelaFato) {
        return tabelaFato == null ? "" : tabelaFato.split("\\s+")[0].toLowerCase(Locale.ROOT);
    }

    private String extrairObjetoDeJoin(String join) {
        if (join == null) return null;
        Matcher matcher = OBJETOS_SQL.matcher(join);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }
}
