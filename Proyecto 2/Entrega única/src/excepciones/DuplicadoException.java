package excepciones;

/**
 * Se lanza cuando se intenta crear algo que debe ser único y ya existe.
 */
public class DuplicadoException extends ClubException {

	public DuplicadoException(String mensaje) {
		super(mensaje);
	}
}
