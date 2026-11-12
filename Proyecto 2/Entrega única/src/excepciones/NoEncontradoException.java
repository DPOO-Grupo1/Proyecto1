package excepciones;

/**
 * Se lanza cuando un identificador no corresponde a ningún objeto del club.
 */
public class NoEncontradoException extends ClubException {

	public NoEncontradoException(String mensaje) {
		super(mensaje);
	}
}
