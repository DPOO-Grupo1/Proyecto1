package modelo.academias;

import excepciones.DatoInvalidoException;

/**
 * Nivel de un socio en una disciplina. El orden importa: define qué niveles son adyacentes.
 */
public enum Nivel {

	PRINCIPIANTE, INTERMEDIO, AVANZADO;

	/**
	 * True si los niveles son iguales o están uno al lado del otro.
	 * Principiante y Avanzado no son adyacentes.
	 */
	public boolean esAdyacenteA(Nivel otro) {
		return Math.abs(this.ordinal() - otro.ordinal()) <= 1;
	}

	/**
	 * Convierte el texto que escribe el usuario ("Intermedio", "avanzado"...) en un nivel.
	 */
	public static Nivel desdeTexto(String texto) throws DatoInvalidoException {
		for (Nivel nivel : values()) {
			if (nivel.name().equalsIgnoreCase(texto)) {
				return nivel;
			}
		}
		throw new DatoInvalidoException("El nivel '" + texto + "' no existe: debe ser Principiante, Intermedio o Avanzado");
	}
}
