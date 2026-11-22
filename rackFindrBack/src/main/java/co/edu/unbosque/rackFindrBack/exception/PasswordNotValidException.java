package co.edu.unbosque.rackFindrBack.exception;

/**
 * Excepción personalizada que se lanza cuando una contraseña no cumple con las políticas mínimas
 * de seguridad establecidas (longitud mínima, mayúsculas, minúsculas, números y símbolos).
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public class PasswordNotValidException extends Exception {

	private static final long serialVersionUID = -1290628531913444044L;

	/**
	 * Crea una nueva instancia de {@code PasswordNotValidException} con el mensaje estándar de validación.
	 */
	public PasswordNotValidException() {
		super("The password does not meet the standard "
				+ "(8 characters minimum, at least one lowercase letter, "
				+ "at least one uppercase letter, "
				+ "at least one number and at least one symbol).");
	}

}
