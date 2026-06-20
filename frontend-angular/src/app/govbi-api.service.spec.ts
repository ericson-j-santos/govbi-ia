import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { GovBiApiService } from './govbi-api.service';

describe('GovBiApiService', () => {
  let service: GovBiApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GovBiApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(GovBiApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve enviar pergunta para API', () => {
    service.perguntar({ pergunta: 'teste analitico', formatoResposta: 'tabela', exibirSql: true }).subscribe();
    const req = httpMock.expectOne('/api/v1/perguntas');
    expect(req.request.method).toBe('POST');
    req.flush({ correlationId: '1', metrica: 'qtd_propostas_cadastradas' });
  });
});
