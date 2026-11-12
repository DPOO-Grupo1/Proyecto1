package modelo.academias;

/**
 * Cancha de tenis. Admite sencillos (2 jugadores) y dobles (4 jugadores).
 * No restringe bebidas calientes.
 */
public class CanchaTenis extends Instalacion {

	public CanchaTenis(String id, String nombre, Disciplina disciplina) {
		super(id, nombre, disciplina);
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
		return false;
	}
}
