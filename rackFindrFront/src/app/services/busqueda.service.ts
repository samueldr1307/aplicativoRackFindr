import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Resultado } from '../models/resultado.model';
import { URL_BACK } from './api';

interface ResultadoApi extends Resultado {
  iaOrigen: string | null;
}
/**
 * Servicio que consulta al backend ({@code POST /solicitud/buscar}) para obtener recomendaciones
 * de librerías de NPM o Maven generadas por las 3 Inteligencias Artificiales.
 */
@Injectable({
  providedIn: 'root',
})
export class BusquedaService {
  constructor(private http: HttpClient) {}

  /**
   * Envía el ecosistema seleccionado y la consulta del usuario al backend autenticado con JWT.
   * @param ecosistema ecosistema de desarrollo ({@code 'npm'} o {@code 'maven'})
   * @param prompt descripción de la necesidad o librería buscada
   * @returns observable con la lista de recomendaciones generadas por las 3 IAs
   */
  buscar(ecosistema: 'npm' | 'maven', prompt: string) {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set('Authorization', 'Bearer ' + token);
    }

    if (token) {
      headers = headers.set('Authorization', 'Bearer ' + token);
    }
    return this.http.post<ResultadoApi[]>(
      URL_BACK + '/solicitud/buscar',
      { ecosistema, prompt },
      { headers },
    );
  }
}
