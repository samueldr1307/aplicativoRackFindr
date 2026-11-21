import { ChangeDetectorRef, Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registrocomponent',
  standalone: false,
  templateUrl: './registrocomponent.html',
  styleUrl: './registrocomponent.css',
})
export class Registrocomponent {
  username = '';
  email = '';
  password = '';
  mensaje = '';
  enviando = false;
  registrado = false;

  constructor(
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  crearCuenta() {
    if (this.enviando || this.registrado) {
      return;
    }

    const usuario = this.username.trim();
    const correo = this.email.trim();

    if (!usuario || !correo || !this.password) {
      this.mensaje = 'Completa los tres campos.';
      return;
    }

    this.mensaje = '';
    this.enviando = true;

    this.authService.registrar(usuario, correo, this.password).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.password = '';
        this.registrado = true;
        this.enviando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        if (error.status === 0) {
          this.mensaje = 'No se pudo conectar con el servidor.';
        } else if (typeof error.error === 'string' && error.error.trim()) {
          this.mensaje = error.error;
        } else {
          this.mensaje = 'No se pudo crear la cuenta. Intenta de nuevo.';
        }

        this.enviando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
