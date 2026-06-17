package br.com.reqsys.govbi.dominio.porta;

import br.com.reqsys.govbi.dominio.modelo.ResultadoAnaliticoPersistido;
import br.com.reqsys.govbi.dominio.modelo.ResultadoConsulta;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ResultadoConsultaPersistidaPort {
    ResultadoAnaliticoPersistido salvar(String filaId, String aprovacaoId, String correlationId, String metrica, ResultadoConsulta resultado, Instant expiraEm, String mensagem);
    Optional<ResultadoAnaliticoPersistido> buscar(String id);
    List<ResultadoAnaliticoPersistido> listarRecentes(int limite);
    List<ResultadoAnaliticoPersistido> listarPorAprovacao(String aprovacaoId, int limite);
    int expirarResultadosVencidos(Instant agora);
}
