package modelo.deportes;

import modelo.partidos.Estadistica;

/**
 * Deporte de conjunto (fútbol, baloncesto, voleibol).
 * Sus reglas (puntos, empates, mínimo de habilitados) son atributos.
 */
public class DeporteEquipo {

	private String nombre;
	private int minimoHabilitados;
	private int puntosVictoria;
	private int puntosEmpate;
	private int puntosDerrota;
	private boolean admiteEmpate;
	private boolean marcadorPorSets;

	public DeporteEquipo(String nombre, int minimoHabilitados, int puntosVictoria, int puntosEmpate,
			int puntosDerrota, boolean admiteEmpate, boolean marcadorPorSets) {
		this.nombre = nombre;
		this.minimoHabilitados = minimoHabilitados;
		this.puntosVictoria = puntosVictoria;
		this.puntosEmpate = puntosEmpate;
		this.puntosDerrota = puntosDerrota;
		this.admiteEmpate = admiteEmpate;
		this.marcadorPorSets = marcadorPorSets;
	}

	public String getNombre() {
		return nombre;
	}

	public int getMinimoHabilitados() {
		return minimoHabilitados;
	}

	public int getPuntosVictoria() {
		return puntosVictoria;
	}

	public int getPuntosEmpate() {
		return puntosEmpate;
	}

	public int getPuntosDerrota() {
		return puntosDerrota;
	}

	public boolean isAdmiteEmpate() {
		return admiteEmpate;
	}

	public boolean isMarcadorPorSets() {
		return marcadorPorSets;
	}

	/**
	 * Puntos que da un resultado según el marcador propio y el del rival.
	 */
	public int puntosPara(int propios, int rival) {
		if (propios > rival) {
			return puntosVictoria;
		}
		if (propios == rival) {
			return puntosEmpate;
		}
		return puntosDerrota;
	}

	/**
	 * Revisa que la estadística sea de este deporte.
	 */
	public boolean admiteEstadistica(Estadistica estadistica) {
		return nombre.equalsIgnoreCase(estadistica.getNombreDeporte());
	}

	@Override
	public String toString() {
		return nombre;
	}
}
