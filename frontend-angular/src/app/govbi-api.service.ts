import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PerguntaAnaliticaRequest, RespostaAnalitica } from './modelos';

@Injectable({ providedIn: 'root' })
export class GovBiApiService {
  private readonly baseUrl = '/api/v1/perguntas';

  constructor(private readonly http: HttpClient) {}

  perguntar(request: PerguntaAnaliticaRequest): Observable<RespostaAnalitica> {
    return this.http.post<RespostaAnalitica>(this.baseUrl, request);
  }
}
