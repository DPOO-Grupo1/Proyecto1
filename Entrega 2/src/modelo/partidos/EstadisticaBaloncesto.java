package modelo.partidos;

import modelo.deportes.JugadorEquipo;

/**
 * Estadística de baloncesto: puntos y rebotes.
 */
public class EstadisticaBaloncesto extends Estadistica {

	public static final String DEPORTE = "Baloncesto";

	private int puntos;
	private int rebotes;

	public EstadisticaBaloncesto(JugadorEquipo jugador, int puntos, int rebotes) {
		super(jugador);
		this.puntos = puntos;
		this.rebotes = rebotes;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getRebotes() {
		return rebotes;
	}

	@Override
	public String getNombreDeporte() {
		return DEPORTE;
	}

	@Override
	public int valorDestacado() {
		return puntos;
	}

	@Override
	public String toString() {
		return jugador.getSocio().getNombreCompleto() + ": " + puntos + " puntos, " + rebotes + " rebotes";
	}
}
