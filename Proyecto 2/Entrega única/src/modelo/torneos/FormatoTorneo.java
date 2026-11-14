package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

/**
 * Cómo se juega un torneo: todos contra todos o eliminación directa.
 * Cada formato arma sus encuentros, sabe cuándo termina y ordena las posiciones.
 */
public abstract class FormatoTorneo {

	public abstract String getNombre();

	/**
	 * Encuentros con los que arranca el torneo.
	 */
	public abstract List<EncuentroTorneo> crearEncuentrosIniciales(Torneo torneo);

	/**
	 * Encuentros de la siguiente ronda si la actual ya terminó. Vacía si no hay más rondas.
	 */
	public abstract List<EncuentroTorneo> crearSiguienteRonda(Torneo torneo);

	public abstract boolean admiteEmpate(Torneo torneo);

	public abstract boolean estaTerminado(Torneo torneo);

	public abstract List<FilaTorneo> calcularPosiciones(Torneo torneo);

	/**
	 * El primero de las posiciones cuando el torneo terminó. Null mientras tanto.
	 */
	public InscripcionTorneo getGanador(Torneo torneo) {
		if (!estaTerminado(torneo)) {
			return null;
		}
		List<FilaTorneo> posiciones = calcularPosiciones(torneo);
		if (posiciones.isEmpty()) {
			return null;
		}
		return posiciones.get(0).getParticipante();
	}

	/**
	 * Una fila por inscrito con los encuentros jugados (los pases directos no cuentan).
	 */
	protected List<FilaTorneo> crearFilas(Torneo torneo) {
		List<FilaTorneo> filas = new ArrayList<FilaTorneo>();
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			filas.add(new FilaTorneo(inscripcion));
		}
		for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
			if (encuentro.isJugado() && !encuentro.isPaseDirecto()) {
				int local = encuentro.getMarcadorLocal();
				int visitante = encuentro.getMarcadorVisitante();
				buscarFila(filas, encuentro.getLocal()).registrarEncuentro(local, visitante,
						torneo.puntosPara(local, visitante));
				buscarFila(filas, encuentro.getVisitante()).registrarEncuentro(visitante, local,
						torneo.puntosPara(visitante, local));
			}
		}
		return filas;
	}

	private FilaTorneo buscarFila(List<FilaTorneo> filas, InscripcionTorneo inscripcion) {
		for (FilaTorneo fila : filas) {
			if (fila.getParticipante() == inscripcion) {
				return fila;
			}
		}
		// No debería pasar: todo encuentro es entre inscritos
		FilaTorneo nueva = new FilaTorneo(inscripcion);
		filas.add(nueva);
		return nueva;
	}

	/**
	 * Ordenamiento por inserción con el criterio de cada formato.
	 */
	protected void ordenar(List<FilaTorneo> filas, Torneo torneo) {
		for (int i = 1; i < filas.size(); i++) {
			FilaTorneo actual = filas.get(i);
			int j = i - 1;
			while (j >= 0 && vaAntes(actual, filas.get(j), torneo)) {
				filas.set(j + 1, filas.get(j));
				j--;
			}
			filas.set(j + 1, actual);
		}
	}

	protected abstract boolean vaAntes(FilaTorneo a, FilaTorneo b, Torneo torneo);

	@Override
	public String toString() {
		return getNombre();
	}
}
