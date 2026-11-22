package co.edu.unbosque.rackFindrBack.service;

import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD estándar para los servicios de la aplicación.
 *
 * @param <T> tipo del Objeto de Transferencia de Datos (DTO) gestionado por el servicio
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public interface CRUDOperation<T> {

	/**
	 * Crea un nuevo registro en el sistema.
	 *
	 * @param data DTO con los datos a registrar
	 * @return código de estado ({@code 0} si fue exitoso, mayor a {@code 0} en caso de error)
	 */
	public int create(T data);

	/**
	 * Obtiene la lista completa de registros almacenados.
	 *
	 * @return lista de objetos DTO
	 */
	public List<T> getAll();

	/**
	 * Elimina un registro identificado por su clave primaria.
	 *
	 * @param id identificador único del registro a eliminar
	 * @return código de estado ({@code 0} si se eliminó, {@code 1} si no se encontró)
	 */
	public int deleteById(Long id);

	/**
	 * Actualiza los datos de un registro existente identificado por su ID.
	 *
	 * @param id      identificador único del registro a modificar
	 * @param newData DTO con los nuevos valores
	 * @return código de estado ({@code 0} si se actualizó correctamente)
	 */
	public int updateById(Long id, T newData);

	/**
	 * Cuenta el número total de registros existentes.
	 *
	 * @return total de registros
	 */
	public long count();

	/**
	 * Verifica si existe un registro con el identificador proporcionado.
	 *
	 * @param id identificador único a verificar
	 * @return {@code true} si existe, {@code false} en caso contrario
	 */
	public boolean exist(Long id);

}
