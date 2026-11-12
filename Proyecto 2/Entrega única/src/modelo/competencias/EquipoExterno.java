package modelo.competencias;

import java.util.ArrayList;
import java.util.List;

import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;

/**
 * Rival de otra institución. No se registran sus jugadores.
 */
public class EquipoExterno extends ParticipanteCompetencia {

	private String institucion;

	public EquipoExterno(String nombre, String institucion, String grupo) {
		super(nombre, grupo);
		this.institucion = institucion;
	}

	public String getInstitucion() {
		return institucion;
	}

	@Override
	public boolean representa(Equipo equipo) {
		return false;
	}

	// De los externos no se registran jugadores.
	@Override
	public List<JugadorEquipo> getNomina() {
		return new ArrayList<JugadorEquipo>();
	}
}
