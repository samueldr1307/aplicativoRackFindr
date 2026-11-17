package co.edu.unbosque.rackFindrBack.dto;

import java.util.Objects;

public class ResultadoDTO {
	
	private String nombre;
	private String descripcion;
	private String instalacion;
	private String comoUsar;
	private String ejemplo;
	private String iaOrigen;

	public ResultadoDTO() {
	}

	public ResultadoDTO(String nombre, String descripcion, String instalacion, String comoUsar, String ejemplo,
			String iaOrigen) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.instalacion = instalacion;
		this.comoUsar = comoUsar;
		this.ejemplo = ejemplo;
		this.iaOrigen = iaOrigen;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getInstalacion() {
		return instalacion;
	}

	public void setInstalacion(String instalacion) {
		this.instalacion = instalacion;
	}

	public String getComoUsar() {
		return comoUsar;
	}

	public void setComoUsar(String comoUsar) {
		this.comoUsar = comoUsar;
	}

	public String getEjemplo() {
		return ejemplo;
	}

	public void setEjemplo(String ejemplo) {
		this.ejemplo = ejemplo;
	}

	public String getIaOrigen() {
		return iaOrigen;
	}

	public void setIaOrigen(String iaOrigen) {
		this.iaOrigen = iaOrigen;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombre, instalacion, iaOrigen);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ResultadoDTO other = (ResultadoDTO) obj;
		return Objects.equals(nombre, other.nombre) && Objects.equals(instalacion, other.instalacion);
	}

	@Override
	public String toString() {
		return "ResultadoDTO [nombre=" + nombre + ", descripcion=" + descripcion + ", instalacion=" + instalacion
				+ ", comoUsar=" + comoUsar + ", ejemplo=" + ejemplo + ", iaOrigen=" + iaOrigen + "]";
	}


}
