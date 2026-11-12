package excepciones;

/**
 * Se lanza cuando se rompe una regla del deporte: deporte o categoría distintos,
 * empates, edad, estadística de otro deporte o mínimo de habilitados.
 */
public class ReglaDeportivaException extends ClubException {

	public ReglaDeportivaException(String mensaje) {
		super(mensaje);
	}
}
