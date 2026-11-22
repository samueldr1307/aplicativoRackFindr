package co.edu.unbosque.rackFindrBack.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración central de Spring Security para la aplicación.
 * Define las políticas CORS, deshabilita CSRF para arquitectura REST stateless,
 * establece las reglas de autorización por rol ({@code USER}, {@code ADMIN}) y registra el filtro JWT.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;

	/**
	 * Construye la configuración de seguridad inyectando el filtro JWT y el servicio de usuarios.
	 *
	 * @param jwtAuthFilter      filtro de autenticación JWT por petición
	 * @param userDetailsService servicio de carga de usuarios
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad HTTP ({@link SecurityFilterChain}),
	 * los permisos por ruta y la gestión de sesiones sin estado ({@code STATELESS}).
	 *
	 * @param http objeto {@link HttpSecurity} para construir las reglas de seguridad
	 * @return cadena de filtros {@link SecurityFilterChain} construida
	 * @throws Exception si ocurre un error al configurar la seguridad HTTP
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/user/getall", "/user/count", "/user/exists/**", "/user/getbyid/**")
						.hasAnyRole("USER", "ADMIN")
						.requestMatchers("/user/**").hasRole("ADMIN")
						.requestMatchers("/solicitud/getall", "/solicitud/deletebyid/**").hasRole("ADMIN")
						.requestMatchers("/solicitud/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/auditoria/mis-auditorias").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/auditoria/**").hasRole("ADMIN")
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(
				List.of("http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(
				List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
		configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
