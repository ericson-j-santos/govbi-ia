import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DeadLetterConsulta,
  EventoAuditoriaConsulta,
  ItemFilaConsulta,
  NotificacaoOperacional,
  RegistroHistoricoConversa,
  ResultadoAnaliticoPersistido,
  SolicitacaoAprovacao
} from '../modelos';

@Injectable({ providedIn: 'root' })
export class OperacionalApiService {
  private readonly baseUrl = '/api/v1';

  constructor(private readonly http: HttpClient) {}

  get<T>(path: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`);
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  listarAprovacoesPendentes(): Observable<SolicitacaoAprovacao[]> {
    return this.http.get<SolicitacaoAprovacao[]>(`${this.baseUrl}/aprovacoes/pendentes`);
  }

  decidirAprovacao(id: string, decisao: 'APROVADA' | 'REJEITADA', justificativa: string): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/aprovacoes/${id}/decisao`, { decisao, justificativa });
  }

  reprocessarAprovacao(id: string): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/fila-consultas/aprovacoes/${id}/reprocessar`, {});
  }

  listarFilaPendente(): Observable<ItemFilaConsulta[]> {
    return this.http.get<ItemFilaConsulta[]>(`${this.baseUrl}/fila-consultas/pendentes`);
  }

  listarFilaRecentes(): Observable<ItemFilaConsulta[]> {
    return this.http.get<ItemFilaConsulta[]>(`${this.baseUrl}/fila-consultas/recentes`);
  }

  listarHistorico(): Observable<RegistroHistoricoConversa[]> {
    return this.http.get<RegistroHistoricoConversa[]>(`${this.baseUrl}/historico/recentes`);
  }

  listarAuditoria(): Observable<EventoAuditoriaConsulta[]> {
    return this.http.get<EventoAuditoriaConsulta[]>(`${this.baseUrl}/auditoria/recentes`);
  }

  listarResultadosRecentes(): Observable<ResultadoAnaliticoPersistido[]> {
    return this.http.get<ResultadoAnaliticoPersistido[]>(`${this.baseUrl}/resultados/recentes`);
  }

  listarNotificacoesRecentes(): Observable<NotificacaoOperacional[]> {
    return this.http.get<NotificacaoOperacional[]>(`${this.baseUrl}/notificacoes/recentes`);
  }

  listarDlqRecentes(): Observable<DeadLetterConsulta[]> {
    return this.http.get<DeadLetterConsulta[]>(`${this.baseUrl}/dlq-consultas/recentes`);
  }
}
