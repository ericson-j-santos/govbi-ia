import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { finalize } from 'rxjs';
import { GovBiApiService } from './govbi-api.service';
import { RespostaAnalitica } from './modelos';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatExpansionModule,
    MatChipsModule,
    MatProgressBarModule,
    MatSnackBarModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  readonly pergunta = new FormControl('Mostre propostas cadastradas por mês em 2025 por situação e unidade', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(10)]
  });

  readonly carregando = signal(false);
  readonly resposta = signal<RespostaAnalitica | null>(null);
  readonly colunas = computed(() => this.resposta()?.resultado.colunas ?? []);

  constructor(
    private readonly api: GovBiApiService,
    private readonly snackBar: MatSnackBar
  ) {}

  perguntar(): void {
    if (this.pergunta.invalid) {
      this.snackBar.open('Informe uma pergunta analítica com pelo menos 10 caracteres.', 'Fechar', { duration: 3500 });
      return;
    }

    this.carregando.set(true);
    this.api.perguntar({
      pergunta: this.pergunta.value,
      formatoResposta: 'tabela_grafico',
      exibirSql: true
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: resposta => this.resposta.set(resposta),
        error: erro => this.snackBar.open(erro?.error?.mensagem ?? 'Falha ao consultar o backend.', 'Fechar', { duration: 5000 })
      });
  }

  exemplo(texto: string): void {
    this.pergunta.setValue(texto);
    this.perguntar();
  }
}
