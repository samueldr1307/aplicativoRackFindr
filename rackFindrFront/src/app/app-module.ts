import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Navbarcomponent } from './components/navbarcomponent/navbarcomponent';
import { Footercomponent } from './components/footercomponent/footercomponent';
import { Busquedacomponent } from './components/busquedacomponent/busquedacomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Recuperarcomponent } from './components/recuperarcomponent/recuperarcomponent';
import { Restablecercomponent } from './components/restablecercomponent/restablecercomponent';
import { Historialcomponent } from './components/historialcomponent/historialcomponent';
import { Instalacioncomponent } from './components/instalacioncomponent/instalacioncomponent';
import { Admincomponent } from './components/admincomponent/admincomponent';
import { Iniciocomponent } from './components/iniciocomponent/iniciocomponent';

/**
 * Módulo raíz de Angular que declara todos los componentes de la interfaz de usuario,
 * importa los módulos de formularios y enrutamiento, y provee el cliente HTTP.
 */
@NgModule({
  declarations: [
    App,
    Navbarcomponent,
    Footercomponent,
    Busquedacomponent,
    Logincomponent,
    Registrocomponent,
    Recuperarcomponent,
    Restablecercomponent,
    Historialcomponent,
    Instalacioncomponent,
    Admincomponent,
    Iniciocomponent,
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
export class AppModule {}
