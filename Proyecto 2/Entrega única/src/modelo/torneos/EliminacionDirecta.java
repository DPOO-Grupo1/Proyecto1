package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

/**
 * Cuadro de eliminación: el que pierde queda por fuera y el ganador de la final es el campeón.
 * En la primera ronda los preferenciales van primero, y se enfrenta el mejor sembrado contra
 * el peor. Si en una ronda quedan impares, el mejor sembrado pasa directo.
 */
public class EliminacionDirecta extends FormatoTorneo {

	@Override
	public String getNombre() {
		return "Eliminación directa";
	}

	@Override
	public List<EncuentroTorneo> crearEncuentrosIniciales(Torneo torneo) {
		List<InscripcionTorneo> sembrados = new ArrayList<InscripcionTorneo>();
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			if (inscripcion.isPreferencial()) {
				sembrados.add(inscripcion);
			}
		}
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			if (!inscripcion.isPreferencial()) {
				sembrados.add(inscripcion);
			}
		}
		return crearRonda(torneo, sembrados, 1);
	}

	@Override
	public List<EncuentroTorneo> crearSiguienteRonda(Torneo torneo) {
		int ronda = torneo.getRondaActual();
		List<InscripcionTorneo> ganadores = new ArrayList<InscripcionTorneo>();
		for (EncuentroTorneo encuentro : torneo.getEncuentrosDeRonda(ronda)) {
			if (!encuentro.isJugado()) {
				return new ArrayList<EncuentroTorneo>();
			}
			ganadores.add(encuentro.getGanador());
		}
		if (ganadores.size() <= 1) {
			return new ArrayList<EncuentroTorneo>();
		}
		return crearRonda(torneo, ganadores, ronda + 1);
	}

	/**
	 * Empareja al primero con el último, al segundo con el penúltimo, y así.
	 */
	private List<EncuentroTorneo> crearRonda(Torneo torneo, List<InscripcionTorneo> clasificados, int ronda) {
		List<EncuentroTorneo> nuevos = new ArrayList<EncuentroTorneo>();
		int numero = torneo.getEncuentros().size() + 1;
		int inicio = 0;
		if (clasificados.size() % 2 == 1) {
			nuevos.add(new EncuentroTorneo(numero, ronda, clasificados.get(0), null));
			numero++;
			inicio = 1;
		}
		int fin = clasificados.size() - 1;
		while (inicio < fin) {
			nuevos.add(new EncuentroTorneo(numero, ronda, clasificados.get(inicio), clasificados.get(fin)));
			numero++;
			inicio++;
			fin--;
		}
		return nuevos;
	}

	/**
	 * En eliminación alguien tiene que pasar.
	 */
	@Override
	public boolean admiteEmpate(Torneo torneo) {
		return false;
	}

	/**
	 * Termina cuando la última ronda está jugada y dejó un solo ganador (la final).
	 */
	@Override
	public boolean estaTerminado(Torneo torneo) {
		int ronda = torneo.getRondaActual();
		if (ronda == 0) {
			return false;
		}
		List<EncuentroTorneo> ultima = torneo.getEncuentrosDeRonda(ronda);
		for (EncuentroTorneo encuentro : ultima) {
			if (!encuentro.isJugado()) {
				return false;
			}
		}
		return ultima.size() == 1 && !ultima.get(0).isPaseDirecto();
	}

	@Override
	public List<FilaTorneo> calcularPosiciones(Torneo torneo) {
		List<FilaTorneo> filas = crearFilas(torneo);
		boolean terminado = estaTerminado(torneo);
		for (FilaTorneo fila : filas) {
			fila.setEtapa(describirEtapa(torneo, fila.getParticipante(), terminado));
		}
		ordenar(filas, torneo);
		return filas;
	}

	private String describirEtapa(Torneo torneo, InscripcionTorneo inscripcion, boolean terminado) {
		if (torneo.getRondaActual() == 0) {
			return "Sin jugar";
		}
		for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
			if (encuentro.isJugado() && !encuentro.isPaseDirecto() && encuentro.participa(inscripcion)
					&& encuentro.getGanador() != inscripcion) {
				return "Eliminado en la ronda " + encuentro.getRonda();
			}
		}
		if (terminado) {
			return "Campeón";
		}
		return "Sigue en competencia";
	}

	/**
	 * Rondas que pasó, contando los pases directos. El campeón es el que más pasó.
	 */
	private int contarRondasSuperadas(Torneo torneo, InscripcionTorneo inscripcion) {
		int rondas = 0;
		for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
			if (encuentro.getGanador() == inscripcion) {
				rondas++;
			}
		}
		return rondas;
	}

	/**
	 * Primero el que llegó más lejos y, entre los que quedaron en la misma ronda, el de mejor diferencia.
	 */
	@Override
	protected boolean vaAntes(FilaTorneo a, FilaTorneo b, Torneo torneo) {
		int rondasA = contarRondasSuperadas(torneo, a.getParticipante());
		int rondasB = contarRondasSuperadas(torneo, b.getParticipante());
		if (rondasA != rondasB) {
			return rondasA > rondasB;
		}
		return a.getDiferencia() > b.getDiferencia();
	}
}
