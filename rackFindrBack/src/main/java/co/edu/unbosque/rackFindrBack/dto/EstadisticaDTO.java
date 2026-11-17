package co.edu.unbosque.rackFindrBack.dto;

import java.util.List;
import java.util.Map;

public class EstadisticaDTO {
	

	private long totalUsuarios;
	private long totalAdministradores;
	private long totalUsuariosEstandar;
	private long totalSolicitudes;
	private long solicitudesNpm;
	private long solicitudesMaven;
	private long totalIniciosSesion;
	private long iniciosSesionExitosos;
	private long iniciosSesionFallidos;
	private Map<String, Long> solicitudesPorUsuario;
	private Map<String, Long> solicitudesPorIa;
	private List<AuditoriaSesionDTO> ultimasAuditorias;

	public EstadisticaDTO() {
	}

	public long getTotalUsuarios() {
		return totalUsuarios;
	}

	public void setTotalUsuarios(long totalUsuarios) {
		this.totalUsuarios = totalUsuarios;
	}

	public long getTotalAdministradores() {
		return totalAdministradores;
	}

	public void setTotalAdministradores(long totalAdministradores) {
		this.totalAdministradores = totalAdministradores;
	}

	public long getTotalUsuariosEstandar() {
		return totalUsuariosEstandar;
	}

	public void setTotalUsuariosEstandar(long totalUsuariosEstandar) {
		this.totalUsuariosEstandar = totalUsuariosEstandar;
	}

	public long getTotalSolicitudes() {
		return totalSolicitudes;
	}

	public void setTotalSolicitudes(long totalSolicitudes) {
		this.totalSolicitudes = totalSolicitudes;
	}

	public long getSolicitudesNpm() {
		return solicitudesNpm;
	}

	public void setSolicitudesNpm(long solicitudesNpm) {
		this.solicitudesNpm = solicitudesNpm;
	}

	public long getSolicitudesMaven() {
		return solicitudesMaven;
	}

	public void setSolicitudesMaven(long solicitudesMaven) {
		this.solicitudesMaven = solicitudesMaven;
	}

	public long getTotalIniciosSesion() {
		return totalIniciosSesion;
	}

	public void setTotalIniciosSesion(long totalIniciosSesion) {
		this.totalIniciosSesion = totalIniciosSesion;
	}

	public long getIniciosSesionExitosos() {
		return iniciosSesionExitosos;
	}

	public void setIniciosSesionExitosos(long iniciosSesionExitosos) {
		this.iniciosSesionExitosos = iniciosSesionExitosos;
	}

	public long getIniciosSesionFallidos() {
		return iniciosSesionFallidos;
	}

	public void setIniciosSesionFallidos(long iniciosSesionFallidos) {
		this.iniciosSesionFallidos = iniciosSesionFallidos;
	}

	public Map<String, Long> getSolicitudesPorUsuario() {
		return solicitudesPorUsuario;
	}

	public void setSolicitudesPorUsuario(Map<String, Long> solicitudesPorUsuario) {
		this.solicitudesPorUsuario = solicitudesPorUsuario;
	}

	public Map<String, Long> getSolicitudesPorIa() {
		return solicitudesPorIa;
	}

	public void setSolicitudesPorIa(Map<String, Long> solicitudesPorIa) {
		this.solicitudesPorIa = solicitudesPorIa;
	}

	public List<AuditoriaSesionDTO> getUltimasAuditorias() {
		return ultimasAuditorias;
	}

	public void setUltimasAuditorias(List<AuditoriaSesionDTO> ultimasAuditorias) {
		this.ultimasAuditorias = ultimasAuditorias;
	}

	

}
