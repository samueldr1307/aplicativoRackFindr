import {Injectable,signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {URL_BACK } from './api';

interface RespuestaLogin {
  token: string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  iniciarSesion(username: string, password: string) {
    return this.http.post<RespuestaLogin>(URL_BACK + '/auth/login', {
      username,
      password,
    });
  }
  registrar(username: string, email: string, password: string) {
    return this.http.post(
      URL_BACK + '/auth/register',
      { username, password, email },
      { responseType: 'text' }, //devolvemos text por que en el login, Spring devuelve un objeto con token y role. En el registro devuelve una frase para mostrar en pantalla.
    );
  }
  recuperarContrasena(usernameOrEmail: string) {
    const datos = new URLSearchParams();
    datos.set('usernameOrEmail', usernameOrEmail);

    return this.http.post(URL_BACK + '/auth/recuperar-contrasena', datos, {
      responseType: 'text',
    });
  }
  restablecerContrasena(usuario: string, codigo: string, nuevaPassword: string) {
    const datos = new URLSearchParams();
    datos.set('usernameOrEmail', usuario);
    datos.set('codigo', codigo);
    datos.set('nuevaPassword', nuevaPassword);

    return this.http.post(URL_BACK + '/auth/restablecer-contrasena', datos, {
      responseType: 'text',
    });
  }
  readonly haySesion = signal(Boolean(localStorage.getItem('token')));
  guardarSesion(token: string, role: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    this.haySesion.set(true);
  }

  esAdmin(): boolean {
    const role = localStorage.getItem('role')?.trim().toUpperCase();
    return this.haySesion() && (role === 'ADMIN' || role === 'ROLE_ADMIN');
  }
  cerrarSesion() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.haySesion.set(false);
  }
}
