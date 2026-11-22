import { Component } from '@angular/core';

interface Paso {
  titulo: string;
  texto: string;
  imagen: string;
  alt: string;
  nota?: string;
}

/**
 * Componente que presenta una guía visual paso a paso para configurar y ejecutar el proyecto en el entorno local.
 */
@Component({
  selector: 'app-instalacioncomponent',
  standalone: false,
  templateUrl: './instalacioncomponent.html',
  styleUrl: './instalacioncomponent.css',
})
export class Instalacioncomponent {
  pasos: Paso[] = [
    {
      titulo: 'Abre las configuraciones de ejecución',
      texto:
        'En la parte superior derecha de WebStorm abre el menú desplegable de configuraciones (Debug Configuration) y selecciona la opción Edit Configurations…',
      imagen: 'imagenes/paso1.png',
      alt: 'Menú desplegable de configuraciones con la opción Edit Configurations',
    },
    {
      titulo: 'Crea una nueva configuración npm',
      texto:
        'En la ventana Run/Debug Configurations haz clic en el botón + (esquina superior izquierda). Se despliega una lista: elige la opción npm. Se creará una nueva configuración en el panel izquierdo.',
      imagen: 'imagenes/paso2.png',
      alt: 'Ventana Run/Debug Configurations con el botón + en la esquina superior izquierda',
    },
    {
      titulo: 'Cambia el comando a install',
      texto:
        'En el campo Command cambia la opción run por install. El campo Scripts queda vacío y la configuración toma el nombre install.',
      imagen: 'imagenes/paso3.png',
      alt: 'Lista del campo Command con la opción install seleccionada',
      nota: 'Mientras el comando siga en run, WebStorm muestra el error "Please specify npm scripts to run". Desaparece al elegir install.',
    },
    {
      titulo: 'Agrega el Node runtime',
      texto:
        'En el campo Node runtime selecciona la opción Add Local… para indicar dónde está instalado Node.js en tu equipo.',
      imagen: 'imagenes/paso4.png',
      alt: 'Opciones Add Local, Add Remote y Add WSL del campo Node runtime',
    },
    {
      titulo: 'Selecciona el archivo node',
      texto:
        'Se abre el explorador de archivos. Ubica la carpeta donde instalaste Node.js, selecciona el archivo node y haz clic en Abrir.',
      imagen: 'imagenes/paso5.png',
      alt: 'Explorador de archivos con el archivo node seleccionado',
    },
    {
      titulo: 'Verifica y guarda la configuración',
      texto:
        'El campo Package manager se completa solo con el npm de la misma carpeta que acabas de agregar. Revisa que Command diga install y haz clic en Apply y luego en OK.',
      imagen: 'imagenes/paso6.png',
      alt: 'Configuración install con Node runtime y Package manager completados',
    },
    {
      titulo: 'Ejecuta install',
      texto:
        'Con la configuración install seleccionada en la barra superior, presiona el botón Run (triángulo verde).',
      imagen: 'imagenes/paso7.png',
      alt: 'Barra superior de WebStorm con la configuración install seleccionada',
    },
    {
      titulo: 'Espera a que termine la instalación',
      texto:
        'Se abre la ventana Run. Espera unos segundos hasta que aparezca el mensaje "Process finished with exit code 0". Eso confirma que las dependencias se instalaron correctamente.',
      imagen: 'imagenes/paso8.png',
      alt: 'Ventana Run con el mensaje Process finished with exit code 0',
      nota: 'Los mensajes "npm warn" son advertencias normales y no impiden que el proyecto funcione.',
    },
    {
      titulo: 'Cambia a Angular CLI Server',
      texto:
        'Abre de nuevo el menú desplegable de configuraciones y cambia install por Angular CLI Server.',
      imagen: 'imagenes/paso9.png',
      alt: 'Menú desplegable con las configuraciones Angular CLI Server e install',
    },
    {
      titulo: 'Ejecuta el proyecto',
      texto:
        'Con Angular CLI Server seleccionado en la barra superior, presiona el botón Run (triángulo verde) para iniciar el servidor de desarrollo.',
      imagen: 'imagenes/paso10.png',
      alt: 'Barra superior con Angular CLI Server seleccionado y el botón Run',
    },
    {
      titulo: 'Abre la aplicación en el navegador',
      texto:
        'En la ventana Run aparece el mensaje "Application bundle generation complete" junto con el enlace Local: http://localhost:4200/. Haz clic en el enlace (o Ctrl + clic) para abrir la aplicación en tu navegador.',
      imagen: 'imagenes/paso11.png',
      alt: 'Ventana Run con el mensaje de compilación completa y el enlace localhost:4200',
      nota: 'Deja el servidor en ejecución mientras trabajas: cada vez que guardes un archivo, la aplicación se recompila sola. Para detenerlo, presiona el botón rojo (Stop) de la barra superior.',
    },
  ];
}
