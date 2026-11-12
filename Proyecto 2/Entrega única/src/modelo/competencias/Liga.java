package modelo.competencias;

import java.util.ArrayList;
import java.util.List;

/**
 * Liga: todos contra todos en una sola tabla.
 */
public class Liga extends Formato {

	private boolean idaYVuelta;

	public Liga(boolean idaYVuelta) {
		super(CRITERIO_POR_DEFECTO);
		this.idaYVuelta = idaYVuelta;
	}

	public boolean isIdaYVuelta() {
		return idaYVuelta;
	}

	/**
	 * En liga no hay grupos.
	 */
	@Override
	public boolean grupoValido(String grupo) {
		return grupo == null || grupo.isEmpty();
	}

	/**
	 * En liga cuenta la tabla completa.
	 */
	@Override
	public List<ParticipanteCompetencia> getClasificados(Competencia competencia) {
		List<ParticipanteCompetencia> clasificados = new ArrayList<ParticipanteCompetencia>();
		for (FilaTabla fila : competencia.calcularTabla(null)) {
			clasificados.add(fila.getParticipante());
		}
		return clasificados;
	}

	@Override
	public String toString() {
		return "Liga" + (idaYVuelta ? " (ida y vuelta)" : "");
	}
}
