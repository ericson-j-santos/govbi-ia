import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { OperacionalApiService } from './operacional-api.service';

describe('OperacionalApiService', () => {
  let service: OperacionalApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OperacionalApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(OperacionalApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve listar aprovações pendentes', () => {
    service.listarAprovacoesPendentes().subscribe();
    const req = httpMock.expectOne('/api/v1/aprovacoes/pendentes');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
