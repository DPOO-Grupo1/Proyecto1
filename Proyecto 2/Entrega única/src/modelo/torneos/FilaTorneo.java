package modelo.torneos;

/**
 * Una fila de las posiciones de un torneo. No se guarda, se calcula con los encuentros.
 */
public class FilaTorneo {

	private InscripcionTorneo participante;
	private int jugados;
	private int ganados;
	private int empatados;
	private int perdidos;
	private int aFavor;
	private int enContra;
	private int puntos;
	private String etapa;

	public FilaTorneo(InscripcionTorneo participante) {
		this.participante = participante;
		this.etapa = null;
	}

	public void registrarEncuentro(int propios, int rival, int puntosObtenidos) {
		jugados++;
		if (propios > rival) {
			ganados++;
		} else if (propios == rival) {
			empatados++;
		} else {
			perdidos++;
		}
		aFavor += propios;
		enContra += rival;
		puntos += puntosObtenidos;
	}

	public InscripcionTorneo getParticipante() {
		return participante;
	}

	public int getJugados() {
		return jugados;
	}

	public int getGanados() {
		return ganados;
	}

	public int getEmpatados() {
		return empatados;
	}

	public int getPerdidos() {
		return perdidos;
	}

	public int getAFavor() {
		return aFavor;
	}

	public int getEnContra() {
		return enContra;
	}

	public int getDiferencia() {
		return aFavor - enContra;
	}

	public int getPuntos() {
		return puntos;
	}

	/**
	 * Hasta dónde llegó en eliminación directa ("Campeón", "Llegó a la ronda 2"...).
	 * Null en todos contra todos.
	 */
	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	@Override
	public String toString() {
		String texto = participante.getNombre();
		if (etapa != null) {
			texto += " | " + etapa;
		}
		return texto + " | PJ " + jugados + " PG " + ganados + " PE " + empatados + " PP " + perdidos + " | " + aFavor
				+ ":" + enContra + " | " + puntos + " pts";
	}
}
