package co.edu.unbosque.rackFindrBack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud , Long>{
	
	public List<Solicitud> findByUsernameOrderByFechaDesc(String username);

	public List<Solicitud> findByUserIdOrderByFechaDesc(Long userId);

	public List<Solicitud> findByEcosistemaIgnoreCase(String ecosistema);

	public long countByEcosistemaIgnoreCase(String ecosistema);

	public void deleteByUsername(String username);


}
