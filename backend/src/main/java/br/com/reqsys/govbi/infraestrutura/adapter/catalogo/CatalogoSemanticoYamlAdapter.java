package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.PoliticaMetrica;
import br.com.reqsys.govbi.dominio.modelo.RlsMetrica;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;
import br.com.reqsys.govbi.dominio.porta.CatalogoSemanticoPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "govbi.catalogo.tipo", havingValue = "yaml", matchIfMissing = true)
public class CatalogoSemanticoYamlAdapter implements CatalogoSemanticoPort {
    private final Map<String, MetricaSemantica> metricas;
    private final List<TrechoCatalogoSemantico> trechos;

    public CatalogoSemanticoYamlAdapter(@Value("${govbi.catalogo.recurso-yaml:classpath:catalogo-semantico.yml}") Resource recursoYaml) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            CatalogoYaml catalogo = mapper.readValue(recursoYaml.getInputStream(), CatalogoYaml.class);
            this.metricas = new LinkedHashMap<>();
            for (MetricaYaml m : catalogo.metricas()) {
                PoliticaYaml p = m.politicaAcesso() == null ? PoliticaYaml.padrao() : m.politicaAcesso();
                RlsYaml r = p.rls() == null ? RlsYaml.padrao() : p.rls();
                var metrica = new MetricaSemantica(
                        m.nome(),
                        m.descricao(),
                        m.tabelaFato(),
                        m.agregacao(),
                        m.filtroPadrao(),
                        List.copyOf(m.dimensoesPermitidas()),
                        List.copyOf(m.camposSensiveis()),
                        Map.copyOf(m.joinsPorDimensao()),
                        new PoliticaMetrica(
                                List.copyOf(p.perfisPermitidos()),
                                List.copyOf(p.perfisAprovadoresPii()),
                                new RlsMetrica(r.campoUnidade(), r.joinObrigatorio(), r.escopoGeral())
                        )
                );
                this.metricas.put(m.nome(), metrica);
            }
            this.trechos = catalogo.trechos().stream()
                    .map(t -> new TrechoCatalogoSemantico(t.id(), t.tipo(), t.nome(), t.conteudo(), 1.0d))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar catálogo semântico YAML governado", e);
        }
    }

    @Override
    public Optional<MetricaSemantica> buscarMetrica(String nome) {
        return Optional.ofNullable(metricas.get(nome));
    }

    @Override
    public List<MetricaSemantica> listarMetricas() {
        return List.copyOf(metricas.values());
    }

    @Override
    public List<TrechoCatalogoSemantico> buscarContexto(String pergunta, int limite) {
        String perguntaNormalizada = normalizar(pergunta);
        return trechos.stream()
                .map(t -> new TrechoCatalogoSemantico(t.id(), t.tipo(), t.nome(), t.conteudo(), pontuar(perguntaNormalizada, t)))
                .filter(t -> t.pontuacao() > 0.0d)
                .sorted(Comparator.comparingDouble(TrechoCatalogoSemantico::pontuacao).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }

    private double pontuar(String perguntaNormalizada, TrechoCatalogoSemantico trecho) {
        String conteudo = normalizar(trecho.nome() + " " + trecho.conteudo());
        double score = 0.0d;
        for (String token : perguntaNormalizada.split("\s+")) {
            if (token.length() < 3) continue;
            if (conteudo.contains(token)) score += token.length() >= 6 ? 2.0d : 1.0d;
        }
        if (conteudo.contains("propostas") && perguntaNormalizada.contains("proposta")) score += 4.0d;
        if (conteudo.contains("mensal") && (perguntaNormalizada.contains("mes") || perguntaNormalizada.contains("mensal"))) score += 3.0d;
        if (conteudo.contains("situacao") && (perguntaNormalizada.contains("situacao") || perguntaNormalizada.contains("status"))) score += 3.0d;
        if (conteudo.contains("unidade") && (perguntaNormalizada.contains("unidade") || perguntaNormalizada.contains("agencia"))) score += 3.0d;
        if (conteudo.contains("dados pessoais") && (perguntaNormalizada.contains("cpf") || perguntaNormalizada.contains("cliente"))) score += 4.0d;
        return score;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CatalogoYaml(String versao, List<MetricaYaml> metricas, List<TrechoYaml> trechos) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MetricaYaml(String nome, String descricao, String tabelaFato, String agregacao, String filtroPadrao, List<String> dimensoesPermitidas, List<String> camposSensiveis, Map<String, String> joinsPorDimensao, PoliticaYaml politicaAcesso) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PoliticaYaml(List<String> perfisPermitidos, List<String> perfisAprovadoresPii, RlsYaml rls) {
        public static PoliticaYaml padrao() { return new PoliticaYaml(List.of("ANALISTA", "ADMIN", "BI_GOVERNADO"), List.of("ADMIN", "DPO", "SEGURANCA_DADOS"), RlsYaml.padrao()); }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RlsYaml(String campoUnidade, String joinObrigatorio, String escopoGeral) {
        public static RlsYaml padrao() { return new RlsYaml("u.codigo_unidade", "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade", "GERAL"); }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TrechoYaml(String id, String tipo, String nome, String conteudo) {}
}
