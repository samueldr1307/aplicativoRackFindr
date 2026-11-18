package co.edu.unbosque.rackFindrBack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auditoria")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*" })
@Transactional
@Tag(name = "Auditoría de Sesiones", description = "Endpoints para consultar auditoría de inicios de sesión (fechas, IP y coordenadas)")
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

	@Autowired
	private AuditoriaSesionService auditoriaServ;

	public AuditoriaController() {
	}

	@Operation(summary = "Obtener todas las auditorías de inicio de sesión")
	@GetMapping("/getall")
	public ResponseEntity<List<AuditoriaSesionDTO>> getAll() {
		List<AuditoriaSesionDTO> list = auditoriaServ.getAll();
		if (list.isEmpty()) {
			return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
	}

	@Operation(summary = "Obtener auditorías por usuario")
	@GetMapping("/usuario/{username}")
	public ResponseEntity<List<AuditoriaSesionDTO>> getByUsername(@PathVariable String username) {
		return ResponseEntity.ok(auditoriaServ.getByUsername(username));
	}

	@Operation(summary = "Obtener auditorías del usuario autenticado")
	@GetMapping("/mis-auditorias")
	public ResponseEntity<List<AuditoriaSesionDTO>> getMisAuditorias() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (auth != null) ? auth.getName() : "";
		return ResponseEntity.ok(auditoriaServ.getByUsername(username));
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countAll() {
		return new ResponseEntity<>(auditoriaServ.count(), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = auditoriaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Auditoría eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
	}

}
