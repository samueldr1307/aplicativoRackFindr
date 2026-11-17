package co.edu.unbosque.rackFindrBack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.AuditoriaSesion;

public interface AuditoriaSesionRepository extends JpaRepository<AuditoriaSesion, Long>{
	
	public List<AuditoriaSesion> findByUsernameOrderByFechaDesc(String username);

	public List<AuditoriaSesion> findAllByOrderByFechaDesc();

	public long countByExitoso(boolean exitoso);


}
