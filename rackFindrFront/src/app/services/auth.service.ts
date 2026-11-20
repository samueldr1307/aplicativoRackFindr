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
    registrar(username: string, password: string, email: string) {
    return this.http.post(
      URL_BACK + '/auth/register',
      { username, password, email },
      { responseType: 'text' }, //devolvemos text por que en el login, Spring devuelve un objeto con token y role. En el registro devuelve una frase para mostrar en pantalla.
    );
    }
  }

