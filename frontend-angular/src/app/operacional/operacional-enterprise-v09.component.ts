import { Component, OnInit } from '@angular/core';
import { OperacionalApiService } from './operacional-api.service';

@Component({
  selector: 'app-operacional-enterprise-v09',
  templateUrl: './operacional-enterprise-v09.component.html',
  styleUrls: ['./operacional-enterprise-v09.component.scss']
})
export class OperacionalEnterpriseV09Component implements OnInit {
  aprovacoes: any[] = [];
  fila: any[] = [];
  resultados: any[] = [];
  auditorias: any[] = [];
  notificacoes: any[] = [];
  dlq: any[] = [];

  constructor(private api: OperacionalApiService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.api.get('/aprovacoes/pendentes').subscribe(v => this.aprovacoes = v as any[]);
    this.api.get('/fila-consultas/recentes').subscribe(v => this.fila = v as any[]);
    this.api.get('/resultados/recentes').subscribe(v => this.resultados = v as any[]);
    this.api.get('/auditoria/recentes').subscribe(v => this.auditorias = v as any[]);
    this.api.get('/notificacoes/recentes').subscribe(v => this.notificacoes = v as any[]);
    this.api.get('/dlq-consultas/recentes').subscribe(v => this.dlq = v as any[]);
  }

  aprovar(id: string): void {
    this.api.post(`/aprovacoes/${id}/decisao`, { decisao: 'APROVADA', justificativa: 'Aprovado pela tela operacional.' }).subscribe(() => this.carregar());
  }

  rejeitar(id: string): void {
    this.api.post(`/aprovacoes/${id}/decisao`, { decisao: 'REJEITADA', justificativa: 'Rejeitado pela tela operacional.' }).subscribe(() => this.carregar());
  }

  reprocessar(aprovacaoId: string): void {
    this.api.post(`/fila-consultas/aprovacoes/${aprovacaoId}/reprocessar`, { motivo: 'Reprocessamento solicitado pela tela operacional.' }).subscribe(() => this.carregar());
  }

  downloadCsv(resultadoId: string): void {
    window.open(`/api/v1/downloads/resultados/${resultadoId}?formato=csv`, '_blank');
  }
}
