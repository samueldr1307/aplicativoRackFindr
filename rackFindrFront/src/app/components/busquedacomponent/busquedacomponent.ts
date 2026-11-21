import { ChangeDetectorRef, Component } from '@angular/core';
import { Resultado } from '../../models/resultado.model';
import { HistorialService } from '../../services/historial.service';
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

  constructor(
    private cdr: ChangeDetectorRef,
    private historialService: HistorialService,
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
        this.historialService.guardar(consulta, ecosistema);
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
