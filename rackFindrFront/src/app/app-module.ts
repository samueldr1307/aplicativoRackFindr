import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Footercomponent } from './components/footercomponent/footercomponent';
import { Navbarcomponent } from './components/navbarcomponent/navbarcomponent';
import { FormsModule } from '@angular/forms';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Recuperarcomponent } from './components/recuperarcomponent/recuperarcomponent';
import { Restablecercomponent } from './components/restablecercomponent/restablecercomponent';

@NgModule({
  declarations: [
    App,
    Footercomponent,
    Navbarcomponent,
    Logincomponent,
    Registrocomponent,
    Recuperarcomponent,
    Restablecercomponent,
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
export class AppModule {}
