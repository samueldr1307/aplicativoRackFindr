import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Logincomponent} from './components/logincomponent/logincomponent';
import {Registrocomponent} from './components/registrocomponent/registrocomponent';
import{Recuperarcomponent} from './components/recuperarcomponent/recuperarcomponent';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'login', component: Logincomponent },
  {path: 'registro', component: Registrocomponent},
  {path: 'recuperar-contrasena', component: Recuperarcomponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
