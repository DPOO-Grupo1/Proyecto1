package excepciones;

/**
 * Se lanza cuando el login o la contraseña son incorrectos.
 */
public class AutenticacionException extends ClubException {

	public AutenticacionException(String mensaje) {
		super(mensaje);
	}
}
