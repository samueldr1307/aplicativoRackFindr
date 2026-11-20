import{Injectable, signal} from '@angular/core';

interface BusquedaGuardada{
  id: number;
  consulta: string;
  ecosistema: 'npm' | 'maven';
  fecha: Date;
}
@Injectable({
  providedIn: 'root',
})
export class HistorialService {
  private siguienteId = 1;
  private readonly registros = signal<BusquedaGuardada[]>([]);

  readonly busquedas = this.registros.asReadonly();

  guardar(consulta: string, ecosistema: 'npm' | 'maven'): void {
    const busqueda: BusquedaGuardada = {
      id: this.siguienteId++,
      consulta,
      ecosistema,
      fecha: new Date(),
    };

    this.registros.update((anteriores) => [busqueda, ...anteriores]);
  }
}
