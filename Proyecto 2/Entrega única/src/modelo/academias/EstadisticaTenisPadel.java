package modelo.academias;

/**
 * Estadísticas de un jugador en partidos de tenis o pádel.
 * Registra sets, games, aces y errores no forzados (RF10).
 */
public class EstadisticaTenisPadel extends EstadisticaPractica {

	private int gamesGanados;
	private int aces;
	private int erroresNoForzados;

	public EstadisticaTenisPadel(int setsGanados, int gamesGanados, int aces, int erroresNoForzados) {
		super(setsGanados);
		this.gamesGanados = gamesGanados;
		this.aces = aces;
		this.erroresNoForzados = erroresNoForzados;
	}

	public int getGamesGanados() {
		return gamesGanados;
	}

	public int getAces() {
		return aces;
	}

	public int getErroresNoForzados() {
		return erroresNoForzados;
	}

	@Override
	public String toString() {
		return setsGanados + " sets, " + gamesGanados + " games, " + aces + " aces, "
				+ erroresNoForzados + " errores no forzados";
	}
}
