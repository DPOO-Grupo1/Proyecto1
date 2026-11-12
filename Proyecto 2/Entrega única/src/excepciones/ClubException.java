package excepciones;

/**
 * Excepción padre de todas las del club.
 * Es abstracta porque siempre se lanza una de sus subclases.
 */
public abstract class ClubException extends Exception {

	public ClubException(String mensaje) {
		super(mensaje);
	}

	public ClubException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
