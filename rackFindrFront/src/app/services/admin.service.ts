import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';
import { URL_BACK } from './api';

export interface UsuarioAdmin {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  enabled: boolean;
}

export interface Auditoria {
  id: number;
  username: string | null;
  accion: string | null;
  fecha: string | null;
  ciudad: string | null;
  pais: string | null;
}

export interface Estadisticas {
  totalUsuarios: number;
  totalAdministradores: number;
  usuariosActivos: number;
  usuariosInactivos: number;
  totalSolicitudes: number;
  solicitudesNpm: number;
  solicitudesMaven: number;
  iniciosSesionExitosos: number;
  iniciosSesionFallidos: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private http: HttpClient) {}

  private get headers(): Record<string, string> {
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  usuarios() {
    return this.http
      .get<UsuarioAdmin[] | null>(`${URL_BACK}/user/getall`, {
        headers: this.headers,
      })
      .pipe(map((lista) => lista ?? []));
  }

  auditoria() {
    return this.http
      .get<Auditoria[] | null>(`${URL_BACK}/auditoria/getall`, {
        headers: this.headers,
      })
      .pipe(map((lista) => lista ?? []));
  }

  estadisticas() {
    return this.http.get<Estadisticas>(`${URL_BACK}/admin/estadisticas`, {
      headers: this.headers,
    });
  }

  crear(usuario: UsuarioAdmin, password: string) {
    const { username, email, role, enabled } = usuario;

    return this.http.post(
      `${URL_BACK}/user/createjson`,
      { username, email, role, enabled, password },
      { headers: this.headers, responseType: 'text' },
    );
  }

  editar(usuario: UsuarioAdmin) {
    const { id, username, email, role, enabled } = usuario;

    return this.http
      .put(
        `${URL_BACK}/user/updatejson`,
        { username, email, role, enabled },
        {
          params: { id },
          headers: this.headers,
          observe: 'response',
          responseType: 'text',
        },
      )
      .pipe(
        map((respuesta) => {
          if (respuesta.status === 226) {
            throw new Error('El nombre de usuario ya está en uso.');
          }
          return respuesta.body;
        }),
      );
  }

  cambiarEstado(usuario: UsuarioAdmin) {
    const accion = usuario.enabled ? 'desactivar' : 'activar';

    return this.http.put(`${URL_BACK}/user/${accion}/${usuario.id}`, null, {
      headers: this.headers,
      responseType: 'text',
    });
  }

  eliminar(id: number) {
    return this.http.delete(`${URL_BACK}/user/deletebyid/${id}`, {
      headers: this.headers,
      responseType: 'text',
    });
  }
}
