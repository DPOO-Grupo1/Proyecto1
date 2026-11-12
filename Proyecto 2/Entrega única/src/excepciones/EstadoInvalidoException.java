package excepciones;

/**
 * Se lanza cuando la acción no aplica en el estado actual
 * (resultado ya registrado, equipo inactivo, baja con partidos pendientes...).
 */
public class EstadoInvalidoException extends ClubException {

	public EstadoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
