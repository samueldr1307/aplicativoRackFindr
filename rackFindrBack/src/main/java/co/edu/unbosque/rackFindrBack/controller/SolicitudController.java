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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.ResultadoDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import co.edu.unbosque.rackFindrBack.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/solicitud")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*" })
@Transactional
@Tag(name = "Solicitudes e IA", description = "Endpoints para consultar las 3 IAs públicas (NPM y Maven), historial y exportación PDF")
@SecurityRequirement(name = "bearerAuth")
public class SolicitudController {

	@Autowired
	private SolicitudService solicitudServ;

	@Autowired
	private AuditoriaSesionService auditoriaServ;

	public SolicitudController() {
	}

	@Operation(summary = "Buscar librerías de NPM o Maven usando las 3 IAs públicas")
	@PostMapping(path = "/buscar", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ResultadoDTO>> buscarLibrerias(@RequestBody SolicitudDTO request,
			HttpServletRequest httpRequest) {
		if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		String usuario = obtenerUsuarioAutenticado(request.getUsername());
		request.setUsername(usuario);
		SolicitudDTO procesada = solicitudServ.procesarYGuardarSolicitud(request);

		String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
		auditoriaServ.registrarAccion(usuario, "USER", "BUSQUEDA_LIBRERIAS",
				"Ecosistema: " + procesada.getEcosistema() + " | Prompt: " + procesada.getPrompt(), ip, null, true);

		return ResponseEntity.ok(procesada.getResultados());
	}

	@Operation(summary = "Recomendar librerías por parámetros y retornar la solicitud completa con las 3 respuestas")
	@PostMapping(path = "/recomendar")
	public ResponseEntity<SolicitudDTO> recomendarPorParametros(
			@RequestParam(defaultValue = "npm") String ecosistema,
			@RequestParam String prompt,
			HttpServletRequest httpRequest) {
		String username = obtenerUsuarioAutenticado(null);
		SolicitudDTO dto = new SolicitudDTO(ecosistema, prompt, username);
		SolicitudDTO procesada = solicitudServ.procesarYGuardarSolicitud(dto);

		String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
		auditoriaServ.registrarAccion(username, "USER", "BUSQUEDA_LIBRERIAS",
				"Ecosistema: " + ecosistema + " | Prompt: " + prompt, ip, null, true);

		return new ResponseEntity<>(procesada, HttpStatus.CREATED);
	}

	@Operation(summary = "Crear solicitud (JSON)")
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SolicitudDTO> createWithJson(@RequestBody SolicitudDTO newSolicitud,
			HttpServletRequest httpRequest) {
		if (newSolicitud.getPrompt() == null || newSolicitud.getPrompt().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		String username = obtenerUsuarioAutenticado(newSolicitud.getUsername());
		newSolicitud.setUsername(username);
		SolicitudDTO saved = solicitudServ.procesarYGuardarSolicitud(newSolicitud);

		String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
		auditoriaServ.registrarAccion(username, "USER", "BUSQUEDA_LIBRERIAS",
				"Solicitud ID: " + saved.getId() + " (" + saved.getEcosistema() + ")", ip, null, true);

		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}

	@Operation(summary = "Obtener historial del usuario autenticado")
	@GetMapping("/mis-solicitudes")
	public ResponseEntity<List<SolicitudDTO>> getMisSolicitudes() {
		String username = obtenerUsuarioAutenticado(null);
		return ResponseEntity.ok(solicitudServ.getByUsername(username));
	}

	@Operation(summary = "Obtener historial por nombre de usuario")
	@GetMapping("/historial/{username}")
	public ResponseEntity<List<SolicitudDTO>> getHistorialPorUsuario(@PathVariable String username) {
		return ResponseEntity.ok(solicitudServ.getByUsername(username));
	}

	@Operation(summary = "Descargar una búsqueda individual en PDF con su solicitud y las tres respuestas")
	@GetMapping(value = "/exportar-pdf/id/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarSolicitudPorIdPdf(@PathVariable Long id, HttpServletRequest httpRequest) {
		String username = obtenerUsuarioAutenticado(null);
		byte[] pdfBytes = solicitudServ.exportarSolicitudIndividualPdf(id);

		String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
		auditoriaServ.registrarAccion(username, "USER", "DESCARGA_PDF_BUSQUEDA",
				"Descarga PDF de solicitud ID: " + id, ip, null, true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "busqueda_" + id + ".pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Exportar en PDF todo el historial del usuario autenticado")
	@GetMapping(value = "/exportar-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarMiHistorialPdf(HttpServletRequest httpRequest) {
		String username = obtenerUsuarioAutenticado(null);
		byte[] pdfBytes = solicitudServ.exportarHistorialUsuarioPdf(username);

		String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
		auditoriaServ.registrarAccion(username, "USER", "DESCARGA_PDF_HISTORIAL",
				"Descarga PDF historial de " + username, ip, null, true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "historial_solicitudes_" + username + ".pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Exportar en PDF el historial de un usuario específico")
	@GetMapping(value = "/exportar-pdf/{username}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportarHistorialUsuarioPdf(@PathVariable String username) {
		byte[] pdfBytes = solicitudServ.exportarHistorialUsuarioPdf(username);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "historial_solicitudes_" + username + ".pdf");
		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<SolicitudDTO>> getAll() {
		List<SolicitudDTO> list = solicitudServ.getAll();
		if (list.isEmpty()) {
			return new ResponseEntity<>(list, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getbyid/{id}")
	public ResponseEntity<SolicitudDTO> getById(@PathVariable Long id) {
		SolicitudDTO found = solicitudServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(new SolicitudDTO(), HttpStatus.NOT_FOUND);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countAll() {
		return new ResponseEntity<>(solicitudServ.count(), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = solicitudServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Solicitud eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
	}

	private String obtenerUsuarioAutenticado(String fallback) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			return auth.getName();
		}
		return (fallback != null && !fallback.isEmpty()) ? fallback : "anonimo";
	}

}
