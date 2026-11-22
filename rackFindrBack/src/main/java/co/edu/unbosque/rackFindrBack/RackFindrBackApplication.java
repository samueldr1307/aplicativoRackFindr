package co.edu.unbosque.rackFindrBack;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal de arranque para la aplicación backend de RackFindr.
 * Inicializa el contexto de Spring Boot y registra los beans globales de configuración.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@SpringBootApplication
public class RackFindrBackApplication {

	/**
	 * Constructor por defecto de la aplicación.
	 */
	public RackFindrBackApplication() {
	}

	/**
	 * Punto de entrada principal que inicia la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos pasados al iniciar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(RackFindrBackApplication.class, args);
	}

	/**
	 * Crea y expone un bean singleton de {@link ModelMapper} para el mapeo entre entidades y DTOs.
	 *
	 * @return instancia configurada de {@link ModelMapper}
	 */
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
