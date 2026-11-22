package co.edu.unbosque.rackFindrBack.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro HTTP que intercepta cada petición entrante una única vez para extraer y validar
 * el token JWT del encabezado {@code Authorization: Bearer <token>}.
 * Si el token es válido, establece la autenticación del usuario en el {@link SecurityContextHolder}.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	/**
	 * Construye el filtro de autenticación JWT inyectando sus dependencias.
	 *
	 * @param jwtUtil            utilidad para validación y extracción de claims JWT
	 * @param userDetailsService servicio para cargar los detalles del usuario desde la base de datos
	 */
	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Procesa la solicitud HTTP verificando la presencia y validez del token JWT antes de continuar la cadena de filtros.
	 *
	 * @param request     solicitud HTTP entrante
	 * @param response    respuesta HTTP saliente
	 * @param filterChain cadena de filtros de seguridad
	 * @throws ServletException si ocurre un error en el procesamiento del servlet
	 * @throws IOException      si ocurre un error de entrada/salida
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			try {
				username = jwtUtil.extractUsername(jwt);
			} catch (Exception e) {
				logger.error("Error extracting username from token", e);
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (jwtUtil.validateToken(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		filterChain.doFilter(request, response);
	}

}

