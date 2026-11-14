package modelo.torneos;

/**
 * Estados de un torneo. Solo avanza en este orden.
 */
public enum EstadoTorneo {

	INSCRIPCIONES_ABIERTAS("Inscripciones abiertas"), EN_CURSO("En curso"), FINALIZADO("Finalizado");

	private String texto;

	private EstadoTorneo(String texto) {
		this.texto = texto;
	}

	@Override
	public String toString() {
		return texto;
	}
}
