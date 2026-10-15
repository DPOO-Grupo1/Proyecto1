package excepciones;

/**
 * Se lanza cuando falla la carga o el guardado de los archivos.
 */
public class PersistenciaException extends ClubException {

	public PersistenciaException(String mensaje) {
		super(mensaje);
	}

	public PersistenciaException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
