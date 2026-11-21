import { Component } from '@angular/core';

interface UsuarioAdmin {
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
  creandoCuenta = false;
  errorFormulario = '';

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
  eliminarUsuario(usuario: UsuarioAdmin) {
    const confirmado = window.confirm(
      `¿Eliminar la cuenta de ${usuario.username}? También se borrarán sus búsquedas.`,
    );

    if (!confirmado) return;

    this.usuarios = this.usuarios.filter((cuenta) => cuenta.id !== usuario.id);
    if (this.usuarioEnEdicion?.id === usuario.id) {
      this.cancelarEdicion();
    }
  }

  nuevaCuenta(): void {
    this.creandoCuenta = true;
    this.errorFormulario = '';
    this.usuarioEnEdicion = {
      id: 0,
      username: '',
      email: '',
      role: 'USER',
      activo: true,
    };
  }

  editarUsuario(usuario: UsuarioAdmin): void {
    this.creandoCuenta = false;
    this.errorFormulario = '';
    this.usuarioEnEdicion = { ...usuario };
  }

  cancelarEdicion(): void {
    this.usuarioEnEdicion = null;
    this.creandoCuenta = false;
    this.errorFormulario = '';
  }

  guardarCuenta(): void {
    const datos = this.usuarioEnEdicion;
    if (!datos) return;

    const username = datos.username.trim();
    const email = datos.email.trim();

    const repetido = this.usuarios.some(
      (usuario) =>
        usuario.id !== datos.id &&
        (usuario.username.toLowerCase() === username.toLowerCase() ||
          usuario.email.toLowerCase() === email.toLowerCase()),
    );

    if (repetido) {
      this.errorFormulario = 'Ya existe una cuenta con ese usuario o correo.';
      return;
    }

    if (this.creandoCuenta) {
      const nuevoId = Math.max(0, ...this.usuarios.map((usuario) => usuario.id)) + 1;
      this.usuarios.push({ ...datos, id: nuevoId, username, email });
      this.filtro = '';
    } else {
      const usuario = this.usuarios.find((cuenta) => cuenta.id === datos.id);
      if (!usuario) return;

      usuario.username = username;
      usuario.email = email;
      usuario.role = datos.role;
    }

    this.cancelarEdicion();
  }

  get totalActivos(): number {
    return this.usuarios.filter((usuario) => usuario.activo).length;
  }
  get totalInactivos(): number {
    return this.usuarios.length - this.totalActivos;
  }
  get totalAdministradores(): number{
    return this.usuarios.filter((usuario) => usuario.role === 'ADMIN').length
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
