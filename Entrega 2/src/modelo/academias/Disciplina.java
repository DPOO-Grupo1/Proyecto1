package modelo.academias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Academia individual (tenis, pádel, tenis de mesa).
 * El cupo es cuántas instalaciones de la disciplina se pueden usar a la vez.
 */
public class Disciplina {

	private String nombre;
	private int cupoInstalaciones;
	private List<Inscripcion> inscripciones;

	public Disciplina(String nombre, int cupoInstalaciones) {
		this.nombre = nombre;
		this.cupoInstalaciones = cupoInstalaciones;
		this.inscripciones = new ArrayList<Inscripcion>();
	}

	public String getNombre() {
		return nombre;
	}

	public int getCupoInstalaciones() {
		return cupoInstalaciones;
	}

	public List<Inscripcion> getInscripciones() {
		return inscripciones;
	}

	public void agregarInscripcion(Inscripcion inscripcion) {
		inscripciones.add(inscripcion);
	}

	/**
	 * Inscritos de mayor a menor ELO Devuelve una copia para no desordenar la lista
	 * original.
	 */
	public List<Inscripcion> getRanking() {
		List<Inscripcion> ranking = new ArrayList<Inscripcion>(inscripciones);
		Collections.sort(ranking, new Comparator<Inscripcion>() {
			@Override
			public int compare(Inscripcion a, Inscripcion b) {
				return b.getPuntajeELO() - a.getPuntajeELO();
			}
		});
		return ranking;
	}

	@Override
	public String toString() {
		return nombre;
	}
}
