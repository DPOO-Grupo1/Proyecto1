package excepciones;

/**
 * Se lanza cuando se pasa un límite (equipo lleno, cupo menor al mínimo...).
 */
public class CupoExcedidoException extends ClubException {

	public CupoExcedidoException(String mensaje) {
		super(mensaje);
	}
}
