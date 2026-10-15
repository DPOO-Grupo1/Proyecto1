package modelo.competencias;

import java.util.ArrayList;
import java.util.List;

/**
 * Grupos (A, B, C...) y después eliminación directa.
 * Clasifican los primeros de cada grupo.
 */
public class GruposEliminacion extends Formato {

	private int numeroGrupos;
	private int clasificadosPorGrupo;

	public GruposEliminacion(int numeroGrupos, int clasificadosPorGrupo) {
		super(CRITERIO_POR_DEFECTO);
		this.numeroGrupos = numeroGrupos;
		this.clasificadosPorGrupo = clasificadosPorGrupo;
	}

	public int getNumeroGrupos() {
		return numeroGrupos;
	}

	public int getClasificadosPorGrupo() {
		return clasificadosPorGrupo;
	}

	public List<String> getGrupos() {
		List<String> grupos = new ArrayList<String>();
		for (int i = 0; i < numeroGrupos; i++) {
			grupos.add(String.valueOf((char) ('A' + i)));
		}
		return grupos;
	}

	@Override
	public boolean grupoValido(String grupo) {
		if (grupo == null) {
			return false;
		}
		for (String nombre : getGrupos()) {
			if (nombre.equalsIgnoreCase(grupo)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ParticipanteCompetencia> getClasificados(Competencia competencia) {
		List<ParticipanteCompetencia> clasificados = new ArrayList<ParticipanteCompetencia>();
		for (String grupo : getGrupos()) {
			List<FilaTabla> tabla = competencia.calcularTabla(grupo);
			for (int i = 0; i < clasificadosPorGrupo && i < tabla.size(); i++) {
				clasificados.add(tabla.get(i).getParticipante());
			}
		}
		return clasificados;
	}

	@Override
	public String toString() {
		return "Grupos y eliminación (" + numeroGrupos + " grupos, " + clasificadosPorGrupo + " clasificados por grupo)";
	}
}
