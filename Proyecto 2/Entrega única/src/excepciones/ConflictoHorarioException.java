package excepciones;

/**
 * Se lanza cuando dos actividades chocan en el tiempo.
 */
public class ConflictoHorarioException extends ClubException {

	public ConflictoHorarioException(String mensaje) {
		super(mensaje);
	}
}
