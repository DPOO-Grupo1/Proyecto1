package modelo.deportes;

import java.util.ArrayList;
import java.util.List;

import modelo.partidos.Partido;
import modelo.usuarios.Socio;

/**
 * Un socio jugando en un equipo, con su número y posición.
 * Un socio puede jugar en varios equipos si son de deportes distintos.
 */
public class JugadorEquipo {

	private int numeroCamiseta;
	private String posicion;
	private boolean activo;
	private Socio socio;
	private Equipo equipo;
	private List<Suspension> suspensiones;

	public JugadorEquipo(Socio socio, Equipo equipo, int numeroCamiseta, String posicion) {
		this.socio = socio;
		this.equipo = equipo;
		this.numeroCamiseta = numeroCamiseta;
		this.posicion = posicion;
		this.activo = true;
		this.suspensiones = new ArrayList<Suspension>();
	}

	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}

	public String getPosicion() {
		return posicion;
	}

	public Socio getSocio() {
		return socio;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public boolean estaActivo() {
		return activo;
	}

	/**
	 * Retira al jugador. El registro no se borra.
	 */
	public void desactivar() {
		activo = false;
	}

	// ---------------- Suspensiones ----------------

	public List<Suspension> getSuspensiones() {
		return suspensiones;
	}

	public void agregarSuspension(Suspension suspension) {
		suspensiones.add(suspension);
	}

	/**
	 * La primera suspensión pendiente, o null si no tiene.
	 */
	public Suspension getSuspensionPendiente() {
		for (Suspension suspension : suspensiones) {
			if (suspension.estaPendiente()) {
				return suspension;
			}
		}
		return null;
	}

	/**
	 * La suspensión pendiente que aplica en ese partido, o null.
	 */
	public Suspension getSuspensionPendientePara(Partido partido) {
		for (Suspension suspension : suspensiones) {
			if (suspension.estaPendiente() && suspension.aplicaEn(partido)) {
				return suspension;
			}
		}
		return null;
	}

	/**
	 * Activo y sin suspensiones pendientes de ningún tipo.
	 */
	public boolean estaHabilitado() {
		return activo && getSuspensionPendiente() == null;
	}

	/**
	 * La suspensión que no lo deja jugar ese partido, o null.
	 * También cuenta la que se está cumpliendo justo en ese partido.
	 */
	public Suspension getSuspensionQueImpide(Partido partido) {
		for (Suspension suspension : suspensiones) {
			if (suspension.getCumplidaEn() == partido) {
				return suspension;
			}
		}
		return getSuspensionPendientePara(partido);
	}

	public boolean estaHabilitadoPara(Partido partido) {
		return activo && getSuspensionQueImpide(partido) == null;
	}

	/**
	 * Por qué no puede jugar ese partido, o null si sí puede.
	 */
	public String motivoNoHabilitadoPara(Partido partido) {
		String nombre = socio.getNombreCompleto();
		if (!activo) {
			return nombre + " está inactivo en " + equipo.getNombre();
		}
		Suspension suspension = getSuspensionQueImpide(partido);
		if (suspension != null) {
			return nombre + " está suspendido para el partido #" + partido.getId() + ": " + suspension.getMotivo()
					+ " en el partido #" + suspension.getOrigen().getId();
		}
		return null;
	}

	@Override
	public String toString() {
		return socio.getNombreCompleto() + " #" + numeroCamiseta + " (" + posicion + ", " + equipo.getNombre() + ")";
	}
}
