package excepciones;

/**
 * Se lanza cuando el usuario no tiene el rol que exige la acción.
 */
public class PermisoDenegadoException extends ClubException {

	public PermisoDenegadoException(String mensaje) {
		super(mensaje);
	}
}
