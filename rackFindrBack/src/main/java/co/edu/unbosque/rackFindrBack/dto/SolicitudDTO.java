package co.edu.unbosque.rackFindrBack.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Transferencia de Datos (DTO) para representar una solicitud de búsqueda de librerías,
 * incluyendo el ecosistema ({@code npm} o {@code maven}), la consulta del usuario y la lista de resultados.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public class SolicitudDTO {

	private Long id;
	private LocalDateTime fecha;
	private String ecosistema;
	private String prompt;
	private String consulta;
	private String promptBase;
	private String contenido;
	private String iaUsada;
	private String username;
	private Long userId;
	private List<ResultadoDTO> resultados = new ArrayList<>();

	public SolicitudDTO() {
		this.fecha = LocalDateTime.now();
	}

	public SolicitudDTO(String ecosistema, String prompt, String username) {
		this();
		this.ecosistema = ecosistema;
		this.prompt = prompt;
		this.consulta = prompt;
		this.username = username;
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
		return (prompt != null && !prompt.isBlank()) ? prompt : consulta;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getConsulta() {
		return (consulta != null && !consulta.isBlank()) ? consulta : prompt;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
		if (this.prompt == null || this.prompt.isBlank()) {
			this.prompt = consulta;
		}
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<ResultadoDTO> getResultados() {
		return resultados;
	}

	public void setResultados(List<ResultadoDTO> resultados) {
		this.resultados = resultados == null ? null : new java.util.ArrayList<>(resultados);
	}

	@Override
	public String toString() {
		return "SolicitudDTO [id=" + id + ", fecha=" + fecha + ", ecosistema=" + ecosistema + ", prompt=" + getPrompt()
				+ ", iaUsada=" + iaUsada + ", username=" + username + "]";
	}

}
