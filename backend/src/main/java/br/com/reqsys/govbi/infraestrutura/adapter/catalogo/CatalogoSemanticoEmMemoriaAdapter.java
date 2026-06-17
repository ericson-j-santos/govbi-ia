package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import br.com.reqsys.govbi.dominio.modelo.MetricaSemantica;
import br.com.reqsys.govbi.dominio.modelo.TrechoCatalogoSemantico;
import br.com.reqsys.govbi.dominio.porta.CatalogoSemanticoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "govbi.catalogo.tipo", havingValue = "memoria")
public class CatalogoSemanticoEmMemoriaAdapter implements CatalogoSemanticoPort {
    private final Map<String, MetricaSemantica> metricas;
    private final List<TrechoCatalogoSemantico> trechos;

    public CatalogoSemanticoEmMemoriaAdapter() {
        this.metricas = new LinkedHashMap<>();
        this.metricas.put(
                "qtd_propostas_cadastradas",
                new MetricaSemantica(
                        "qtd_propostas_cadastradas",
                        "Quantidade de propostas cadastradas no período informado. Use esta métrica para perguntas sobre volume, total, quantidade ou evolução de propostas.",
                        "gold.fato_proposta p",
                        "COUNT(*)",
                        "p.ic_excluido = 0",
                        List.of("ano_mes", "situacao", "unidade", "produto"),
                        List.of("cpf", "nome_cliente", "email", "telefone"),
                        Map.of(
                                "ano_mes", "JOIN gold.dim_tempo t ON t.id_tempo = p.id_tempo_cadastro",
                                "situacao", "JOIN gold.dim_situacao s ON s.id_situacao = p.id_situacao",
                                "unidade", "JOIN gold.dim_unidade u ON u.id_unidade = p.id_unidade",
                                "produto", "JOIN gold.dim_produto pr ON pr.id_produto = p.id_produto"
                        )
                )
        );

        this.trechos = List.of(
                trecho("metrica.qtd_propostas_cadastradas", "metrica", "qtd_propostas_cadastradas", "Métrica governada para contar propostas cadastradas. Sinônimos: quantidade de propostas, volume de propostas, total de propostas, evolução de propostas."),
                trecho("dim.ano_mes", "dimensao", "ano_mes", "Dimensão temporal mensal. Use quando a pergunta mencionar mês, mensal, evolução, série histórica, ano mês ou comparação entre meses."),
                trecho("dim.situacao", "dimensao", "situacao", "Dimensão de situação/status da proposta. Use quando a pergunta mencionar situação, status, aprovada, reprovada, em análise ou etapa."),
                trecho("dim.unidade", "dimensao", "unidade", "Dimensão organizacional da unidade/agência. Use quando a pergunta mencionar unidade, agência, ponto de atendimento, região ou dependência."),
                trecho("dim.produto", "dimensao", "produto", "Dimensão de produto. Use quando a pergunta mencionar produto, modalidade ou linha de crédito."),
                trecho("lgpd.pii", "seguranca", "dados_pessoais", "Campos sensíveis: cpf, nome_cliente, email e telefone. Perguntas que pedem listagem individual de clientes, CPF ou dados pessoais devem ser bloqueadas ou exigir aprovação humana."),
                trecho("sql.gold", "sql", "camada_gold", "Consultas devem usar exclusivamente objetos governados da camada gold, sempre com SELECT, agregação explícita, WHERE com filtro padrão da métrica e limitador de linhas."),
                trecho("governanca.readonly", "seguranca", "readonly", "São proibidos DDL, DML, múltiplos comandos, comentários SQL, funções perigosas, SELECT * e objetos fora da allowlist semântica.")
        );
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

    private TrechoCatalogoSemantico trecho(String id, String tipo, String nome, String conteudo) {
        return new TrechoCatalogoSemantico(id, tipo, nome, conteudo, 1.0d);
    }

    private double pontuar(String perguntaNormalizada, TrechoCatalogoSemantico trecho) {
        String conteudo = normalizar(trecho.nome() + " " + trecho.conteudo());
        double score = 0.0d;
        for (String token : perguntaNormalizada.split("\\s+")) {
            if (token.length() < 3) {
                continue;
            }
            if (conteudo.contains(token)) {
                score += token.length() >= 6 ? 2.0d : 1.0d;
            }
        }
        if (conteudo.contains("propostas") && perguntaNormalizada.contains("proposta")) score += 4.0d;
        if (conteudo.contains("mensal") && (perguntaNormalizada.contains("mes") || perguntaNormalizada.contains("mensal"))) score += 3.0d;
        if (conteudo.contains("situacao") && (perguntaNormalizada.contains("situacao") || perguntaNormalizada.contains("status"))) score += 3.0d;
        if (conteudo.contains("unidade") && (perguntaNormalizada.contains("unidade") || perguntaNormalizada.contains("agencia"))) score += 3.0d;
        if (conteudo.contains("dados pessoais") && (perguntaNormalizada.contains("cpf") || perguntaNormalizada.contains("cliente"))) score += 4.0d;
        return score;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }
}
