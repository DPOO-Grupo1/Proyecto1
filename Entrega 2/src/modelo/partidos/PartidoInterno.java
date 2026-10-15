package modelo.partidos;

import java.time.LocalDate;

import modelo.deportes.Equipo;

/**
 * Partido entre dos equipos del club, de práctica o de evaluación.
 */
public class PartidoInterno extends Partido {

	private String proposito;
	private Equipo local;
	private Equipo visitante;

	public PartidoInterno(int id, LocalDate fecha, Equipo local, Equipo visitante, String proposito) {
		super(id, fecha);
		this.local = local;
		this.visitante = visitante;
		this.proposito = proposito;
	}

	public String getProposito() {
		return proposito;
	}

	public Equipo getLocal() {
		return local;
	}

	public Equipo getVisitante() {
		return visitante;
	}

	@Override
	public boolean participa(Equipo equipo) {
		return equipo == local || equipo == visitante;
	}

	@Override
	public boolean mismoContexto(Partido otro) {
		return otro instanceof PartidoInterno;
	}

	@Override
	public String toString() {
		String texto = "Partido interno #" + id + " " + fecha + ": " + local.getNombre() + " vs "
				+ visitante.getNombre() + " (" + proposito + ")";
		if (jugado) {
			texto += " " + marcadorLocal + "-" + marcadorVisitante;
		}
		return texto;
	}
}
