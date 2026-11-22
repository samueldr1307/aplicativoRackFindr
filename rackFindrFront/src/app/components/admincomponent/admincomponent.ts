import { Component, OnInit, computed,  signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminService, Auditoria, Estadisticas, UsuarioAdmin } from '../../services/admin.service';

type Seccion = 'usuarios' | 'estadisticas' | 'auditoria';

/**
 * Componente del panel de administración.
 * Gestiona el CRUD de usuarios, visualización de estadísticas e indicadores en gráficas,
 * consulta y filtrado de la bitácora de auditoría y exportación de reportes en formato PDF.
 */
@Component({
  selector: 'app-admincomponent',
  standalone: false,
  templateUrl: './admincomponent.html',
  styleUrl: './admincomponent.css',
})
export class Admincomponent implements OnInit {
  constructor(private api: AdminService) {}

  usuarios = signal<UsuarioAdmin[]>([]);
  estadisticas = signal<Estadisticas | null>(null);
  auditorias = signal<Auditoria[]>([]);
  graficasAuditoria = computed(() => {
    const acciones = new Map<string, number>();
    const busquedas = new Map<string, number>();
    const dias = new Map<string, number>();

    for (const registro of this.auditorias()) {
      const accion = registro.accion?.trim() || 'Sin acción registrada';
      acciones.set(accion, (acciones.get(accion) ?? 0) + 1);

      if (accion === 'BUSQUEDA_LIBRERIAS') {
        const usuario = registro.username?.trim() || 'Sin usuario registrado';
        busquedas.set(usuario, (busquedas.get(usuario) ?? 0) + 1);
      }

      if (registro.fecha) {
        const dia = registro.fecha.slice(0, 10);
        dias.set(dia, (dias.get(dia) ?? 0) + 1);
      }
    }
    const nombresAcciones: Record<string, string> = {
      LOGIN_EXITOSO: 'Inicio de sesión exitoso',
      LOGIN_CUENTA_NO_CONFIRMADA: 'Intento de ingreso con cuenta sin confirmar',
      BUSQUEDA_LIBRERIAS: 'Búsqueda de librerías',
      REGISTRO_USUARIO: 'Registro de usuario',
    };

    const graficaAcciones = this.prepararGrafica('Acciones registradas por tipo', acciones);

    for (const fila of graficaAcciones.filas) {
      const codigo = fila.etiqueta;
      fila.etiqueta =
        nombresAcciones[codigo] ??
        codigo.charAt(0).toUpperCase() + codigo.slice(1).toLowerCase().replace(/_/g, ' ');
    }
    return [
      graficaAcciones,
      this.prepararGrafica('Búsquedas por usuario', busquedas),
      this.prepararGrafica('Uso por día', dias, true),
    ];
  });

  private prepararGrafica(titulo: string, conteos: Map<string, number>, porFecha = false) {
    const filas = Array.from(conteos, ([etiqueta, cantidad]) => ({ etiqueta, cantidad }));

    filas.sort((a, b) =>
      porFecha
        ? a.etiqueta.localeCompare(b.etiqueta)
        : b.cantidad - a.cantidad || a.etiqueta.localeCompare(b.etiqueta),
    );

    const maximo = filas.reduce((mayor, fila) => Math.max(mayor, fila.cantidad), 1);
    return { titulo, filas, maximo };
  }

  estado = signal<Record<Seccion, string>>({
    usuarios: '',
    estadisticas: '',
    auditoria: '',
  });

  guardando = signal(false);
  errorOperacion = signal('');
  descargandoReporte = signal(false);
  errorReporte = signal('');

  descargarPdf(tipo: 'usuarios' | 'estadisticas' | 'general') {
    if (this.descargandoReporte()) return;

    this.descargandoReporte.set(true);
    this.errorReporte.set('');

    this.api.descargarReporte(tipo).subscribe({
      next: (archivo) => {
        const url = URL.createObjectURL(archivo);
        const enlace = document.createElement('a');

        enlace.href = url;
        enlace.download = `reporte_${tipo}_rackfindr.pdf`;
        document.body.appendChild(enlace);
        enlace.click();
        enlace.remove();

        setTimeout(() => URL.revokeObjectURL(url), 1000);
        this.descargandoReporte.set(false);
      },
      error: (error) => {
        this.errorReporte.set(this.mensaje(error));
        this.descargandoReporte.set(false);
      },
    });
  }

  filtro = '';
  filtroUsuario = '';
  filtroAccion = '';
  usuarioEnEdicion: UsuarioAdmin | null = null;
  creandoCuenta = false;
  password = '';

  ngOnInit() {
    this.recargar();
  }

  recargar() {
    this.cargar('usuarios', this.api.usuarios(), (lista) => this.usuarios.set(lista));
    this.cargar('estadisticas', this.api.estadisticas(), (datos) => this.estadisticas.set(datos));
    this.cargar('auditoria', this.api.auditoria(), (lista) => this.auditorias.set(lista));
  }

  private cargar<T>(seccion: Seccion, peticion: Observable<T>, aplicar: (datos: T) => void) {
    this.estado.update((actual) => ({ ...actual, [seccion]: 'Cargando…' }));

    peticion.subscribe({
      next: (datos) => {
        aplicar(datos);
        this.estado.update((actual) => ({ ...actual, [seccion]: '' }));
      },
      error: (error) => {
        this.estado.update((actual) => ({ ...actual, [seccion]: this.mensaje(error) }));
      },
    });
  }

  private mensaje(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'No se pudo conectar con el servidor.';
      if (error.status === 401) return 'Inicia sesión de nuevo.';
      if (error.status === 403) return 'Tu cuenta no tiene permiso para esta operación.';
      if (error.status === 406)
        return 'No se pudo crear la cuenta. Revisa si el usuario ya existe.';
      return `La solicitud falló (HTTP ${error.status}).`;
    }

    return error instanceof Error ? error.message : 'No se pudo completar la operación.';
  }

  get usuariosFiltrados() {
    const texto = this.filtro.trim().toLowerCase();
    return this.usuarios().filter(
      (usuario) =>
        usuario.username.toLowerCase().includes(texto) ||
        (usuario.email ?? '').toLowerCase().includes(texto),
    );
  }

  get auditoriasFiltradas() {
    const usuario = this.filtroUsuario.trim().toLowerCase();
    const accion = this.filtroAccion.trim().toLowerCase();

    return this.auditorias().filter(
      (registro) =>
        (registro.username ?? '').toLowerCase().includes(usuario) &&
        (registro.accion ?? '').toLowerCase().includes(accion),
    );
  }

  nuevaCuenta() {
    this.errorOperacion.set('');
    this.creandoCuenta = true;
    this.password = '';
    this.usuarioEnEdicion = {
      id: 0,
      username: '',
      email: '',
      role: 'USER',
      enabled: true,
    };
  }

  editarUsuario(usuario: UsuarioAdmin) {
    this.errorOperacion.set('');
    this.creandoCuenta = false;
    this.password = '';
    this.usuarioEnEdicion = { ...usuario, email: usuario.email ?? '' };
  }

  cancelarEdicion() {
    this.usuarioEnEdicion = null;
    this.creandoCuenta = false;
    this.password = '';
  }

  guardarCuenta() {
    if (!this.usuarioEnEdicion || this.guardando()) return;

    const cuenta = { ...this.usuarioEnEdicion };
    cuenta.username = cuenta.username.trim();
    cuenta.email = cuenta.email.trim();

    const repetido = this.usuarios().some(
      (usuario) =>
        usuario.id !== cuenta.id &&
        (usuario.username.toLowerCase() === cuenta.username.toLowerCase() ||
          (usuario.email ?? '').toLowerCase() === cuenta.email.toLowerCase()),
    );

    if (repetido) {
      this.errorOperacion.set('Ya existe una cuenta con ese usuario o correo.');
      return;
    }

    const peticion = this.creandoCuenta
      ? this.api.crear(cuenta, this.password)
      : this.api.editar(cuenta);

    this.ejecutar(peticion);
  }

  cambiarEstado(usuario: UsuarioAdmin) {
    this.ejecutar(this.api.cambiarEstado(usuario));
  }

  eliminarUsuario(usuario: UsuarioAdmin) {
    const confirmado = window.confirm(
      `¿Eliminar la cuenta de ${usuario.username}? También se borrarán sus búsquedas.`,
    );
    if (confirmado) this.ejecutar(this.api.eliminar(usuario.id));
  }

  private ejecutar(peticion: Observable<unknown>) {
    if (this.guardando()) return;

    this.guardando.set(true);
    this.errorOperacion.set('');

    peticion.subscribe({
      next: () => {
        if (this.creandoCuenta) this.filtro = '';
        this.cancelarEdicion();
        this.guardando.set(false);
        this.recargar();
      },
      error: (error) => {
        this.errorOperacion.set(this.mensaje(error));
        this.guardando.set(false);
      },
    });
  }
}
