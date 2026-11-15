package excepciones;

/**
 * Se lanza cuando los turnos no cumplen la cobertura mínima.
 */
public class CoberturaTurnosException extends ClubException {

	public CoberturaTurnosException(String mensaje) {
		super(mensaje);
	}
}
