package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.DownloadResultadoControlado;
import br.com.reqsys.govbi.dominio.modelo.UsuarioContexto;

public interface DownloadResultadoPort {
    DownloadResultadoControlado gerar(String resultadoId, String formato, UsuarioContexto usuario);
}
