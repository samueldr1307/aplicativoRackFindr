import { ChangeDetectorRef, Component } from '@angular/core';
import { Resultado } from '../../models/resultado.model';

@Component({
  selector: 'app-busquedacomponent',
  standalone: false,
  templateUrl: './busquedacomponent.html',
  styleUrl: './busquedacomponent.css',
})
export class Busquedacomponent {
  ecosistema: 'npm' | 'maven' = 'npm';
  consulta: string = '';
  cargando: boolean = false;
  buscado: boolean = false;
  error: string = '';
  resultados: Resultado[] = [];

  datosNpm: Resultado[] = [
    {
      nombre: 'GSAP',
      descripcion: 'Librería potente para animar cualquier elemento.',
      instalacion: 'npm install gsap',
      ejemplo: "gsap.to('.caja', { x: 100, duration: 1 });",
    },
    {
      nombre: '@angular/animations',
      descripcion: 'Animaciones integradas en Angular.',
      instalacion: 'npm install @angular/animations',
      ejemplo: "trigger('abrir', [state('cerrado', style({ height: 0 }))])",
    },
  ];

  datosMaven: Resultado[] = [
    {
      nombre: 'Spring Boot Starter Validation',
      descripcion: 'Valida los datos que llegan al backend.',
      instalacion:
        '<dependency>\n  <groupId>org.springframework.boot</groupId>\n  <artifactId>spring-boot-starter-validation</artifactId>\n</dependency>',
      ejemplo: '@NotBlank private String nombre;',
    },
    {
      nombre: 'Lombok',
      descripcion: 'Reduce el código repetitivo de las clases Java.',
      instalacion:
        '<dependency>\n  <groupId>org.projectlombok</groupId>\n  <artifactId>lombok</artifactId>\n</dependency>',
      ejemplo: '@Data public class Usuario { private String nombre; }',
    },
  ];

  constructor(private cdr: ChangeDetectorRef) {}

  elegir(valor: 'npm' | 'maven'): void {
    this.ecosistema = valor;
  }

  buscar(): void {
    if (!this.consulta.trim()) return;

    this.cargando = true;
    this.buscado = false;
    this.error = '';
    this.resultados = [];

    setTimeout(() => {
      if (this.consulta.toLowerCase().includes('error')) {
        this.error = 'No se pudo obtener respuesta. Intenta de nuevo.';
      } else {
        this.resultados = this.ecosistema === 'npm' ? this.datosNpm : this.datosMaven;
        this.buscado = true;
      }

      this.cargando = false;
      this.cdr.detectChanges();
    }, 1500);
  }
}
