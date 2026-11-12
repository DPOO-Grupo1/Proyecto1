package modelo.academias;

import excepciones.DatoInvalidoException;

/**
 * Sencillos o dobles.
 */
public enum Modalidad {

	SENCILLOS(2), DOBLES(4);

	private int jugadores;

	private Modalidad(int jugadores) {
		this.jugadores = jugadores;
	}

	public int getJugadores() {
		return jugadores;
	}

	public static Modalidad desdeTexto(String texto) throws DatoInvalidoException {
		for (Modalidad modalidad : values()) {
			if (modalidad.name().equalsIgnoreCase(texto)) {
				return modalidad;
			}
		}
		throw new DatoInvalidoException("La modalidad '" + texto + "' no existe: debe ser Sencillos o Dobles");
	}
}
