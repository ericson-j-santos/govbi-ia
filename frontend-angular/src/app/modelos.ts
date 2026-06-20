export interface PerguntaAnaliticaRequest {
  pergunta: string;
  formatoResposta: string;
  exibirSql: boolean;
}

export interface ResultadoConsulta {
  colunas: string[];
  linhas: Record<string, string | number | boolean | null>[];
}

export interface TrechoCatalogoSemantico {
  id: string;
  tipo: string;
  nome: string;
  conteudo: string;
  pontuacao: number;
}

export interface TentativaGeracaoConsulta {
  rodada: number;
  aprovada: boolean;
  erros: string[];
  avisos: string[];
}

export interface RespostaAnalitica {
  correlationId: string;
  intencao: string;
  metrica: string;
  dimensoes: string[];
  filtros: Record<string, string | number | boolean>;
  sqlGerado: string | null;
  resultado: ResultadoConsulta;
  avisos: string[];
  mascaramentoAplicado: boolean;
  explicacao: string;
  nivelSensibilidade: string;
  contextoSemantico: TrechoCatalogoSemantico[];
  tentativas: TentativaGeracaoConsulta[];
  linhasEstimadas: number;
  custoEstimado: number;
}

export interface SolicitacaoAprovacao {
  id: string;
  correlationId: string;
  usuarioSolicitante?: string;
  metrica: string;
  nivelSensibilidade: string;
  status: string;
  motivos?: string[];
}

export interface ItemFilaConsulta {
  id: string;
  tipo?: string;
  correlationId: string;
  status: string;
  mensagem?: string;
  metrica?: string;
}

export interface RegistroHistoricoConversa {
  id: string;
  correlationId: string;
  pergunta?: string;
  metrica?: string;
  criadoEm?: string;
}

export interface EventoAuditoriaConsulta {
  id: string;
  correlationId: string;
  tipoEvento: string;
  status: string;
  usuarioHash?: string;
  registradoEm?: string;
}

export interface ResultadoAnaliticoPersistido {
  id: string;
  correlationId: string;
  metrica: string;
  statusRetencao?: string;
}

export interface NotificacaoOperacional {
  id: string;
  canal: string;
  status: string;
  titulo: string;
}

export interface DeadLetterConsulta {
  id: string;
  correlationId: string;
  status: string;
  motivoFalha?: string;
}

export type AprovacaoPendente = SolicitacaoAprovacao;
