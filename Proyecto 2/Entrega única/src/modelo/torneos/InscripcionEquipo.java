package modelo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.usuarios.Usuario;

/**
 * Equipo del club inscrito en un torneo de deporte de conjunto.
 */
public class InscripcionEquipo extends InscripcionTorneo {

	private Equipo equipo;

	public InscripcionEquipo(Equipo equipo, LocalDate fecha, boolean preferencial) {
		super(fecha, preferencial);
		this.equipo = equipo;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	@Override
	public String getNombre() {
		return equipo.getNombre();
	}

	@Override
	public String getClave() {
		return equipo.getNombre();
	}

	/**
	 * Los socios que están activos en el equipo.
	 */
	@Override
	public List<Usuario> getJugadores() {
		List<Usuario> jugadores = new ArrayList<Usuario>();
		for (JugadorEquipo jugador : equipo.getJugadoresActivos()) {
			jugadores.add(jugador.getSocio());
		}
		return jugadores;
	}

	/**
	 * El equipo paga una sola inscripción.
	 */
	@Override
	public int contarTarifas() {
		return 1;
	}
}
