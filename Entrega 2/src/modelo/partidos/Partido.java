package modelo.partidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;

/**
 * Partido de un deporte de conjunto. Puede ser interno o de competencia.
 */
public abstract class Partido {

	protected int id;
	protected LocalDate fecha;
	protected int marcadorLocal;
	protected int marcadorVisitante;
	protected boolean jugado;
	protected List<Estadistica> estadisticas;

	public Partido(int id, LocalDate fecha) {
		this.id = id;
		this.fecha = fecha;
		this.marcadorLocal = 0;
		this.marcadorVisitante = 0;
		this.jugado = false;
		this.estadisticas = new ArrayList<Estadistica>();
	}

	public int getId() {
		return id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public boolean isJugado() {
		return jugado;
	}

	public void registrarResultado(int marcadorLocal, int marcadorVisitante) {
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
		this.jugado = true;
	}

	public List<Estadistica> getEstadisticas() {
		return estadisticas;
	}

	public void agregarEstadistica(Estadistica estadistica) {
		estadisticas.add(estadistica);
	}

	/**
	 * Si el jugador tiene estadística es porque jugó el partido.
	 */
	public boolean tieneEstadisticaDe(JugadorEquipo jugador) {
		return getEstadisticaDe(jugador) != null;
	}

	/**
	 * Null si el jugador no jugó.
	 */
	public Estadistica getEstadisticaDe(JugadorEquipo jugador) {
		for (Estadistica estadistica : estadisticas) {
			if (estadistica.getJugador() == jugador) {
				return estadistica;
			}
		}
		return null;
	}

	/**
	 * Cada tipo de partido sabe qué equipos lo juegan.
	 */
	public abstract boolean participa(Equipo equipo);

	/**
	 * Sirve para saber dónde aplica una suspensión: los internos con los internos,
	 * y los de competencia con los de su misma competencia.
	 */
	public abstract boolean mismoContexto(Partido otro);
}
