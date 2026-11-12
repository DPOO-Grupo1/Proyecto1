package modelo.academias;

/**
 * Estadísticas de un jugador en partidos de tenis de mesa.
 * Registra sets ganados y puntos totales obtenidos (RF10).
 */
public class EstadisticaTenisMesa extends EstadisticaPractica {

	private int puntosTotales;

	public EstadisticaTenisMesa(int setsGanados, int puntosTotales) {
		super(setsGanados);
		this.puntosTotales = puntosTotales;
	}

	public int getPuntosTotales() {
		return puntosTotales;
	}

	@Override
	public String toString() {
		return setsGanados + " sets, " + puntosTotales + " puntos totales";
	}
}
