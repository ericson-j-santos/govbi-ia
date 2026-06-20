import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OperacionalApiService } from './operacional-api.service';
import {
  EventoAuditoriaConsulta,
  ItemFilaConsulta,
  RegistroHistoricoConversa,
  SolicitacaoAprovacao
} from '../modelos';

@Component({
  selector: 'app-operacional-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './operacional-dashboard.component.html',
  styleUrls: ['./operacional-dashboard.component.scss']
})
export class OperacionalDashboardComponent implements OnInit {
  aprovacoes: SolicitacaoAprovacao[] = [];
  fila: ItemFilaConsulta[] = [];
  historico: RegistroHistoricoConversa[] = [];
  auditoria: EventoAuditoriaConsulta[] = [];

  constructor(private readonly api: OperacionalApiService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.api.listarAprovacoesPendentes().subscribe(r => this.aprovacoes = Array.isArray(r) ? r : []);
    this.api.listarFilaPendente().subscribe(r => this.fila = Array.isArray(r) ? r : []);
    this.api.listarHistorico().subscribe(r => this.historico = Array.isArray(r) ? r : []);
    this.api.listarAuditoria().subscribe(r => this.auditoria = Array.isArray(r) ? r : []);
  }
}
