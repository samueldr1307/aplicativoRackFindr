import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Iniciocomponent } from './components/iniciocomponent/iniciocomponent';
import { Busquedacomponent } from './components/busquedacomponent/busquedacomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Recuperarcomponent } from './components/recuperarcomponent/recuperarcomponent';
import { Restablecercomponent } from './components/restablecercomponent/restablecercomponent';
import {Historialcomponent} from './components/historialcomponent/historialcomponent';
import { Instalacioncomponent } from './components/instalacioncomponent/instalacioncomponent';

const routes: Routes = [
  { path: '', component: Iniciocomponent, pathMatch: 'full' },
  { path: 'buscar', component: Busquedacomponent },
  { path: 'login', component: Logincomponent },
  { path: 'registro', component: Registrocomponent },
  { path: 'recuperar-contrasena', component: Recuperarcomponent },
  { path: 'restablecer-contrasena', component: Restablecercomponent },
  {path: 'historial', component: Historialcomponent },
  { path: 'instalacion', component: Instalacioncomponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
