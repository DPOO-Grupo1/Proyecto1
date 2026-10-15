package modelo.competencias;

import java.util.List;

/**
 * Formato de una competencia: liga o grupos.
 * Es una clase aparte porque el formato puede cambiar entre temporadas.
 */
public abstract class Formato {

	public static final String CRITERIO_POR_DEFECTO = "Puntos, enfrentamiento directo y diferencia de goles o sets";

	protected String criterioDesempate;

	public Formato(String criterioDesempate) {
		this.criterioDesempate = criterioDesempate;
	}

	public String getCriterioDesempate() {
		return criterioDesempate;
	}

	/**
	 * Ordena por puntos, luego enfrentamiento directo y luego diferencia.
	 * Es igual para todos los formatos.
	 */
	public void ordenar(List<FilaTabla> filas, Competencia competencia) {
		// Ordenamiento por inserción
		for (int i = 1; i < filas.size(); i++) {
			FilaTabla actual = filas.get(i);
			int j = i - 1;
			while (j >= 0 && vaAntes(actual, filas.get(j), competencia)) {
				filas.set(j + 1, filas.get(j));
				j--;
			}
			filas.set(j + 1, actual);
		}
	}

	protected boolean vaAntes(FilaTabla a, FilaTabla b, Competencia competencia) {
		if (a.getPuntos() != b.getPuntos()) {
			return a.getPuntos() > b.getPuntos();
		}
		int directoA = competencia.puntosEnfrentamientoDirecto(a.getParticipante(), b.getParticipante());
		int directoB = competencia.puntosEnfrentamientoDirecto(b.getParticipante(), a.getParticipante());
		if (directoA != directoB) {
			return directoA > directoB;
		}
		return a.getDiferencia() > b.getDiferencia();
	}

	// Cada formato decide qué grupos acepta y quién clasifica.
	public abstract boolean grupoValido(String grupo);

	public abstract List<ParticipanteCompetencia> getClasificados(Competencia competencia);
}
