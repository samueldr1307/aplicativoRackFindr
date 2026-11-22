package co.edu.unbosque.rackFindrBack.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración de OpenAPI 3 / Swagger UI para la documentación interactiva de los endpoints REST.
 * Define metadatos de la API, esquema de autenticación Bearer JWT y respuestas HTTP estándar.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Constructor por defecto de la configuración OpenAPI.
	 */
	public OpenApiConfig() {
	}

	/**
	 * Construye y personaliza la especificación {@link OpenAPI} de la plataforma RackFindr.
	 *
	 * @return objeto {@link OpenAPI} con la información general y esquemas de seguridad JWT
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		String mainDescription = "<h2>API REST RackFindr</h2>"
				+ "<p>Recomendador de librerías de NPM y Maven asistido por 3 Inteligencias Artificiales con seguridad JWT, JavaMail, auditoría y exportación PDF.</p>";

		String securityDescription = "Autenticación mediante JWT (JSON Web Token). Ingresa el token obtenido en <code>/auth/login</code>.";

		Info info = new Info()
				.title("API de RackFindr")
				.version("1.0")
				.description(mainDescription)
				.contact(new Contact().name("Equipo de Desarrollo").email("soporte@rackfindr.com"))
				.license(new License().name("Licencia MIT").url("https://opensource.org/licenses/MIT"));

		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description(securityDescription);

		return new OpenAPI()
				.info(info)
				.components(new Components()
						.addSecuritySchemes("bearerAuth", securityScheme)
						.addResponses("UnauthorizedError", new ApiResponse()
								.description("No autenticado - Token JWT inválido o expirado")
								.content(new Content().addMediaType("application/json",
										new MediaType().addExamples("error", new Example().value(
												"{\"error\": \"No autorizado\", \"mensaje\": \"Token inválido o expirado\"}")))))
						.addResponses("ForbiddenError", new ApiResponse()
								.description("Acceso prohibido - No tienes permisos suficientes")
								.content(new Content().addMediaType("application/json",
										new MediaType().addExamples("error", new Example().value(
												"{\"error\": \"Acceso prohibido\", \"mensaje\": \"No tienes permisos para esta operación\"}")))))
						.addResponses("NotFoundError", new ApiResponse()
								.description("Recurso no encontrado")
								.content(new Content().addMediaType("application/json",
										new MediaType().addExamples("error", new Example().value(
												"{\"error\": \"No encontrado\", \"mensaje\": \"El recurso solicitado no existe\"}"))))));
	}

}
