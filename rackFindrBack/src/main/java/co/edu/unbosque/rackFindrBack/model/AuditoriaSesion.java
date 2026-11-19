package co.edu.unbosque.rackFindrBack.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria_sesion")
public class AuditoriaSesion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	private String rol;
	private String accion;
	private LocalDateTime fecha;

	@Column(columnDefinition = "TEXT")
	private String ip;

	@Column(columnDefinition = "TEXT")
	private String coordenadas;

	private String ciudad;
	private String pais;
	private boolean exitoso;

	@Column(columnDefinition = "TEXT")
	private String detalle;

	public AuditoriaSesion() {
		this.fecha = LocalDateTime.now();
	}

	public AuditoriaSesion(String username, String rol, String accion, LocalDateTime fecha, String ip,
			String coordenadas, String ciudad, String pais, boolean exitoso, String detalle) {
		this.username = username;
		this.rol = rol;
		this.accion = accion;
		this.fecha = fecha != null ? fecha : LocalDateTime.now();
		this.ip = ip;
		this.coordenadas = coordenadas;
		this.ciudad = ciudad;
		this.pais = pais;
		this.exitoso = exitoso;
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

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, fecha);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AuditoriaSesion other = (AuditoriaSesion) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "AuditoriaSesion [id=" + id + ", username=" + username + ", accion=" + accion + ", fecha=" + fecha
				+ ", ip=" + ip + ", coordenadas=" + coordenadas + ", exitoso=" + exitoso + "]";
	}

}
