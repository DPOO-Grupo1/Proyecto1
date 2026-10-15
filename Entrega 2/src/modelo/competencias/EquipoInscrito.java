package modelo.competencias;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;

/**
 * Equipo del club inscrito en una competencia, con su nómina.
 * La nómina no cambia después de inscribir.
 */
public class EquipoInscrito extends ParticipanteCompetencia {

	private LocalDate fechaInscripcion;
	private Equipo equipo;
	private List<JugadorEquipo> nomina;

	public EquipoInscrito(Equipo equipo, List<JugadorEquipo> nomina, String grupo, LocalDate fechaInscripcion) {
		super(equipo.getNombre(), grupo);
		this.equipo = equipo;
		this.nomina = new ArrayList<JugadorEquipo>(nomina);
		this.fechaInscripcion = fechaInscripcion;
	}

	public LocalDate getFechaInscripcion() {
		return fechaInscripcion;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	@Override
	public List<JugadorEquipo> getNomina() {
		return nomina;
	}

	@Override
	public boolean representa(Equipo equipo) {
		return this.equipo == equipo;
	}
}
