import { Component } from '@angular/core';

@Component({
  selector: 'app-iniciocomponent',
  standalone: false,
  templateUrl: './iniciocomponent.html',
  styleUrl: './iniciocomponent.css',
})
export class Iniciocomponent {
  funciones = [
    { titulo: 'Buscar en npm', texto: 'Librerías para tus proyectos Angular.' },
    { titulo: 'Buscar en Maven', texto: 'Dependencias para tus proyectos Spring Boot.' },
    { titulo: 'Guardar historial', texto: 'Vuelve a tus búsquedas cuando quieras.' },
  ];

  pasos = [
    'Crea tu cuenta gratis',
    'Busca por nombre o descripción',
    'Copia el comando de instalación',
  ];

  ecosistemas = [
    { nombre: 'npm', texto: 'Paquetes de JavaScript y Angular.' },
    { nombre: 'Maven', texto: 'Dependencias de Java y Spring Boot.' },
  ];
}
