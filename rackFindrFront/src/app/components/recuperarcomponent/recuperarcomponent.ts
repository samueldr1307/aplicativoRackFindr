import { ChangeDetectorRef, Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-recuperarcomponent',
  standalone: false,
  templateUrl: './recuperarcomponent.html',
  styleUrl: './recuperarcomponent.css',
})
export class Recuperarcomponent {
  usuarioOCorreo = '';
  mensaje = '';
  enviando = false;
  enviado = false;

  constructor(
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  solicitarEnlace() {
    if (this.enviando || this.enviado) {
      return;
    }

    const usuario = this.usuarioOCorreo.trim();

    if (!usuario) {
      this.mensaje = 'Escribe tu usuario o correo electrónico.';
      return;
    }

    this.mensaje = '';
    this.enviando = true;

    this.authService.recuperarContrasena(usuario).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.enviado = true;
        this.enviando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        if (error.status === 0) {
          this.mensaje = 'No se pudo conectar con el servidor.';
        } else if (typeof error.error === 'string' && error.error.trim()) {
          this.mensaje = error.error;
        } else {
          this.mensaje = 'No se pudo solicitar el enlace. Intenta de nuevo.';
        }

        this.enviando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
