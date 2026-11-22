import { Component } from '@angular/core';

/**
 * Componente de pie de página con enlaces a los repositorios oficiales de NPM y Maven Central.
 */
@Component({
  selector: 'app-footercomponent',
  standalone: false,
  styleUrl: './footercomponent.css',
  templateUrl: './footercomponent.html',
})
export class Footercomponent {
  anio: number = new Date().getFullYear();
}
