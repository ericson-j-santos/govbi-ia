import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class OperacionalApiService {
  private readonly baseUrl = '/api/v1';
  constructor(private readonly http: HttpClient) {}

  listarAprovacoesPendentes() { return this.http.get(`${this.baseUrl}/aprovacoes/pendentes`); }
  decidirAprovacao(id: string, decisao: 'APROVADA' | 'REJEITADA', justificativa: string) {
    return this.http.post(`${this.baseUrl}/aprovacoes/${id}/decisao`, { decisao, justificativa });
  }
  reprocessarAprovacao(id: string) { return this.http.post(`${this.baseUrl}/fila-consultas/aprovacoes/${id}/reprocessar`, {}); }
  listarFilaPendente() { return this.http.get(`${this.baseUrl}/fila-consultas/pendentes`); }
  listarHistorico() { return this.http.get(`${this.baseUrl}/historico/recentes`); }
  listarAuditoria() { return this.http.get(`${this.baseUrl}/auditoria/recentes`); }
  listarMetricas() { return this.http.get(`${this.baseUrl}/catalogo/metricas`); }
}
