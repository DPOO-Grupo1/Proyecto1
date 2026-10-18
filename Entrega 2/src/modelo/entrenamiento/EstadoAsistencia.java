package modelo.entrenamiento;

import excepciones.DatoInvalidoException;

/**
 * Estados de asistencia de un socio a una sesión de entrenamiento.
 */
public enum EstadoAsistencia {
	PENDIENTE,
	ASISTIO,
	FALTO,
	TARDE;

	/**
	 * Parsea el texto del estado de asistencia en español flexible (con o sin tilde).
	 */
	public static EstadoAsistencia desdeTexto(String texto) throws DatoInvalidoException {
		if (texto == null) {
			throw new DatoInvalidoException("El estado de asistencia no puede ser nulo");
		}
		String limpio = texto.trim().toLowerCase();
		if (limpio.equals("asistio") || limpio.equals("asistió")) {
			return ASISTIO;
		} else if (limpio.equals("falto") || limpio.equals("faltó")) {
			return FALTO;
		} else if (limpio.equals("tarde") || limpio.equals("llegó tarde") || limpio.equals("llego tarde")) {
			return TARDE;
		} else if (limpio.equals("pendiente")) {
			return PENDIENTE;
		}
		throw new DatoInvalidoException("Estado de asistencia desconocido: '" + texto
				+ "'. Valores válidos: asistió, faltó, llegó tarde");
	}

	@Override
	public String toString() {
		switch (this) {
		case ASISTIO:
			return "asistió";
		case FALTO:
			return "faltó";
		case TARDE:
			return "llegó tarde";
		case PENDIENTE:
			return "pendiente";
		default:
			return name().toLowerCase();
		}
	}
}
