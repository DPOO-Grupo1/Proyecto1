package excepciones;

/**
 * Se lanza cuando una venta no cumple las reglas de la tienda o la cafetería.
 */
public class VentaInvalidaException extends ClubException {

	public VentaInvalidaException(String mensaje) {
		super(mensaje);
	}
}
