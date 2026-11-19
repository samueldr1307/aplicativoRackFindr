package co.edu.unbosque.rackFindrBack.dto;

import java.time.LocalDateTime;

public class AuditoriaSesionDTO {

	private Long id;
	private String username;
	private String rol;
	private String accion;
	private LocalDateTime fecha;
	private String ip;
	private String coordenadas;
	private String ciudad;
	private String pais;
	private boolean exitoso;
	private boolean confirmadoCorreo;
	private String detalle;

	public AuditoriaSesionDTO() {
		this.fecha = LocalDateTime.now();
	}

	public AuditoriaSesionDTO(String username, String rol, String accion, LocalDateTime fecha, String ip,
			String coordenadas, String ciudad, String pais, boolean exitoso, boolean confirmadoCorreo, String detalle) {
		this.username = username;
		this.rol = rol;
		this.accion = accion;
		this.fecha = fecha != null ? fecha : LocalDateTime.now();
		this.ip = ip;
		this.coordenadas = coordenadas;
		this.ciudad = ciudad;
		this.pais = pais;
		this.exitoso = exitoso;
		this.confirmadoCorreo = confirmadoCorreo;
		this.detalle = detalle;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public boolean isExitoso() {
		return exitoso;
	}

	public void setExitoso(boolean exitoso) {
		this.exitoso = exitoso;
	}

	public boolean isConfirmadoCorreo() {
		return confirmadoCorreo;
	}

	public void setConfirmadoCorreo(boolean confirmadoCorreo) {
		this.confirmadoCorreo = confirmadoCorreo;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

}
