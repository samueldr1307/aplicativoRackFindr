package co.edu.unbosque.rackFindrBack.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.rackFindrBack.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Componente utilitario encargado de la generación, firma, extracción de claims y validación
 * de tokens JWT (JSON Web Token) utilizando el algoritmo HMAC-SHA256.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Component
public class JwtUtil {

	/** Tiempo de validez del token JWT en milisegundos (24 horas). */
	private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

	@Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}")
	private String secret;

	/**
	 * Obtiene la clave criptográfica HMAC a partir del secreto configurado.
	 *
	 * @return clave {@link Key} para firmar y verificar tokens
	 */
	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extrae el nombre de usuario ({@code subject}) almacenado en el token JWT.
	 *
	 * @param token token JWT firmado
	 * @return nombre de usuario contenido en el token
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae la fecha de expiración del token JWT.
	 *
	 * @param token token JWT firmado
	 * @return fecha de expiración {@link Date}
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrae el rol del usuario almacenado en los claims personalizados del token JWT.
	 *
	 * @param token token JWT firmado
	 * @return nombre del rol del usuario
	 */
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	/**
	 * Extrae un claim específico del token aplicando la función extractora indicada.
	 *
	 * @param <T>            tipo de dato del claim a retornar
	 * @param token          token JWT firmado
	 * @param claimsResolver función que transforma los {@link Claims} en el valor deseado
	 * @return valor del claim extraído
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Genera un nuevo token JWT firmado para el usuario autenticado incluyendo sus autoridades y rol.
	 *
	 * @param userDetails detalles del usuario autenticado
	 * @return cadena con el token JWT compacto y firmado
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", userDetails.getAuthorities());

		if (userDetails instanceof User) {
			User user = (User) userDetails;
			claims.put("role", user.getRole().name());
		}

		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Valida que el token JWT pertenezca al usuario indicado y no haya expirado.
	 *
	 * @param token       token JWT a validar
	 * @param userDetails detalles del usuario contra los cuales comparar
	 * @return {@code true} si el token es válido y vigente, {@code false} en caso contrario
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
