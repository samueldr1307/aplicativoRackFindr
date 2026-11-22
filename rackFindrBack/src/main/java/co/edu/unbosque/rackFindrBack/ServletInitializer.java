package co.edu.unbosque.rackFindrBack;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador de Servlet que permite desplegar la aplicación empaquetada como archivo WAR
 * dentro de un contenedor de servlets externo como Apache Tomcat.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Constructor por defecto del inicializador de Servlet.
	 */
	public ServletInitializer() {
	}

	/**
	 * Configura el constructor de la aplicación registrando la clase principal {@link RackFindrBackApplication}.
	 *
	 * @param application constructor de la aplicación Spring Boot
	 * @return instancia configurada de {@link SpringApplicationBuilder}
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RackFindrBackApplication.class);
	}

}
