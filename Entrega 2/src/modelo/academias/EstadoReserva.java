package modelo.academias;

/**
 * Estado de una reserva de instalación.
 */
public enum EstadoReserva {

	ACTIVA, CANCELADA, FINALIZADA;

	public static EstadoReserva desdeTexto(String texto) {
		for (EstadoReserva estado : values()) {
			if (estado.name().equalsIgnoreCase(texto)) {
				return estado;
			}
		}
		return ACTIVA;
	}
}
