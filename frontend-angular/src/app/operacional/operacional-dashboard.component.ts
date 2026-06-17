import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OperacionalApiService } from './operacional-api.service';

@Component({
  selector: 'app-operacional-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './operacional-dashboard.component.html',
  styleUrls: ['./operacional-dashboard.component.scss']
})
export class OperacionalDashboardComponent {
  aprovacoes: any[] = [];
  fila: any[] = [];
  historico: any[] = [];
  auditoria: any[] = [];

  constructor(private readonly api: OperacionalApiService) {}

  carregar(): void {
    this.api.listarAprovacoesPendentes().subscribe((r: any) => this.aprovacoes = Array.isArray(r) ? r : []);
    this.api.listarFilaPendente().subscribe((r: any) => this.fila = Array.isArray(r) ? r : []);
    this.api.listarHistorico().subscribe((r: any) => this.historico = Array.isArray(r) ? r : []);
    this.api.listarAuditoria().subscribe((r: any) => this.auditoria = Array.isArray(r) ? r : []);
  }
}
