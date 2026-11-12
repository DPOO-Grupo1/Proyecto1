package modelo.partidos;

import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;

/**
 * Estadística de fútbol: goles, asistencias y tarjetas. Solo el fútbol tiene tarjetas.
 */
public class EstadisticaFutbol extends Estadistica {

	public static final String DEPORTE = "Fútbol";

	private int goles;
	private int asistencias;
	private int amarillas;
	private int rojas;

	public EstadisticaFutbol(JugadorEquipo jugador, int goles, int asistencias, int amarillas, int rojas) {
		super(jugador);
		this.goles = goles;
		this.asistencias = asistencias;
		this.amarillas = amarillas;
		this.rojas = rojas;
	}

	public int getGoles() {
		return goles;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public int getAmarillas() {
		return amarillas;
	}

	public int getRojas() {
		return rojas;
	}

	/**
	 * Una roja o dos amarillas suspenden para el siguiente partido.
	 * Devuelve el motivo, o null si no hay suspensión.
	 */
	public String motivoSuspension() {
		if (rojas >= 1) {
			return Suspension.MOTIVO_ROJA;
		}
		if (amarillas >= 2) {
			return Suspension.MOTIVO_DOS_AMARILLAS;
		}
		return null;
	}

	@Override
	public String getNombreDeporte() {
		return DEPORTE;
	}

	@Override
	public int valorDestacado() {
		return asistencias;
	}

	@Override
	public String toString() {
		return jugador.getSocio().getNombreCompleto() + ": " + goles + " goles, " + asistencias + " asistencias, "
				+ amarillas + " amarillas, " + rojas + " rojas";
	}
}
