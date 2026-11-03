import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Busquedacomponent } from './components/busquedacomponent/busquedacomponent';

const routes: Routes = [{ path: '', component: Busquedacomponent }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
