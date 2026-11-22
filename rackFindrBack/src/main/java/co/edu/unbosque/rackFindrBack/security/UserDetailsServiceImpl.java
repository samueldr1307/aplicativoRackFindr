package co.edu.unbosque.rackFindrBack.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.rackFindrBack.repository.UserRepository;

/**
 * Implementación personalizada de {@link UserDetailsService} para integrar
 * el repositorio de usuarios de RackFindr con el motor de autenticación de Spring Security.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * Construye el servicio inyectando el repositorio de usuarios.
	 *
	 * @param userRepository repositorio JPA de usuarios
	 */
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Busca y carga los detalles de un usuario a partir de su nombre de usuario.
	 *
	 * @param username nombre de usuario a consultar
	 * @return objeto {@link UserDetails} correspondiente al usuario encontrado
	 * @throws UsernameNotFoundException si no existe ningún usuario con el nombre indicado
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}

}

