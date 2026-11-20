import { Component } from '@angular/core';
import{HistorialService} from '../../services/historial.service';

@Component({
  selector: 'app-historialcomponent',
  standalone: false,
  styleUrl: './historialcomponent.css',
  templateUrl: './historialcomponent.html',
})
export class Historialcomponent {
  constructor(public historialService: HistorialService) {
  }
}
