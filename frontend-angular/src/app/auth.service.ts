import { Injectable } from '@angular/core';

export interface ContextoAuth {
  usuario: string;
  perfil: string;
  escopoUnidade: string;
  token?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private contexto: ContextoAuth = {
    usuario: 'usuario-demo',
    perfil: 'ANALISTA',
    escopoUnidade: 'GERAL'
  };

  constructor() {
    const token = localStorage.getItem('govbi.oidc.token');
    const usuario = localStorage.getItem('govbi.oidc.usuario');
    const perfil = localStorage.getItem('govbi.oidc.perfil');
    const escopo = localStorage.getItem('govbi.oidc.escopo');
    if (token) {
      this.contexto.token = token;
    }
    if (usuario) {
      this.contexto.usuario = usuario;
    }
    if (perfil) {
      this.contexto.perfil = perfil;
    }
    if (escopo) {
      this.contexto.escopoUnidade = escopo;
    }
  }

  contextoAtual(): ContextoAuth {
    return { ...this.contexto };
  }

  definirOidc(token: string, usuario: string, perfil: string, escopoUnidade: string): void {
    this.contexto = { token, usuario, perfil, escopoUnidade };
    localStorage.setItem('govbi.oidc.token', token);
    localStorage.setItem('govbi.oidc.usuario', usuario);
    localStorage.setItem('govbi.oidc.perfil', perfil);
    localStorage.setItem('govbi.oidc.escopo', escopoUnidade);
  }
}
