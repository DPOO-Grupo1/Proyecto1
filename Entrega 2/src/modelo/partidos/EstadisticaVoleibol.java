package modelo.partidos;

import modelo.deportes.JugadorEquipo;

/**
 * Estadística de voleibol: aces y bloqueos.
 */
public class EstadisticaVoleibol extends Estadistica {

	public static final String DEPORTE = "Voleibol";

	private int aces;
	private int bloqueos;

	public EstadisticaVoleibol(JugadorEquipo jugador, int aces, int bloqueos) {
		super(jugador);
		this.aces = aces;
		this.bloqueos = bloqueos;
	}

	public int getAces() {
		return aces;
	}

	public int getBloqueos() {
		return bloqueos;
	}

	@Override
	public String getNombreDeporte() {
		return DEPORTE;
	}

	@Override
	public int valorDestacado() {
		return aces;
	}

	@Override
	public String toString() {
		return jugador.getSocio().getNombreCompleto() + ": " + aces + " aces, " + bloqueos + " bloqueos";
	}
}
