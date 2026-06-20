import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { OperacionalApiService } from './operacional-api.service';
import {
  DeadLetterConsulta,
  EventoAuditoriaConsulta,
  ItemFilaConsulta,
  NotificacaoOperacional,
  ResultadoAnaliticoPersistido,
  SolicitacaoAprovacao
} from '../modelos';

@Component({
  selector: 'app-operacional-enterprise-v09',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './operacional-enterprise-v09.component.html',
  styleUrls: ['./operacional-enterprise-v09.component.scss']
})
export class OperacionalEnterpriseV09Component implements OnInit {
  aprovacoes: SolicitacaoAprovacao[] = [];
  fila: ItemFilaConsulta[] = [];
  resultados: ResultadoAnaliticoPersistido[] = [];
  auditorias: EventoAuditoriaConsulta[] = [];
  notificacoes: NotificacaoOperacional[] = [];
  dlq: DeadLetterConsulta[] = [];

  constructor(private readonly api: OperacionalApiService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.api.listarAprovacoesPendentes().subscribe(v => this.aprovacoes = v);
    this.api.listarFilaRecentes().subscribe(v => this.fila = v);
    this.api.listarResultadosRecentes().subscribe(v => this.resultados = v);
    this.api.listarAuditoria().subscribe(v => this.auditorias = v);
    this.api.listarNotificacoesRecentes().subscribe(v => this.notificacoes = v);
    this.api.listarDlqRecentes().subscribe(v => this.dlq = v);
  }

  aprovar(id: string): void {
    this.api.decidirAprovacao(id, 'APROVADA', 'Aprovado pela tela operacional.').subscribe(() => this.carregar());
  }

  rejeitar(id: string): void {
    this.api.decidirAprovacao(id, 'REJEITADA', 'Rejeitado pela tela operacional.').subscribe(() => this.carregar());
  }

  reprocessar(aprovacaoId: string): void {
    this.api.reprocessarAprovacao(aprovacaoId).subscribe(() => this.carregar());
  }

  downloadCsv(resultadoId: string): void {
    window.open(`/api/v1/downloads/resultados/${resultadoId}?formato=csv`, '_blank');
  }
}
