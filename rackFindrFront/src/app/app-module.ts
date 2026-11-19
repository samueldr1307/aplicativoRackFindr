import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient} from '@angular/common/http';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Footercomponent } from './components/footercomponent/footercomponent';
import { Navbarcomponent} from './components/navbarcomponent/navbarcomponent';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [App, Footercomponent, Navbarcomponent],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
export class AppModule {}
