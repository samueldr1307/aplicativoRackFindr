import { Component, signal } from '@angular/core';

/**
 * Componente raíz de la aplicación RackFindr.
 * Renderiza la estructura base con barra de navegación, vista activa del router y pie de página.
 */
@Component({
  selector: 'app-root',
  standalone: false,
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  /** Título reactivo de la aplicación. */
  protected readonly title = signal('rackFindrFront');
}
