import { Component, inject } from '@angular/core';
import{AuthService} from '../../services/auth.service';

/**
 * Componente de barra de navegación superior.
 * Adapta dinámicamente las opciones del menú según el estado de sesión y el rol del usuario.
 */
@Component({
  selector: 'app-navbarcomponent',
  standalone: false,
  styleUrl: './navbarcomponent.css',
  templateUrl: './navbarcomponent.html',
})
export class Navbarcomponent {
  readonly authService= inject (AuthService);
}
