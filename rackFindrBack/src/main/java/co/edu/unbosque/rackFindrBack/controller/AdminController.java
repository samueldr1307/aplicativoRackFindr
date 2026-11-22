package co.edu.unbosque.rackFindrBack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.dto.EstadisticaDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import co.edu.unbosque.rackFindrBack.service.SolicitudService;
import co.edu.unbosque.rackFindrBack.service.UserService;
import co.edu.unbosque.rackFindrBack.util.PdfGeneratorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para el panel administrativo.
 * Expone endpoints protegidos por el rol {@code ADMIN} para consultar estadísticas de uso
 * de la plataforma y exportar reportes administrativos, de usuarios y de estadísticas en formato PDF.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(originPatterns = "*")
@Transactional
@Tag(name = "Panel Administrativo", description = "Endpoints administrativos para estadísticas y reportes en PDF")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private SolicitudService solicitudService;

	@Autowired
	private AuditoriaSesionService auditoriaService;

	public AdminController() {
	}

	@Operation(summary = "Obtener estadísticas de uso para gráficas")
	@GetMapping("/estadisticas")
	public ResponseEntity<EstadisticaDTO> obtenerEstadisticas() {
		return ResponseEntity.ok(auditoriaService.generarEstadisticasPlataforma());
	}

	@Operation(summary = "Exportar estadísticas de uso en PDF")
	@GetMapping(value = "/estadisticas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarEstadisticasPdf(HttpServletRequest request) {
		EstadisticaDTO stats = auditoriaService.generarEstadisticasPlataforma();
		byte[] pdfBytes = PdfGeneratorUtil.generarEstadisticasPdf(stats);

		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		auditoriaService.registrarAccion(obtenerUsuarioAutenticado(), "ADMIN", "EXPORTAR_ESTADISTICAS_PDF",
				"Descarga de estadisticas de uso en PDF", ip, null, true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "estadisticas_uso_rackfindr.pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Descargar reporte de usuarios en PDF")
	@GetMapping(value = "/reporte/usuarios/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarReporteUsuariosPdf(HttpServletRequest request) {
		List<UserDTO> usuarios = userService.getAll();
		byte[] pdfBytes = PdfGeneratorUtil.generarReporteUsuariosPdf(usuarios);

		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		auditoriaService.registrarAccion(obtenerUsuarioAutenticado(), "ADMIN", "EXPORTAR_USUARIOS_PDF",
				"Descarga de reporte de usuarios en PDF", ip, null, true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "reporte_usuarios_rackfindr.pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Exportar reporte administrativo completo en PDF")
	@GetMapping(value = "/reporte/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarReporteAdministrativoPdf(HttpServletRequest request) {
		EstadisticaDTO stats = auditoriaService.generarEstadisticasPlataforma();
		List<UserDTO> usuarios = userService.getAll();
		List<SolicitudDTO> solicitudes = solicitudService.getAll();
		List<AuditoriaSesionDTO> auditorias = auditoriaService.getAll();

		byte[] pdfBytes = PdfGeneratorUtil.generarReporteAdminPdf(stats, usuarios, solicitudes, auditorias);

		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		auditoriaService.registrarAccion(obtenerUsuarioAutenticado(), "ADMIN", "EXPORTAR_REPORTE_GENERAL_PDF",
				"Descarga de reporte administrativo general en PDF", ip, null, true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "reporte_administrativo_rackfindr.pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	private String obtenerUsuarioAutenticado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			return auth.getName();
		}
		return "admin";
	}

}
