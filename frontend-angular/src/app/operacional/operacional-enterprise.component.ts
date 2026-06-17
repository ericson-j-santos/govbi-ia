import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-operacional-enterprise',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule],
  templateUrl: './operacional-enterprise.component.html',
  styleUrl: './operacional-enterprise.component.scss'
})
export class OperacionalEnterpriseComponent {
  colunas = ['tipo', 'status', 'sla', 'acao'];
  itens = [
    { tipo: 'Aprovação sensível', status: 'PENDENTE', sla: '22h restantes' },
    { tipo: 'Fila pós-aprovação', status: 'EM_PROCESSAMENTO', sla: 'Dentro do SLA' },
    { tipo: 'Resultado materializado', status: 'ATIVO', sla: 'Expira em 30 dias' }
  ];
}
