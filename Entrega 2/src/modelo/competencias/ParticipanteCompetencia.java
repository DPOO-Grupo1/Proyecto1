package modelo.competencias;

import java.util.List;

import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;

/**
 * Equipo que juega una competencia: uno del club inscrito o un rival externo.
 */
public abstract class ParticipanteCompetencia {

	protected String nombre;
	protected String grupo;

	public ParticipanteCompetencia(String nombre, String grupo) {
		this.nombre = nombre;
		this.grupo = grupo;
	}

	public String getNombre() {
		return nombre;
	}

	/**
	 * Null en liga.
	 */
	public String getGrupo() {
		return grupo;
	}

	/**
	 * Solo un EquipoInscrito puede representar a un equipo del club.
	 */
	public abstract boolean representa(Equipo equipo);

	/**
	 * Jugadores del club que pueden jugar por este participante.
	 */
	public abstract List<JugadorEquipo> getNomina();

	public boolean estaEnNomina(JugadorEquipo jugador) {
		return getNomina().contains(jugador);
	}

	@Override
	public String toString() {
		return nombre + (grupo != null ? " (grupo " + grupo + ")" : "");
	}
}
