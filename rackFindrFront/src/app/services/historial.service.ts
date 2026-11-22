import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';
import { URL_BACK } from './api';

export interface BusquedaGuardada {
  id: number;
  username: string | null;
  consulta: string;
  ecosistema: string;
  fecha: string;
}

/**
 * Servicio para consultar el historial de búsquedas de librerías almacenadas en el backend.
 */
@Injectable({ providedIn: 'root' })
export class HistorialService {
  constructor(private http: HttpClient) {}

  /**
   * Obtiene las búsquedas del usuario autenticado o el historial global si es administrador.
   * @param token token JWT de la sesión activa
   * @param general {@code true} para consultar todas las solicitudes ({@code /solicitud/getall}), {@code false} para las propias
   * @returns observable con la lista de búsquedas ordenada de la más reciente a la más antigua
   */
  consultar(token: string, general = false) {
    const ruta = general ? '/solicitud/getall' : '/solicitud/mis-solicitudes';

    return this.http
      .get<BusquedaGuardada[] | null>(`${URL_BACK}${ruta}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .pipe(map((lista) => [...(lista ?? [])].sort((a, b) => b.fecha.localeCompare(a.fecha))));
  }
}
