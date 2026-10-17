package modelo.academias;

/**
 * Partido de práctica jugado sobre una reserva de instalación.
 * Se completa en la parte 3 con estadísticas y actualización de ELO.
 */
public class PartidoPractica {

	private String id;
	private Reserva reserva;
	private boolean cancelado;

	public PartidoPractica(String id, Reserva reserva) {
		this.id = id;
		this.reserva = reserva;
		this.cancelado = false;
	}

	public String getId() {
		return id;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public boolean estaCancelado() {
		return cancelado;
	}

	/**
	 * Se cancela automáticamente si la reserva asociada se cancela (RF8).
	 */
	public void cancelar() {
		this.cancelado = true;
	}

	@Override
	public String toString() {
		return "Partido " + id + (cancelado ? " (cancelado)" : "");
	}
}
