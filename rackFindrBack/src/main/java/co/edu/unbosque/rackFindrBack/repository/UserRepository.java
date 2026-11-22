package co.edu.unbosque.rackFindrBack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.User;

/**
 * Repositorio JPA para la entidad {@link User} que gestiona las cuentas de usuario en la base de datos.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Busca un usuario por su nombre de usuario único.
	 *
	 * @param username nombre de usuario a buscar
	 * @return {@link Optional} con el usuario encontrado o vacío si no existe
	 */
	public Optional<User> findByUsername(String username);

	/**
	 * Busca un usuario por su dirección de correo electrónico (cifrada o en texto plano).
	 *
	 * @param email correo electrónico a buscar
	 * @return {@link Optional} con el usuario encontrado o vacío si no existe
	 */
	public Optional<User> findByEmail(String email);

	/**
	 * Elimina un usuario de la base de datos a partir de su nombre de usuario.
	 *
	 * @param username nombre de usuario a eliminar
	 */
	public void deleteByUsername(String username);

}
