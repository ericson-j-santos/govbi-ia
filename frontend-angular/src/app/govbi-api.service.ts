import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PerguntaAnaliticaRequest, RespostaAnalitica } from './modelos';

@Injectable({ providedIn: 'root' })
export class GovBiApiService {
  private readonly baseUrl = '/api/v1/perguntas';

  constructor(private readonly http: HttpClient) {}

  perguntar(request: PerguntaAnaliticaRequest): Observable<RespostaAnalitica> {
    const headers = new HttpHeaders({
      'X-Usuario': 'usuario-demo',
      'X-Perfil': 'ANALISTA',
      'X-Escopo-Unidade': 'GERAL'
    });
    return this.http.post<RespostaAnalitica>(this.baseUrl, request, { headers });
  }
}
