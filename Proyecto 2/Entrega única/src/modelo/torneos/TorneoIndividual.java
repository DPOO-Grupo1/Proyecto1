package modelo.torneos;

import java.time.LocalDate;

import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;

/**
 * Torneo de una disciplina individual en un nivel, por ejemplo un cuadro de tenis
 * nivel Intermedio. Se inscriben jugadores, o parejas si es de dobles.
 */
public class TorneoIndividual extends Torneo {

	private Disciplina disciplina;
	private Nivel nivel;
	private Modalidad modalidad;

	public TorneoIndividual(String nombre, LocalDate fecha, int cupos, TipoTorneo tipo, FormatoTorneo formato,
			Disciplina disciplina, Nivel nivel, Modalidad modalidad) {
		super(nombre, fecha, cupos, tipo, formato);
		this.disciplina = disciplina;
		this.nivel = nivel;
		this.modalidad = modalidad;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public Modalidad getModalidad() {
		return modalidad;
	}

	public boolean esDobles() {
		return modalidad == Modalidad.DOBLES;
	}

	/**
	 * Un punto por cada victoria. En estas disciplinas no hay empates.
	 */
	@Override
	public int puntosPara(int propios, int rival) {
		return propios > rival ? 1 : 0;
	}

	@Override
	public boolean admiteEmpate() {
		return false;
	}

	@Override
	public String getDescripcion() {
		return disciplina.getNombre() + " nivel " + nivel.name().toLowerCase() + ", "
				+ modalidad.name().toLowerCase();
	}
}
