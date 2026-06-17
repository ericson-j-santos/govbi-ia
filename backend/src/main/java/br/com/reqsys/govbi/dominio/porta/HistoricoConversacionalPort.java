package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.RegistroHistoricoConversa;
import java.util.List;

public interface HistoricoConversacionalPort {
    RegistroHistoricoConversa registrar(RegistroHistoricoConversa registro);
    List<RegistroHistoricoConversa> listarPorUsuarioHash(String usuarioHash, int limite);
    List<RegistroHistoricoConversa> listarRecentes(int limite);
}
