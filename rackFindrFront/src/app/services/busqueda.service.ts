import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Resultado } from '../models/resultado.model';
import { URL_BACK } from './api';

interface ResultadoApi extends Resultado {
  iaOrigen: string | null;
}
@Injectable({
  providedIn: 'root',
})
export class BusquedaService {
  constructor(private http: HttpClient) {}
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
