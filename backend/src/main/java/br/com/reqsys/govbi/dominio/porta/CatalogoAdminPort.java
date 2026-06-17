package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.CatalogoAlteracao;
import java.util.List;

public interface CatalogoAdminPort {
    String obterYamlAtual();
    CatalogoAlteracao proporAlteracao(String usuario, String descricao, String novoYaml);
    List<CatalogoAlteracao> listarAlteracoes(int limite);
}
