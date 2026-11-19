package co.edu.unbosque.rackFindrBack.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.dto.EstadisticaDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.model.AuditoriaSesion;
import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.model.User.Role;
import co.edu.unbosque.rackFindrBack.repository.AuditoriaSesionRepository;
import co.edu.unbosque.rackFindrBack.repository.SolicitudRepository;
import co.edu.unbosque.rackFindrBack.repository.UserRepository;
import co.edu.unbosque.rackFindrBack.util.AESUtil;

@Service
public class AuditoriaSesionService implements CRUDOperation<AuditoriaSesionDTO> {

	@Autowired
	private AuditoriaSesionRepository auditoriaRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SolicitudRepository solicitudRepo;

	@Autowired
	private SolicitudService solicitudService;

	public AuditoriaSesionService() {
	}

	@Override
	public long count() {
		return auditoriaRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return auditoriaRepo.existsById(id) ? true : false;
	}

	@Override
	public int create(AuditoriaSesionDTO data) {
		if (data == null || data.getUsername() == null) {
			return 1;
		}
		AuditoriaSesion entity = new AuditoriaSesion();
		entity.setUsername(data.getUsername());
		entity.setRol(data.getRol() != null ? data.getRol() : "USER");
		entity.setAccion(data.getAccion() != null ? data.getAccion() : "ACCION_SISTEMA");
		entity.setFecha(data.getFecha() != null ? data.getFecha() : LocalDateTime.now());
		entity.setIp(AESUtil.encrypt(data.getIp() != null ? data.getIp() : "127.0.0.1"));
		entity.setCoordenadas(
				AESUtil.encrypt(data.getCoordenadas() != null ? data.getCoordenadas() : "4.6097, -74.0817"));
		entity.setCiudad(data.getCiudad() != null ? data.getCiudad() : "Bogota");
		entity.setPais(data.getPais() != null ? data.getPais() : "Colombia");
		entity.setExitoso(data.isExitoso());
		entity.setDetalle(data.getDetalle());

		auditoriaRepo.save(entity);
		return 0;
	}

	public AuditoriaSesionDTO registrarAuditoriaLogin(String username, String rol, String ipCliente,
			String coordenadasCliente, boolean exitoso, String detalle) {
		String accion = exitoso ? "LOGIN_EXITOSO" : "LOGIN_FALLIDO";
		return registrarAccion(username, rol, accion, detalle, ipCliente, coordenadasCliente, exitoso);
	}

	public AuditoriaSesionDTO registrarAccion(String username, String rol, String accion, String detalle,
			String ipCliente, String coordenadasCliente, boolean exitoso) {
		Map<String, String> geo = ExternalHTTPRequestHandler.obtenerGeolocalizacionIp(ipCliente);

		String finalIp = (ipCliente != null && !ipCliente.isEmpty()) ? ipCliente : geo.get("ip");
		String finalCoord = (coordenadasCliente != null && !coordenadasCliente.isEmpty()) ? coordenadasCliente
				: geo.get("coordenadas");
		String ciudad = geo.get("ciudad");
		String pais = geo.get("pais");

		AuditoriaSesion entity = new AuditoriaSesion(
				username != null ? username : "anonimo",
				rol != null ? rol : "USER",
				accion,
				LocalDateTime.now(),
				AESUtil.encrypt(finalIp),
				AESUtil.encrypt(finalCoord),
				ciudad,
				pais,
				exitoso,
				detalle);

		AuditoriaSesion saved = auditoriaRepo.save(entity);
		return mapEntityToDto(saved);
	}

	@Override
	public List<AuditoriaSesionDTO> getAll() {
		List<AuditoriaSesion> list = auditoriaRepo.findAllByOrderByFechaDesc();
		List<AuditoriaSesionDTO> dtoList = new ArrayList<>();
		for (AuditoriaSesion a : list) {
			dtoList.add(mapEntityToDto(a));
		}
		return dtoList;
	}

	public List<AuditoriaSesionDTO> getByUsername(String username) {
		List<AuditoriaSesion> list = auditoriaRepo.findByUsernameOrderByFechaDesc(username);
		List<AuditoriaSesionDTO> dtoList = new ArrayList<>();
		for (AuditoriaSesion a : list) {
			dtoList.add(mapEntityToDto(a));
		}
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<AuditoriaSesion> found = auditoriaRepo.findById(id);
		if (found.isPresent()) {
			auditoriaRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, AuditoriaSesionDTO newData) {
		Optional<AuditoriaSesion> found = auditoriaRepo.findById(id);
		if (found.isPresent()) {
			AuditoriaSesion entity = found.get();
			entity.setDetalle(newData.getDetalle());
			auditoriaRepo.save(entity);
			return 0;
		} else {
			return 1;
		}
	}

	public EstadisticaDTO generarEstadisticasPlataforma() {
		EstadisticaDTO stats = new EstadisticaDTO();

		List<User> usuarios = userRepo.findAll();
		long admins = 0;
		long activos = 0;
		for (User u : usuarios) {
			if (u.getRole() == Role.ADMIN) {
				admins++;
			}
			if (u.isEnabled()) {
				activos++;
			}
		}
		stats.setTotalUsuarios(usuarios.size());
		stats.setTotalAdministradores(admins);
		stats.setTotalUsuariosEstandar(usuarios.size() - admins);
		stats.setUsuariosActivos(activos);
		stats.setUsuariosInactivos(usuarios.size() - activos);

		long totalSol = solicitudRepo.count();
		long npmCount = solicitudRepo.countByEcosistemaIgnoreCase("npm");
		long mavenCount = solicitudRepo.countByEcosistemaIgnoreCase("maven");
		stats.setTotalSolicitudes(totalSol);
		stats.setSolicitudesNpm(npmCount);
		stats.setSolicitudesMaven(mavenCount);

		List<AuditoriaSesionDTO> todasAuditorias = getAll();
		long loginsExitosos = 0;
		long loginsFallidos = 0;
		Map<String, Long> porAccion = new LinkedHashMap<>();

		for (AuditoriaSesionDTO a : todasAuditorias) {
			String acc = a.getAccion() != null ? a.getAccion() : "SESION";
			Long actualAcc = porAccion.get(acc);
			porAccion.put(acc, actualAcc == null ? 1L : actualAcc + 1L);

			if ("LOGIN_EXITOSO".equals(acc)) {
				loginsExitosos++;
			} else if ("LOGIN_FALLIDO".equals(acc)) {
				loginsFallidos++;
			}
		}

		stats.setTotalIniciosSesion(loginsExitosos + loginsFallidos);
		stats.setIniciosSesionExitosos(loginsExitosos);
		stats.setIniciosSesionFallidos(loginsFallidos);
		stats.setAccionesAuditoria(porAccion);

		Map<String, Long> porUsuario = new LinkedHashMap<>();
		for (SolicitudDTO s : solicitudService.getAll()) {
			String u = s.getUsername() != null ? s.getUsername() : "anonimo";
			Long actual = porUsuario.get(u);
			porUsuario.put(u, actual == null ? 1L : actual + 1L);
		}
		stats.setSolicitudesPorUsuario(porUsuario);

		Map<String, Long> porIa = new LinkedHashMap<>();
		porIa.put("Gemini", totalSol);
		porIa.put("OpenAI", totalSol);
		porIa.put("Llama", totalSol);
		stats.setSolicitudesPorIa(porIa);

		stats.setUltimasAuditorias(todasAuditorias);

		return stats;
	}

	private AuditoriaSesionDTO mapEntityToDto(AuditoriaSesion entity) {
		AuditoriaSesionDTO dto = new AuditoriaSesionDTO();
		dto.setId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setRol(entity.getRol());
		dto.setAccion(entity.getAccion());
		dto.setFecha(entity.getFecha());
		dto.setIp(AESUtil.decrypt(entity.getIp()));
		dto.setCoordenadas(AESUtil.decrypt(entity.getCoordenadas()));
		dto.setCiudad(entity.getCiudad());
		dto.setPais(entity.getPais());
		dto.setExitoso(entity.isExitoso());
		dto.setDetalle(entity.getDetalle());
		return dto;
	}

}
