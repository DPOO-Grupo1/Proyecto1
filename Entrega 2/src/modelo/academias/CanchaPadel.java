package modelo.academias;

/**
 * Cancha de pádel. Solo admite dobles con 4 jugadores
 * Restringe el ingreso de bebidas calientes
 */
public class CanchaPadel extends Instalacion {

	public CanchaPadel(String id, String nombre, Disciplina disciplina) {
		super(id, nombre, disciplina);
	}

	@Override
	public boolean esReservable() {
		return true;
	}

	@Override
	public boolean admite(int numJugadores, Modalidad modalidad) {
		return modalidad == Modalidad.DOBLES && numJugadores == 4;
	}

	@Override
	public boolean restringeBebidaCaliente() {
		return true;
	}
}
