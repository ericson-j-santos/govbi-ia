import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api/') && !req.url.includes('/api/v1/')) {
    return next(req);
  }

  const auth = inject(AuthService).contextoAtual();
  const headers: Record<string, string> = {
    'X-Usuario': auth.usuario,
    'X-Perfil': auth.perfil,
    'X-Escopo-Unidade': auth.escopoUnidade
  };
  if (auth.token) {
    headers['Authorization'] = `Bearer ${auth.token}`;
  }

  return next(req.clone({ setHeaders: headers }));
};
