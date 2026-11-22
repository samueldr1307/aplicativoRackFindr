package co.edu.unbosque.rackFindrBack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.Solicitud;

/**
 * Repositorio JPA para las operaciones de acceso a datos de la entidad {@link Solicitud}.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

	/**
	 * Obtiene el historial de solicitudes realizadas por un usuario específico, ordenado por fecha descendente.
	 *
	 * @param username nombre de usuario
	 * @return lista de solicitudes del usuario
	 */
	public List<Solicitud> findByUsernameOrderByFechaDesc(String username);

	/**
	 * Obtiene el historial de solicitudes asociadas al identificador de un usuario, ordenado por fecha descendente.
	 *
	 * @param userId identificador único del usuario
	 * @return lista de solicitudes del usuario
	 */
	public List<Solicitud> findByUserIdOrderByFechaDesc(Long userId);

	/**
	 * Filtra las solicitudes según el ecosistema tecnológico ({@code npm} o {@code maven}) sin distinguir mayúsculas.
	 *
	 * @param ecosistema nombre del ecosistema a buscar
	 * @return lista de solicitudes correspondientes al ecosistema
	 */
	public List<Solicitud> findByEcosistemaIgnoreCase(String ecosistema);

	/**
	 * Cuenta el número total de solicitudes registradas para un ecosistema determinado.
	 *
	 * @param ecosistema nombre del ecosistema ({@code npm} o {@code maven})
	 * @return cantidad de solicitudes de dicho ecosistema
	 */
	public long countByEcosistemaIgnoreCase(String ecosistema);

	/**
	 * Elimina todas las solicitudes asociadas a un nombre de usuario.
	 *
	 * @param username nombre del usuario cuyas solicitudes serán eliminadas
	 */
	public void deleteByUsername(String username);

}
