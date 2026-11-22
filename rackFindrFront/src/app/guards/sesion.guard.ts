import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de ruta que verifica que exista una sesión JWT activa antes de acceder a vistas protegidas.
 * Redirige a {@code /login} si el usuario no ha iniciado sesión.
 */
export const sesionGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.haySesion() ? true : router.createUrlTree(['/login']);
};
