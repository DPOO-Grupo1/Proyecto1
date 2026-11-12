package modelo.deportes;

import modelo.partidos.Partido;

/**
 * Suspensión por tarjetas. Nace en un partido y se cumple en el siguiente del mismo tipo.
 */
public class Suspension {

	public static final String MOTIVO_ROJA = "Roja directa";
	public static final String MOTIVO_DOS_AMARILLAS = "2 amarillas";
	public static final String MOTIVO_ACUMULACION = "Acumulación de amarillas";

	private String motivo;
	private Partido origen;
	private Partido cumplidaEn;

	public Suspension(String motivo, Partido origen) {
		this.motivo = motivo;
		this.origen = origen;
		this.cumplidaEn = null;
	}

	public String getMotivo() {
		return motivo;
	}

	public Partido getOrigen() {
		return origen;
	}

	/**
	 * Null mientras esté pendiente.
	 */
	public Partido getCumplidaEn() {
		return cumplidaEn;
	}

	public boolean estaPendiente() {
		return cumplidaEn == null;
	}

	/**
	 * No aplica en el partido donde nació, ni en partidos de otro tipo
	 * o de otra competencia. Eso lo decide el partido de origen.
	 */
	public boolean aplicaEn(Partido partido) {
		return partido != origen && origen.mismoContexto(partido);
	}

	public void cumplir(Partido partido) {
		this.cumplidaEn = partido;
	}

	@Override
	public String toString() {
		String texto = motivo + " (partido #" + origen.getId() + ")";
		if (estaPendiente()) {
			texto += ", pendiente";
		} else {
			texto += ", cumplida en el partido #" + cumplidaEn.getId();
		}
		return texto;
	}
}
