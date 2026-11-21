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
  usuarioEnEdicion: UsuarioAdmin | null = null;

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

  cambiarEstado(usuario: UsuarioAdmin) {
    usuario.activo = !usuario.activo;
  }

  editarUsuario(usuario: UsuarioAdmin) {
    this.usuarioEnEdicion = { ...usuario };
  }
  cancelarEdicion(): void {
    this.usuarioEnEdicion = null;
  }
  guardarEdicion(): void {
    const editado = this.usuarioEnEdicion;
    if (!editado) return;

    const usuario = this.usuarios.find((cuenta) => cuenta.id === editado.id);
    if (!usuario) return;

    usuario.username = editado.username.trim();
    usuario.email = editado.email.trim();
    usuario.role = editado.role;

    this.usuarioEnEdicion = null;
  }

  get usuariosFiltrados(): UsuarioAdmin[] {
    const texto = this.filtro.trim().toLowerCase();
    if (!texto) return this.usuarios;
    return this.usuarios.filter(
      (usuario) =>
        usuario.username.toLowerCase().includes(texto) ||
        usuario.email.toLowerCase().includes(texto),
    );
  }
}
