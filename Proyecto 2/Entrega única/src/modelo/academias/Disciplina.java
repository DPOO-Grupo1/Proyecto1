package modelo.academias;

import java.util.ArrayList;
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
	 * Inscritos de mayor a menor ELO. Es una lista nueva, así no se desordena la original.
	 */
	public List<Inscripcion> getRanking() {
		List<Inscripcion> ranking = new ArrayList<Inscripcion>();
		// Ordenamiento por inserción: cada inscrito se pone después de los que tienen igual o más ELO
		for (Inscripcion inscripcion : inscripciones) {
			int i = 0;
			while (i < ranking.size() && ranking.get(i).getPuntajeELO() >= inscripcion.getPuntajeELO()) {
				i++;
			}
			ranking.add(i, inscripcion);
		}
		return ranking;
	}

	/**
	 * Valida si la disciplina admite el tipo de estadística registrado (RF10, EP10.2a).
	 * Tenis y Pádel admiten EstadisticaTenisPadel; Tenis de mesa admite EstadisticaTenisMesa.
	 */
	public boolean admiteEstadistica(EstadisticaPractica estadistica) {
		if (estadistica == null) {
			return false;
		}
		if (nombre.equalsIgnoreCase("Tenis") || nombre.equalsIgnoreCase("Pádel") || nombre.equalsIgnoreCase("Padel")) {
			return estadistica instanceof EstadisticaTenisPadel;
		}
		if (nombre.equalsIgnoreCase("Tenis de mesa")) {
			return estadistica instanceof EstadisticaTenisMesa;
		}
		return false;
	}

	@Override
	public String toString() {
		return nombre;
	}
}
