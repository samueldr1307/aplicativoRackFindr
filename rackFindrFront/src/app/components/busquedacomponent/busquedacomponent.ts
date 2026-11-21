import { ChangeDetectorRef, Component } from '@angular/core';
import { Resultado } from '../../models/resultado.model';
import { BusquedaService } from '../../services/busqueda.service';

interface RespuestaIA {
  nombreIA: string;
  recomendaciones: Resultado[];
  error: string;
}

@Component({
  selector: 'app-busquedacomponent',
  standalone: false,
  templateUrl: './busquedacomponent.html',
  styleUrl: './busquedacomponent.css',
})
export class Busquedacomponent {
  ecosistema: 'npm' | 'maven' = 'npm';
  consulta = '';
  cargando = false;
  buscado = false;
  error = '';
  respuestas: RespuestaIA[] = [];



  constructor(
    private cdr: ChangeDetectorRef,
    private busquedaService: BusquedaService,
  ) {}

  elegir(valor: 'npm' | 'maven'): void {
    this.ecosistema = valor;
  }

  buscar(): void {
    const consulta = this.consulta.trim();

    if (!consulta || this.cargando) return;

    const ecosistema = this.ecosistema;

    this.cargando = true;
    this.buscado = false;
    this.error = '';
    this.respuestas = [];

    this.busquedaService.buscar(ecosistema, consulta).subscribe({
      next: (resultados) => {
        const grupos = new Map<string, RespuestaIA>();

        for (const resultado of resultados) {
          const nombreIA = resultado.iaOrigen?.trim() || 'IA sin identificar';
          let grupo = grupos.get(nombreIA);

          if (!grupo) {
            grupo = { nombreIA, recomendaciones: [], error: '' };
            grupos.set(nombreIA, grupo);
          }

          grupo.recomendaciones.push(resultado);
        }

        this.respuestas = Array.from(grupos.values());
        this.buscado = true;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        if (error.status === 0) {
          this.error = 'No se pudo conectar con el servidor.';
        } else if (error.status === 401) {
          this.error = 'Inicia sesión para buscar librerías.';
        } else if (error.status === 403) {
          this.error = 'No tienes permiso para realizar esta búsqueda.';
        } else {
          this.error = 'No se pudo completar la búsqueda. Intenta de nuevo.';
        }

        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
