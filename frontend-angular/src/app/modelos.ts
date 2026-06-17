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


export interface AprovacaoPendente {
  id: string;
  correlationId: string;
  metrica: string;
  nivelSensibilidade: string;
  status: string;
}
