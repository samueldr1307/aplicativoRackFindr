import { Component } from '@angular/core';
import { Resultado } from '../../models/resultado.model';

type Ecosistema = 'npm' | 'maven';

@Component({
  selector: 'app-busquedacomponent',
  standalone: false,
  templateUrl: './busquedacomponent.html',
  styleUrl: './busquedacomponent.css',
})
export class Busquedacomponent {
  ecosistema: Ecosistema = 'npm';
  consulta = '';
  cargando = false;
  error = '';
  buscado = false;
  resultados: Resultado[] = [];

  elegir(ecosistema: Ecosistema) {
    this.ecosistema = ecosistema;
    this.resultados = [];
    this.error = '';
    this.buscado = false;
  }

  buscar() {
    const texto = this.consulta.trim();
    if (!texto || this.cargando) {
      return;
    }
  }
}
