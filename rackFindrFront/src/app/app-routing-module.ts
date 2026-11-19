import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Logincomponent} from './components/logincomponent/logincomponent';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'login', component: Logincomponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
