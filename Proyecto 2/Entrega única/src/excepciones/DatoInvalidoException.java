package excepciones;

/**
 * Se lanza cuando un valor no tiene sentido (marcador negativo, fecha pasada...).
 */
public class DatoInvalidoException extends ClubException {

	public DatoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
