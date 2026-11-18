package co.edu.unbosque.rackFindrBack.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.dto.EstadisticaDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.repository.SolicitudRepository;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import co.edu.unbosque.rackFindrBack.service.SolicitudService;
import co.edu.unbosque.rackFindrBack.service.UserService;
import co.edu.unbosque.rackFindrBack.util.PdfGeneratorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*" })
@Transactional
@Tag(name = "Panel Administrativo", description = "Endpoints administrativos para eliminar usuarios, estadísticas, reportes y toda la información")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private SolicitudService solicitudService;

	@Autowired
	private SolicitudRepository solicitudRepo;

	@Autowired
	private AuditoriaSesionService auditoriaService;

	public AdminController() {
	}

	@Operation(summary = "Obtener estadísticas de la plataforma")
	@GetMapping("/estadisticas")
	public ResponseEntity<EstadisticaDTO> obtenerEstadisticas() {
		return ResponseEntity.ok(auditoriaService.generarEstadisticasPlataforma());
	}

	@Operation(summary = "Obtener toda la información consolidada del sistema")
	@GetMapping("/toda-la-informacion")
	public ResponseEntity<Map<String, Object>> obtenerTodaLaInformacion() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("estadisticas", auditoriaService.generarEstadisticasPlataforma());
		data.put("usuarios", userService.getAll());
		data.put("solicitudes", solicitudService.getAll());
		data.put("auditorias", auditoriaService.getAll());
		return ResponseEntity.ok(data);
	}

	@Operation(summary = "Listar todos los usuarios")
	@GetMapping("/usuarios")
	public ResponseEntity<List<UserDTO>> listarUsuarios() {
		return ResponseEntity.ok(userService.getAll());
	}

	@Operation(summary = "Eliminar usuario por ID")
	@DeleteMapping("/usuarios/{id}")
	public ResponseEntity<String> eliminarUsuarioPorId(@PathVariable Long id) {
		UserDTO user = userService.getById(id);
		if (user == null) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}
		solicitudRepo.deleteByUsername(user.getUsername());
		int status = userService.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Error al eliminar", HttpStatus.BAD_REQUEST);
	}

	@Operation(summary = "Eliminar usuario por username")
	@DeleteMapping("/usuarios/username/{username}")
	public ResponseEntity<String> eliminarUsuarioPorUsername(@PathVariable String username) {
		solicitudRepo.deleteByUsername(username);
		int status = userService.deleteByUsername(username);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Listar todas las solicitudes")
	@GetMapping("/solicitudes")
	public ResponseEntity<List<SolicitudDTO>> listarTodasLasSolicitudes() {
		return ResponseEntity.ok(solicitudService.getAll());
	}

	@Operation(summary = "Listar todas las auditorías de inicio de sesión")
	@GetMapping("/auditorias")
	public ResponseEntity<List<AuditoriaSesionDTO>> listarTodasLasAuditorias() {
		return ResponseEntity.ok(auditoriaService.getAll());
	}

	@Operation(summary = "Exportar reporte administrativo completo en PDF")
	@GetMapping(value = "/reporte/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarReporteAdministrativoPdf() {
		EstadisticaDTO stats = auditoriaService.generarEstadisticasPlataforma();
		List<UserDTO> usuarios = userService.getAll();
		List<SolicitudDTO> solicitudes = solicitudService.getAll();
		List<AuditoriaSesionDTO> auditorias = auditoriaService.getAll();

		byte[] pdfBytes = PdfGeneratorUtil.generarReporteAdminPdf(stats, usuarios, solicitudes, auditorias);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "reporte_administrativo_rackfindr.pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

}
