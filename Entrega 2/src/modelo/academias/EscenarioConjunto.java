package modelo.academias;

/**
 * Escenario deportivo para deportes de conjunto (cancha de fútbol, baloncesto,
 * etc.).
 * No es reservable por socios para prácticas de academia
 */
public class EscenarioConjunto extends Instalacion {

	private String tipo;

	public EscenarioConjunto(String id, String nombre, String tipo) {
		super(id, nombre, null);
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	@Override
	public boolean esReservable() {
		return false;
	}

	@Override
	public boolean admite(int numJugadores, Modalidad modalidad) {
		return false;
	}

	@Override
	public boolean restringeBebidaCaliente() {
		return false;
	}

	@Override
	public String toString() {
		return id + ": " + nombre + " (" + tipo + ")";
	}
}
