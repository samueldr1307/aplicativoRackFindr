# RackFindr — Recomendador de Librerías NPM y Maven con 3 IAs

Plataforma Full-Stack compuesta por un backend en **Spring Boot (`rackFindrBack`)** y un frontend en **Angular (`rackFindrFront`)** que recomienda librerías y dependencias para **NPM (Angular)** y **Maven (Spring Boot)** consultando simultáneamente **3 Inteligencias Artificiales** (Gemini, OpenAI y Llama).

---

## Estructura del Repositorio

- **[`rackFindrBack/`](./rackFindrBack/README.md):** API REST en Java 21 + Spring Boot 4.1.3 empaquetada como `.war` para Apache Tomcat, con autenticación JWT, cifrado `AES/GCM/NoPadding`, JavaMail SMTP, auditoría geolocalizada por IP, documentación Swagger/OpenAPI y exportación de reportes en PDF con OpenPDF.
- **[`rackFindrFront/`](./rackFindrFront/README.md):** Aplicación web SPA en Angular con buscador comparativo de las 3 IAs, flujo completo de autenticación y confirmación por correo, historial de consultas y panel administrativo con gráficas y exportación PDF.

---

## Credenciales Iniciales Precargadas

- **Administrador:** `admin` / `1234567890` (`ROLE_ADMIN`)
- **Usuario Estándar:** `normaluser` / `1234567890` (`ROLE_USER`)
