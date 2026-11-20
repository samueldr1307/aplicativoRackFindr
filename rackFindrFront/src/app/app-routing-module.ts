import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Busquedacomponent } from './components/busquedacomponent/busquedacomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Recuperarcomponent } from './components/recuperarcomponent/recuperarcomponent';
import { Restablecercomponent } from './components/restablecercomponent/restablecercomponent';

const routes: Routes = [
  { path: '', component: Busquedacomponent, pathMatch: 'full' },
  { path: 'login', component: Logincomponent },
  { path: 'registro', component: Registrocomponent },
  { path: 'recuperar-contrasena', component: Recuperarcomponent },
  { path: 'restablecer-contrasena', component: Restablecercomponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}