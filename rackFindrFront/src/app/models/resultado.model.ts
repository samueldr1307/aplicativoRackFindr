/**
 * Modelo que representa una recomendación de librería o dependencia devuelta por una Inteligencia Artificial.
 */
export interface Resultado {
  /** Nombre del paquete o librería recomendada. */
  nombre: string;
  /** Descripción funcional de la librería. */
  descripcion: string;
  /** Comando o fragmento XML/NPM para instalar la librería. */
  instalacion: string;
  /** Instrucciones breves de configuración y uso. */
  comoUsar: string;
  /** Ejemplo práctico de código. */
  ejemplo: string;
}
