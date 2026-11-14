package modelo.torneos;

import java.time.LocalDate;
import java.util.List;

import modelo.usuarios.Usuario;

/**
 * Quien ocupa un cupo en un torneo: un equipo completo, o un jugador (o pareja) en
 * las disciplinas individuales.
 */
public abstract class InscripcionTorneo {

	protected LocalDate fecha;
	protected boolean preferencial;
	protected double valorPagado;

	public InscripcionTorneo(LocalDate fecha, boolean preferencial) {
		this.fecha = fecha;
		this.preferencial = preferencial;
		this.valorPagado = 0;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	/**
	 * True si ocupa uno de los cupos reservados para los mejores del ranking.
	 */
	public boolean isPreferencial() {
		return preferencial;
	}

	public double getValorPagado() {
		return valorPagado;
	}

	public void setValorPagado(double valorPagado) {
		this.valorPagado = valorPagado;
	}

	/**
	 * Nombre que se muestra: el del equipo o el de los jugadores.
	 */
	public abstract String getNombre();

	/**
	 * Identifica la inscripción en el archivo: el nombre del equipo o los login.
	 */
	public abstract String getClave();

	/**
	 * Personas que juegan por esta inscripción.
	 */
	public abstract List<Usuario> getJugadores();

	/**
	 * Cuántas tarifas paga en un torneo competitivo.
	 */
	public abstract int contarTarifas();

	public boolean incluye(Usuario usuario) {
		return getJugadores().contains(usuario);
	}

	@Override
	public String toString() {
		return getNombre() + (preferencial ? " (cupo preferencial)" : "");
	}
}
