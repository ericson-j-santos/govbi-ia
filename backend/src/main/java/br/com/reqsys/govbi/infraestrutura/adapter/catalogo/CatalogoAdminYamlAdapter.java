package br.com.reqsys.govbi.infraestrutura.adapter.catalogo;

import br.com.reqsys.govbi.dominio.modelo.CatalogoAlteracao;
import br.com.reqsys.govbi.dominio.porta.CatalogoAdminPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(prefix = "govbi.persistencia.operacional", name = "tipo", havingValue = "memoria", matchIfMissing = true)
public class CatalogoAdminYamlAdapter implements CatalogoAdminPort {
    private final Resource resource;
    private final boolean edicaoHabilitada;
    private final CopyOnWriteArrayList<CatalogoAlteracao> alteracoes = new CopyOnWriteArrayList<>();

    public CatalogoAdminYamlAdapter(
            @Value("classpath:catalogo-semantico.yml") Resource resource,
            @Value("${produto-operacional.admin.catalogo-edicao-habilitada:false}") boolean edicaoHabilitada) {
        this.resource = resource;
        this.edicaoHabilitada = edicaoHabilitada;
    }

    @Override
    public String obterYamlAtual() {
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível ler catálogo semântico", e);
        }
    }

    @Override
    public CatalogoAlteracao proporAlteracao(String usuario, String descricao, String novoYaml) {
        if (novoYaml == null || !novoYaml.contains("versao:" ) || !novoYaml.contains("metricas:")) {
            throw new IllegalArgumentException("Catálogo proposto inválido: versão e métricas são obrigatórias.");
        }
        String status = edicaoHabilitada ? "PROPOSTA_REGISTRADA" : "PROPOSTA_REGISTRADA_SEM_APLICACAO_AUTOMATICA";
        var alteracao = new CatalogoAlteracao(UUID.randomUUID().toString(), usuario, descricao, status,
                "Alteração proposta com " + novoYaml.length() + " caracteres. Aplicação automática=" + edicaoHabilitada,
                Instant.now());
        alteracoes.add(alteracao);
        return alteracao;
    }

    @Override
    public List<CatalogoAlteracao> listarAlteracoes(int limite) {
        return alteracoes.stream()
                .sorted(Comparator.comparing(CatalogoAlteracao::registradaEm).reversed())
                .limit(Math.max(1, limite))
                .toList();
    }
}
