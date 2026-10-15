package excepciones;

/**
 * Se lanza cuando los turnos no cumplen la cobertura mínima (área C).
 */
public class CoberturaTurnosException extends ClubException {

	public CoberturaTurnosException(String mensaje) {
		super(mensaje);
	}
}
