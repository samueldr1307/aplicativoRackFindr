import { Component } from '@angular/core';

interface  UsuarioAdmin {
  id: number;
  username: string;
  email: string;
  role: string;
  activo: boolean;
}
@Component({
  selector: 'app-admincomponent',
  standalone: false,
  styleUrl: './admincomponent.css',
  templateUrl: './admincomponent.html',
})
export class Admincomponent {
  filtro = '';

  usuarios: UsuarioAdmin[] = [
    {
      id: 1,
      username: 'admin.demo',
      email: 'admin@example.com',
      role: 'ADMIN',
      activo: true,
    },
    {
      id: 2,
      username: 'cliente.demo',
      email: 'cliente@example.com',
      role: 'USER',
      activo: false,
    },
  ];

  get usuariosFiltrados(): UsuarioAdmin[]{
    const texto = this.filtro.trim().toLowerCase();
    if(!texto)return this.usuarios;
    return this.usuarios.filter(
      (usuario) =>
        usuario.username.toLowerCase().includes(texto)||
        usuario.email.toLowerCase().includes(texto),
    );
  }
}
