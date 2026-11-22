# RackFindr Backend (`rackFindrBack`)

API RESTful desarrollada con **Spring Boot 4.1.3** y **Java 21** para la recomendación inteligente de librerías de **NPM (Angular)** y **Maven (Spring Boot)** consultando simultáneamente **3 Inteligencias Artificiales** (Google Gemini, OpenAI y Llama), con seguridad **JWT**, cifrado **AES-GCM**, envío de correos **SMTP (JavaMail)**, auditoría con geolocalización por IP y generación de reportes en **PDF (OpenPDF)**.

---

## Arquitectura por Paquetes (`co.edu.unbosque.rackFindrBack`)

| Paquete | Descripción |
| :--- | :--- |
| `configuration` | Precarga de usuarios iniciales (`LoadDataBase`) y configuración de Swagger/OpenAPI 3 (`OpenApiConfig`). |
| `controller` | Controladores REST (`AuthController`, `SolicitudController`, `UserController`, `AdminController`, `AuditoriaController`). |
| `dto` | Objetos de Transferencia de Datos (`UserDTO`, `SolicitudDTO`, `ResultadoDTO`, `AuditoriaSesionDTO`, `EstadisticaDTO`). |
| `exception` | Excepciones personalizadas de validación (`PasswordNotValidException`). |
| `model` | Entidades JPA mapeadas a MySQL (`User`, `Solicitud`, `AuditoriaSesion`). |
| `repository` | Repositorios Spring Data JPA (`UserRepository`, `SolicitudRepository`, `AuditoriaSesionRepository`). |
| `security` | Configuración de Spring Security, proveedor BCrypt y filtro stateless JWT (`SecurityConfig`, `JwtAuthenticationFilter`, `JwtUtil`, `UserDetailsServiceImpl`). |
| `service` | Lógica de negocio, integración HTTP con las 3 IAs y geolocalización (`UserService`, `SolicitudService`, `AuditoriaSesionService`, `ExternalHTTPRequestHandler`). |
| `util` | Utilidades de cifrado `AES/GCM/NoPadding` (`AESUtil`), envío de correos SMTP (`EmailSender`) y exportación PDF (`PdfGeneratorUtil`). |

---

## Seguridad y Cifrado

- **Autenticación y Autorización:** Basada en tokens **JWT (HMAC-SHA256)** con validez de 24 horas y control de acceso por roles (`ROLE_USER` y `ROLE_ADMIN`).
- **Contraseñas:** Hash unidireccional mediante `BCryptPasswordEncoder`.
- **Datos Sensibles en Base de Datos:** Correos electrónicos, códigos de verificación/recuperación, prompts, respuestas de IA, direcciones IP y coordenadas geográficas se almacenan cifrados en MySQL mediante **`AES/GCM/NoPadding`** (`AESUtil`).

---

## Endpoints Principales

### Autenticación (`/auth`)
- `POST /auth/login` — Inicia sesión y retorna `{ token, role }`.
- `POST /auth/register` — Registra un usuario inactivo y envía enlace/código de confirmación por correo.
- `GET|POST /auth/confirmar-cuenta` — Activa la cuenta verificando el código enviado al correo.
- `POST /auth/recuperar-contrasena` — Envía enlace y código de recuperación al correo registrado.
- `POST /auth/restablecer-contrasena` — Actualiza la contraseña validando el código de recuperación.

### Solicitudes e IA (`/solicitud`)
- `POST /solicitud/buscar` — Consulta las 3 IAs para el ecosistema (`npm` o `maven`) y guarda la solicitud cifrada.
- `GET /solicitud/mis-solicitudes` — Retorna el historial del usuario autenticado.
- `GET /solicitud/getall` — Retorna todas las solicitudes (rol `ADMIN`).
- `GET /solicitud/exportar-pdf` — Descarga el historial del usuario autenticado en PDF.
- `GET /solicitud/exportar-pdf/id/{id}` — Descarga el reporte PDF de una búsqueda individual.

### Administración (`/admin`, `/user`, `/auditoria`)
- `GET /admin/estadisticas` — Métricas globales de usuarios, solicitudes por ecosistema/IA e inicios de sesión.
- `GET /admin/estadisticas/pdf` — Exporta las estadísticas de uso en PDF.
- `GET /admin/reporte/usuarios/pdf` — Exporta el listado de usuarios en PDF.
- `GET /admin/reporte/pdf` — Exporta el reporte administrativo general en PDF.
- `GET /user/getall`, `POST /user/createjson`, `PUT /user/updatejson`, `PUT /user/activar/{id}`, `PUT /user/desactivar/{id}`, `DELETE /user/deletebyid/{id}` — Gestión CRUD de usuarios.
- `GET /auditoria/getall`, `GET /auditoria/mis-auditorias` — Bitácora de auditoría con fecha, IP, coordenadas, ciudad y país.

---

## Compilación y Despliegue (`.war`)

1. Configurar las credenciales de MySQL y SMTP en `src/main/resources/application.properties`.
2. Generar el archivo `.war` para despliegue en Apache Tomcat:
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. Desplegar el archivo generado en `target/rackFindrBack-0.0.1-SNAPSHOT.war` en el contenedor Tomcat (`https://gpcueb.org/rackFindrBack-0.0.2/`).
4. Para generar la documentación **Javadoc** en HTML (`target/site/apidocs/index.html`):
   ```bash
   ./mvnw javadoc:javadoc
   ```
