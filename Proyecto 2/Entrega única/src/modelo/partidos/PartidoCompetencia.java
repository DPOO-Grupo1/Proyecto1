package modelo.partidos;

import java.time.LocalDate;

import modelo.competencias.Competencia;
import modelo.competencias.ParticipanteCompetencia;
import modelo.deportes.Equipo;

/**
 * Partido oficial de una competencia. Puede ser incluso entre dos rivales externos.
 */
public class PartidoCompetencia extends Partido {

	private String escenario;
	private String fase;
	private Competencia competencia;
	private ParticipanteCompetencia local;
	private ParticipanteCompetencia visitante;

	public PartidoCompetencia(int id, LocalDate fecha, Competencia competencia, ParticipanteCompetencia local,
			ParticipanteCompetencia visitante, String escenario, String fase) {
		super(id, fecha);
		this.competencia = competencia;
		this.local = local;
		this.visitante = visitante;
		this.escenario = escenario;
		this.fase = fase;
	}

	public String getEscenario() {
		return escenario;
	}

	public String getFase() {
		return fase;
	}

	public Competencia getCompetencia() {
		return competencia;
	}

	public ParticipanteCompetencia getLocal() {
		return local;
	}

	public ParticipanteCompetencia getVisitante() {
		return visitante;
	}

	/**
	 * Null si el equipo no juega este partido.
	 */
	public ParticipanteCompetencia getParticipanteDe(Equipo equipo) {
		if (local.representa(equipo)) {
			return local;
		}
		if (visitante.representa(equipo)) {
			return visitante;
		}
		return null;
	}

	@Override
	public boolean participa(Equipo equipo) {
		return getParticipanteDe(equipo) != null;
	}

	@Override
	public boolean mismoContexto(Partido otro) {
		if (!(otro instanceof PartidoCompetencia)) {
			return false;
		}
		PartidoCompetencia otroPartido = (PartidoCompetencia) otro;
		return otroPartido.getCompetencia() == competencia;
	}

	@Override
	public String toString() {
		String texto = competencia.getNombre() + " #" + id + " " + fecha + " (" + fase + ", " + escenario + "): "
				+ local.getNombre() + " vs " + visitante.getNombre();
		if (jugado) {
			texto += " " + marcadorLocal + "-" + marcadorVisitante;
		}
		return texto;
	}
}
