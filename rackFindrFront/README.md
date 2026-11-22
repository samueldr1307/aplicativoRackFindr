# RackFindr Frontend (`rackFindrFront`)

Aplicación web SPA desarrollada en **Angular** y **TypeScript** para la plataforma **RackFindr**: recomendador de librerías de **NPM** y **Maven** asistido por 3 Inteligencias Artificiales (Gemini, OpenAI y Llama), con gestión de sesión JWT, historial de consultas y panel administrativo con gráficas de uso y descarga de reportes en PDF.

---

## Estructura del Proyecto (`src/app`)

| Directorio / Archivo | Descripción |
| :--- | :--- |
| `services/api.ts` | Define la constante `URL_BACK` con la URL del backend desplegado (`https://gpcueb.org/rackFindrBack-0.0.2`). |
| `services/auth.service.ts` | Gestiona login, registro, recuperación/restablecimiento de contraseña y estado reactivo de sesión JWT en `localStorage`. |
| `services/busqueda.service.ts` | Consume `POST /solicitud/buscar` adjuntando el encabezado `Authorization: Bearer <token>`. |
| `services/historial.service.ts` | Consulta el historial personal (`/solicitud/mis-solicitudes`) o general (`/solicitud/getall`). |
| `services/admin.service.ts` | Gestiona usuarios (CRUD, activación/desactivación), estadísticas, auditoría y descarga de reportes PDF (`Blob`). |
| `guards/sesion.guard.ts` | Protege las rutas privadas (`/buscar`, `/historial`) redirigiendo a `/login` si no hay sesión activa. |
| `guards/admin.guard.ts` | Protege la ruta `/admin` verificando sesión activa y rol `ADMIN`. |
| `models/resultado.model.ts` | Interfaz `Resultado` con la estructura de cada recomendación de librería. |
| `components/iniciocomponent` | Página de bienvenida con descripción de funcionalidades y ecosistemas soportados. |
| `components/logincomponent` | Formulario de autenticación con redirección automática según el rol (`USER` o `ADMIN`). |
| `components/registrocomponent` | Registro de cuenta con notificación de activación por correo electrónico. |
| `components/recuperarcomponent` | Solicitud de enlace y código de recuperación de contraseña por correo. |
| `components/restablecercomponent` | Restablecimiento de contraseña mediante código recibido por correo. |
| `components/busquedacomponent` | Buscador comparativo de librerías en NPM o Maven agrupado por las 3 IAs (Gemini, OpenAI y Llama). |
| `components/historialcomponent` | Visualización cronológica de búsquedas realizadas. |
| `components/admincomponent` | Panel administrativo con gestión de usuarios, gráficas de auditoría/uso, filtros y exportación PDF. |
| `components/instalacioncomponent` | Guía visual paso a paso de configuración y ejecución local. |
| `components/navbarcomponent` / `footercomponent` | Barra de navegación dinámica y pie de página. |

---

## Configuración y Ejecución

1. **Configurar la URL del Backend:**
   En `src/app/services/api.ts`:
   ```typescript
   export const URL_BACK = 'https://gpcueb.org/rackFindrBack-0.0.2';
   ```
2. **Instalar dependencias:**
   ```bash
   npm install
   ```
3. **Iniciar el servidor de desarrollo:**
   ```bash
   npm start
   ```
   La aplicación estará disponible en `http://localhost:4200/`.
4. **Compilar para producción:**
   ```bash
   npm run build
   ```
