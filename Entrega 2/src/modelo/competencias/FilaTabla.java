package modelo.competencias;

/**
 * Una fila de la tabla de posiciones. No se guarda, se calcula cada vez.
 */
public class FilaTabla {

	private ParticipanteCompetencia participante;
	private int jugados;
	private int ganados;
	private int empatados;
	private int perdidos;
	private int aFavor;
	private int enContra;
	private int puntos;

	public FilaTabla(ParticipanteCompetencia participante) {
		this.participante = participante;
	}

	public void registrarPartido(int propios, int rival, int puntosObtenidos) {
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

	public ParticipanteCompetencia getParticipante() {
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

	@Override
	public String toString() {
		return participante.getNombre() + " | PJ " + jugados + " PG " + ganados + " PE " + empatados + " PP " + perdidos
				+ " | " + aFavor + ":" + enContra + " (" + (getDiferencia() >= 0 ? "+" : "") + getDiferencia() + ") | "
				+ puntos + " pts";
	}
}
