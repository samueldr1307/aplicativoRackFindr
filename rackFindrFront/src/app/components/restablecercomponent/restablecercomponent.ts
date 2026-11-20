import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import{ ActivatedRoute } from '@angular/router';
import { AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-restablecercomponent',
  standalone: false,
  styleUrl: './restablecercomponent.css',
  templateUrl: './restablecercomponent.html',
})
export class Restablecercomponent implements OnInit {
  usuario = '';
  codigo = '';
  nuevaPassword = '';
  confirmarPassword = '';
  mensaje = '';
  guardando = false;
  actualizada = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const parametros = this.route.snapshot.queryParamMap;
    this.usuario = parametros.get('username') || '';
    this.codigo = parametros.get('codigo') || '';

    if (!this.usuario || !this.codigo) {
      this.mensaje = 'El enlace está incompleto. Solicita uno nuevo.';
    }
  }

  guardar() {
    if (this.guardando || this.actualizada) {
      return;
    }

    if (this.nuevaPassword !== this.confirmarPassword) {
      this.mensaje = 'Las contraseñas no coinciden.';
      return;
    }

    this.mensaje = '';
    this.guardando = true;

    this.authService.restablecerContrasena(
      this.usuario,
      this.codigo,
      this.nuevaPassword,
    ).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.actualizada = true;
        this.guardando = false;
        this.nuevaPassword = '';
        this.confirmarPassword = '';
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.guardando = false;
        this.mensaje = typeof error.error === 'string' && error.error.trim()
          ? error.error
          : 'No se pudo actualizar la contraseña. Intenta de nuevo.';
        this.cdr.detectChanges();
      },
    });
  }
}
