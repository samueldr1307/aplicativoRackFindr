package co.edu.unbosque.rackFindrBack.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una solicitud o búsqueda de librerías en la tabla {@code solicitud}.
 * Almacena el ecosistema consultado ({@code npm} o {@code maven}), el prompt cifrado,
 * la respuesta consolidada de las 3 Inteligencias Artificiales y el usuario que realizó la consulta.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Entity
@Table(name = "solicitud")
public class Solicitud {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime fecha;

	@Column(length = 30)
	private String ecosistema;

	@Column(columnDefinition = "TEXT")
	private String prompt;

	@Column(columnDefinition = "TEXT")
	private String promptBase;

	@Column(columnDefinition = "LONGTEXT")
	private String contenido;

	@Column(length = 120)
	private String iaUsada;

	@Column(length = 100)
	private String username;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public Solicitud() {
		this.fecha = LocalDateTime.now();
	}

	public Solicitud(LocalDateTime fecha, String ecosistema, String prompt, String promptBase, String contenido,
			String iaUsada, String username, User user) {
		this.fecha = fecha != null ? fecha : LocalDateTime.now();
		this.ecosistema = ecosistema;
		this.prompt = prompt;
		this.promptBase = promptBase;
		this.contenido = contenido;
		this.iaUsada = iaUsada;
		this.username = username;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getEcosistema() {
		return ecosistema;
	}

	public void setEcosistema(String ecosistema) {
		this.ecosistema = ecosistema;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPromptBase() {
		return promptBase;
	}

	public void setPromptBase(String promptBase) {
		this.promptBase = promptBase;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getIaUsada() {
		return iaUsada;
	}

	public void setIaUsada(String iaUsada) {
		this.iaUsada = iaUsada;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, fecha, prompt, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Solicitud other = (Solicitud) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Solicitud [id=" + id + ", fecha=" + fecha + ", ecosistema=" + ecosistema + ", prompt=" + prompt
				+ ", iaUsada=" + iaUsada + ", username=" + username + "]";
	}

}
