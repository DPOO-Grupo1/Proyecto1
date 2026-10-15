package excepciones;

/**
 * Se lanza cuando un jugador está suspendido, inactivo o fuera de la nómina.
 */
public class JugadorNoHabilitadoException extends ClubException {

	public JugadorNoHabilitadoException(String mensaje) {
		super(mensaje);
	}
}
