import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de ruta que restringe el acceso exclusivamente a usuarios autenticados con rol {@code ADMIN}.
 * Redirige a {@code /login} si no hay sesión activa o a {@code /buscar} si el usuario no es administrador.
 */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.haySesion()) {
    return router.createUrlTree(['/login']);
  }

  return auth.esAdmin() ? true : router.createUrlTree(['/buscar']);
};
