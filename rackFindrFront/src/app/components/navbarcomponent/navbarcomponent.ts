import { Component, inject } from '@angular/core';
import{AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-navbarcomponent',
  standalone: false,
  styleUrl: './navbarcomponent.css',
  templateUrl: './navbarcomponent.html',
})
export class Navbarcomponent {
  readonly authService= inject (AuthService);
}
