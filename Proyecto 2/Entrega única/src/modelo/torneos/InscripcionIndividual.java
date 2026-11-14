package modelo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modelo.usuarios.Empleado;
import modelo.usuarios.Usuario;

/**
 * Jugador inscrito en un torneo individual. En dobles va con su pareja.
 */
public class InscripcionIndividual extends InscripcionTorneo {

	private Usuario jugador;
	private Usuario pareja;

	/**
	 * La pareja va en null en sencillos.
	 */
	public InscripcionIndividual(Usuario jugador, Usuario pareja, LocalDate fecha, boolean preferencial) {
		super(fecha, preferencial);
		this.jugador = jugador;
		this.pareja = pareja;
	}

	public Usuario getJugador() {
		return jugador;
	}

	/**
	 * Null en sencillos.
	 */
	public Usuario getPareja() {
		return pareja;
	}

	@Override
	public String getNombre() {
		if (pareja == null) {
			return jugador.getNombreCompleto();
		}
		return jugador.getNombreCompleto() + " y " + pareja.getNombreCompleto();
	}

	@Override
	public String getClave() {
		if (pareja == null) {
			return jugador.getLogin();
		}
		return jugador.getLogin() + "+" + pareja.getLogin();
	}

	@Override
	public List<Usuario> getJugadores() {
		List<Usuario> jugadores = new ArrayList<Usuario>();
		jugadores.add(jugador);
		if (pareja != null) {
			jugadores.add(pareja);
		}
		return jugadores;
	}

	/**
	 * Cada jugador paga su tarifa, menos los empleados, que juegan gratis.
	 */
	@Override
	public int contarTarifas() {
		int tarifas = 0;
		for (Usuario usuario : getJugadores()) {
			if (!(usuario instanceof Empleado)) {
				tarifas++;
			}
		}
		return tarifas;
	}
}
