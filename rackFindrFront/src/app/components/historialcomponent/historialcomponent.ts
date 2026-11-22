import { Component, OnInit, signal } from '@angular/core';
import { BusquedaGuardada, HistorialService } from '../../services/historial.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-historialcomponent',
  standalone: false,
  templateUrl: './historialcomponent.html',
  styleUrl: './historialcomponent.css',
})
export class Historialcomponent implements OnInit {
  busquedas = signal<BusquedaGuardada[]>([]);
  cargando = signal(false);
  error = signal('');

  constructor(
    private historial: HistorialService,
    readonly authService: AuthService,
  ) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    const token = localStorage.getItem('token');
    this.busquedas.set([]);

    if (!token) {
      this.error.set('Inicia sesión para ver tu historial.');
      return;
    }

    this.error.set('');
    this.cargando.set(true);

    this.historial.consultar(token, this.authService.esAdmin()).subscribe({
      next: (lista) => {
        this.busquedas.set(lista);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar tu historial. Revisa la conexión y tu sesión.');
        this.cargando.set(false);
      },
    });
  }
}
