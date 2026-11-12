package modelo.academias;

/**
 * Estadística base de un socio en un partido de práctica.
 * Todos los deportes de academia llevan la cuenta de sets ganados.
 */
public abstract class EstadisticaPractica {

	protected int setsGanados;

	public EstadisticaPractica(int setsGanados) {
		this.setsGanados = setsGanados;
	}

	public int getSetsGanados() {
		return setsGanados;
	}

	@Override
	public String toString() {
		return setsGanados + " sets";
	}
}
