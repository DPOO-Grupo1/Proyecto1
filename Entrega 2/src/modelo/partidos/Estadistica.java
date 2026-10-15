package modelo.partidos;

import modelo.deportes.JugadorEquipo;

/**
 * Estadística de un jugador en un partido. Hay una subclase por deporte.
 */
public abstract class Estadistica {

	protected JugadorEquipo jugador;

	public Estadistica(JugadorEquipo jugador) {
		this.jugador = jugador;
	}

	public JugadorEquipo getJugador() {
		return jugador;
	}

	/**
	 * Tiene que coincidir con el nombre del DeporteEquipo.
	 */
	public abstract String getNombreDeporte();

	/**
	 * Lo que cuenta para el jugador destacado: asistencias, puntos o aces.
	 */
	public abstract int valorDestacado();
}
