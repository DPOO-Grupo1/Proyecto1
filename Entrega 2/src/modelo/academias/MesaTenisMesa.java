package modelo.academias;

/**
 * Mesa de tenis de mesa. Admite sencillos (2 jugadores) y dobles (4 jugadores).
 * Restringe bebidas calientes solo si es techada (RF4, RF7).
 */
public class MesaTenisMesa extends Instalacion {

	private boolean techada;

	public MesaTenisMesa(String id, String nombre, Disciplina disciplina, boolean techada) {
		super(id, nombre, disciplina);
		this.techada = techada;
	}

	public boolean isTechada() {
		return techada;
	}

	@Override
	public boolean esReservable() {
		return true;
	}

	@Override
	public boolean admite(int numJugadores, Modalidad modalidad) {
		if (modalidad == Modalidad.SENCILLOS && numJugadores == 2) {
			return true;
		}
		if (modalidad == Modalidad.DOBLES && numJugadores == 4) {
			return true;
		}
		return false;
	}

	@Override
	public boolean restringeBebidaCaliente() {
		return techada;
	}

	@Override
	public String toString() {
		return super.toString() + (techada ? " (techada)" : " (al aire libre)");
	}
}
