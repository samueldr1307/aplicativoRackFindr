package co.edu.unbosque.rackFindrBack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.AuditoriaSesion;

/**
 * Repositorio JPA para la gestión de persistencia de registros de auditoría ({@link AuditoriaSesion}).
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public interface AuditoriaSesionRepository extends JpaRepository<AuditoriaSesion, Long> {

	/**
	 * Busca los registros de auditoría asociados a un nombre de usuario, ordenados por fecha descendente.
	 *
	 * @param username nombre del usuario a consultar
	 * @return lista de registros de auditoría del usuario
	 */
	public List<AuditoriaSesion> findByUsernameOrderByFechaDesc(String username);

	/**
	 * Obtiene todos los registros de auditoría del sistema ordenados del más reciente al más antiguo.
	 *
	 * @return lista completa de auditorías ordenada por fecha descendente
	 */
	public List<AuditoriaSesion> findAllByOrderByFechaDesc();

	/**
	 * Cuenta la cantidad de eventos de auditoría según si fueron exitosos o fallidos.
	 *
	 * @param exitoso {@code true} para contar eventos exitosos, {@code false} para fallidos
	 * @return número total de registros que coinciden con el estado indicado
	 */
	public long countByExitoso(boolean exitoso);

}
