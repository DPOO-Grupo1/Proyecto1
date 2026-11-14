package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

/**
 * Cada inscrito juega una vez contra todos los demás. Gana el que más puntos sume.
 */
public class TodosContraTodos extends FormatoTorneo {

	@Override
	public String getNombre() {
		return "Todos contra todos";
	}

	@Override
	public List<EncuentroTorneo> crearEncuentrosIniciales(Torneo torneo) {
		List<EncuentroTorneo> nuevos = new ArrayList<EncuentroTorneo>();
		List<InscripcionTorneo> inscritos = torneo.getInscripciones();
		int numero = torneo.getEncuentros().size() + 1;
		for (int i = 0; i < inscritos.size(); i++) {
			for (int j = i + 1; j < inscritos.size(); j++) {
				nuevos.add(new EncuentroTorneo(numero, 1, inscritos.get(i), inscritos.get(j)));
				numero++;
			}
		}
		return nuevos;
	}

	/**
	 * Todo se juega en una sola ronda.
	 */
	@Override
	public List<EncuentroTorneo> crearSiguienteRonda(Torneo torneo) {
		return new ArrayList<EncuentroTorneo>();
	}

	@Override
	public boolean admiteEmpate(Torneo torneo) {
		return torneo.admiteEmpate();
	}

	@Override
	public boolean estaTerminado(Torneo torneo) {
		return !torneo.getEncuentros().isEmpty() && torneo.getEncuentrosPendientes().isEmpty();
	}

	@Override
	public List<FilaTorneo> calcularPosiciones(Torneo torneo) {
		List<FilaTorneo> filas = crearFilas(torneo);
		ordenar(filas, torneo);
		return filas;
	}

	/**
	 * Puntos, luego diferencia y luego tantos a favor.
	 */
	@Override
	protected boolean vaAntes(FilaTorneo a, FilaTorneo b, Torneo torneo) {
		if (a.getPuntos() != b.getPuntos()) {
			return a.getPuntos() > b.getPuntos();
		}
		if (a.getDiferencia() != b.getDiferencia()) {
			return a.getDiferencia() > b.getDiferencia();
		}
		return a.getAFavor() > b.getAFavor();
	}
}
