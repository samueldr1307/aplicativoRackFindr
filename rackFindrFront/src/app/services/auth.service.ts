import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {URL_BACK } from './api';

interface RespuestaLogin {
  token: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  iniciarSesion(username: string, password: string) {
    return this.http.post<RespuestaLogin>(URL_BACK + '/auth/login', {
      username,
      password,
      });
    }
  }

