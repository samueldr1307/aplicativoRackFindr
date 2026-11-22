import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { URL_BACK } from './api';
import { Router } from '@angular/router';

/**
 * Estructura de la respuesta devuelta por el backend tras un inicio de sesión exitoso.
 */
interface RespuestaLogin {
  token: string;
  role: string;
}

/**
 * Servicio de autenticación y gestión de sesión del usuario en el cliente.
 * Comunica con los endpoints {@code /auth/*} del backend y administra el token JWT y rol en {@code localStorage}.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  /**
   * Autentica a un usuario enviando sus credenciales al endpoint {@code POST /auth/login}.
   * @param username nombre de usuario
   * @param password contraseña en texto plano
   * @returns observable con el token JWT y el rol asignado
   */
  iniciarSesion(username: string, password: string) {
    return this.http.post<RespuestaLogin>(URL_BACK + '/auth/login', {
      username,
      password,
    });
  }

  /**
   * Registra una nueva cuenta de usuario pendiente de confirmación por correo ({@code POST /auth/register}).
   * @param username nombre de usuario único
   * @param email correo electrónico para recibir el enlace de activación
   * @param password contraseña de la cuenta
   * @returns observable con el mensaje de confirmación del servidor
   */
  registrar(username: string, email: string, password: string) {
    return this.http.post(
      URL_BACK + '/auth/register',
      { username, password, email },
      { responseType: 'text' }, //devolvemos text por que en el login, Spring devuelve un objeto con token y role. En el registro devuelve una frase para mostrar en pantalla.
    );
  }

  /**
   * Solicita el envío de un enlace y código de recuperación de contraseña al correo del usuario.
   * @param usernameOrEmail nombre de usuario o dirección de correo electrónico registrada
   * @returns observable con la respuesta en texto del servidor
   */
  recuperarContrasena(usernameOrEmail: string) {
    const datos = new URLSearchParams();
    datos.set('usernameOrEmail', usernameOrEmail);

    return this.http.post(URL_BACK + '/auth/recuperar-contrasena', datos, {
      responseType: 'text',
    });
  }

  /**
   * Restablece la contraseña del usuario validando el código enviado por correo electrónico.
   * @param usuario nombre de usuario o correo electrónico
   * @param codigo código de verificación de 6 dígitos
   * @param nuevaPassword nueva contraseña a establecer
   * @returns observable con el mensaje de confirmación
   */
  restablecerContrasena(usuario: string, codigo: string, nuevaPassword: string) {
    const datos = new URLSearchParams();
    datos.set('usernameOrEmail', usuario);
    datos.set('codigo', codigo);
    datos.set('nuevaPassword', nuevaPassword);

    return this.http.post(URL_BACK + '/auth/restablecer-contrasena', datos, {
      responseType: 'text',
    });
  }

  /** Señal reactiva que indica si existe un token JWT almacenado en la sesión actual. */
  readonly haySesion = signal(Boolean(localStorage.getItem('token')));

  /**
   * Persiste el token JWT y el rol del usuario en {@code localStorage} y activa el estado de sesión.
   * @param token token JWT emitido por el backend
   * @param role rol del usuario ({@code USER} o {@code ADMIN})
   */
  guardarSesion(token: string, role: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    this.haySesion.set(true);
  }

  /**
   * Determina si el usuario autenticado posee privilegios de administrador.
   * @returns {@code true} si hay sesión activa y el rol es {@code ADMIN} o {@code ROLE_ADMIN}
   */
  esAdmin(): boolean {
    const role = localStorage.getItem('role')?.trim().toUpperCase();
    return this.haySesion() && (role === 'ADMIN' || role === 'ROLE_ADMIN');
  }

  /**
   * Elimina las credenciales de {@code localStorage} y redirige a la página de inicio.
   */
  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.haySesion.set(false);
    void this.router.navigateByUrl('/', { replaceUrl: true });
  }
}
