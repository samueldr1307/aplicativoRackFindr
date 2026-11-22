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

@Injectable({ providedIn: 'root' })
export class HistorialService {
  constructor(private http: HttpClient) {}

  consultar(token: string, general = false) {
    const ruta = general ? '/solicitud/getall' : '/solicitud/mis-solicitudes';

    return this.http
      .get<BusquedaGuardada[] | null>(`${URL_BACK}${ruta}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .pipe(map((lista) => [...(lista ?? [])].sort((a, b) => b.fecha.localeCompare(a.fecha))));
  }
}
