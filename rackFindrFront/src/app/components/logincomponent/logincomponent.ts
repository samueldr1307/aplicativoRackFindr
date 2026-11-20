import { ChangeDetectorRef, Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-logincomponent',
  standalone: false,
  templateUrl: './logincomponent.html',
  styleUrl: './logincomponent.css',
})
export class Logincomponent {
  username = '';
  password = '';
  mensaje = '';
  cargando = false;

  constructor(
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  ingresar() {
    if (this.cargando) {
      return;
    }

    const usuario = this.username.trim();

    if (!usuario || !this.password) {
      this.mensaje = 'Escribe tu usuario y contraseña.';
      return;
    }

    this.mensaje = '';
    this.cargando = true;

    this.authService.iniciarSesion(usuario, this.password).subscribe({
      next: (respuesta) => {
        this.authService.guardarSesion(respuesta.token, respuesta.role);
        this.password = '';
        this.mensaje = 'Sesión iniciada correctamente.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        if (error.status === 0) {
          this.mensaje = 'No se pudo conectar con el servidor.';
        } else if (typeof error.error === 'string' && error.error.trim()) {
          this.mensaje = error.error;
        } else {
          this.mensaje = 'No se pudo iniciar sesión.';
        }

        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
