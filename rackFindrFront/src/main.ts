import { platformBrowser } from '@angular/platform-browser';
import { AppModule } from './app/app-module';

/**
 * Punto de entrada principal de la aplicación frontend RackFindr (Angular).
 * Inicializa el módulo raíz {@link AppModule} en el navegador.
 */
platformBrowser().bootstrapModule(AppModule, {
  
})
  .catch(err => console.error(err));
