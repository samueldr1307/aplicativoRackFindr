import { ChangeDetectorRef, Component } from '@angular/core';
import { Resultado } from '../../models/resultado.model';

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

  datosNpm: Resultado[] = [
    {
      nombre: 'GSAP',
      descripcion: 'Librería potente para animar cualquier elemento.',
      instalacion: 'npm install gsap',
      comoUsar:
        'Importa gsap en tu componente y llama a gsap.to() indicando el elemento y la animación.',
      ejemplo: "gsap.to('.caja', { x: 100, duration: 1 });",
    },
    {
      nombre: '@angular/animations',
      descripcion: 'Animaciones integradas en Angular.',
      instalacion: 'npm install @angular/animations',
      comoUsar:
        'Define un trigger con sus estados dentro del decorador @Component y asócialo a un elemento del HTML.',
      ejemplo: "trigger('abrir', [state('cerrado', style({ height: 0 }))])",
    },
  ];

  datosMaven: Resultado[] = [
    {
      nombre: 'Spring Boot Starter Validation',
      descripcion: 'Valida los datos que llegan al backend.',
      instalacion:
        '<dependency>\n  <groupId>org.springframework.boot</groupId>\n  <artifactId>spring-boot-starter-validation</artifactId>\n</dependency>',
      comoUsar:
        'Agrega anotaciones como @NotBlank a los campos de tu clase y @Valid al parámetro del controlador.',
      ejemplo: '@NotBlank private String nombre;',
    },
    {
      nombre: 'Lombok',
      descripcion: 'Reduce el código repetitivo de las clases Java.',
      instalacion:
        '<dependency>\n  <groupId>org.projectlombok</groupId>\n  <artifactId>lombok</artifactId>\n</dependency>',
      comoUsar:
        'Escribe @Data encima de la clase y Lombok genera los getters, setters y constructores por ti.',
      ejemplo: '@Data public class Usuario { private String nombre; }',
    },
  ];

  constructor(private cdr: ChangeDetectorRef) {}

  elegir(valor: 'npm' | 'maven'): void {
    this.ecosistema = valor;
  }

  buscar(): void {
    const consulta = this.consulta.trim().toLowerCase();

    if (!consulta || this.cargando) {
      return;
    }

    const ejemplos = this.ecosistema === 'npm' ? this.datosNpm : this.datosMaven;

    this.cargando = true;
    this.buscado = false;
    this.error = '';
    this.respuestas = [];

    // Simula la espera mientras conectamos las tres IAs al backend.
    setTimeout(() => {
      if (consulta === 'error') {
        this.error = 'No se pudieron consultar las IAs. Intenta de nuevo.';
      } else if (consulta !== 'sin resultados') {
        const falloTerceraIA = consulta === 'error parcial';

        this.respuestas = [
          {
            nombreIA: 'IA 1',
            recomendaciones: ejemplos.slice(0, 1),
            error: '',
          },
          {
            nombreIA: 'IA 2',
            recomendaciones: ejemplos.slice(1),
            error: '',
          },
          {
            nombreIA: 'IA 3',
            recomendaciones: falloTerceraIA ? [] : [...ejemplos],
            error: falloTerceraIA ? 'Esta IA no pudo responder.' : '',
          },
        ];
      }

      this.cargando = false;
      this.buscado = true;
      this.cdr.detectChanges();
    }, 1500);
  }
}
