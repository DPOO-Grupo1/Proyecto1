package modelo.academias;

import java.util.ArrayList;
import java.util.List;

import modelo.usuarios.Socio;

/**
 * Un socio inscrito en una disciplina, con su nivel y su ELO.
 * El ELO es independiente en cada disciplina.
 */
public class Inscripcion {

	public static final int ELO_INICIAL = 1000;

	/**
	 * Cuánto puede moverse el ELO en un partido. 32 es el valor clásico del sistema
	 * ELO.
	 */
	public static final int FACTOR_K = 32;

	private Socio socio;
	private Disciplina disciplina;
	private Nivel nivel;
	private int puntajeELO;
	private List<PartidoPractica> partidos;

	public Inscripcion(Socio socio, Disciplina disciplina, Nivel nivel, int puntajeELO) {
		this.socio = socio;
		this.disciplina = disciplina;
		this.nivel = nivel;
		this.puntajeELO = puntajeELO;
		this.partidos = new ArrayList<PartidoPractica>();
	}

	public Socio getSocio() {
		return socio;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public int getPuntajeELO() {
		return puntajeELO;
	}

	/**
	 * Fórmula ELO: ganarle a alguien con más ELO da más puntos que ganarle a
	 * alguien con menos.
	 * esperado = probabilidad de ganar según la diferencia de ELO (entre 0 y 1).
	 * nuevo = actual + K * (resultado - esperado), con resultado 1 si ganó y 0 si
	 * perdió.
	 * actual es el puntaje que tenía antes de jugar el partido, y K representa lo
	 * máximo que un jugador puede ganar o perder en un solo partido.
	 */
	public void actualizarELO(int eloRival, boolean gano) {
		double esperado = 1.0 / (1.0 + Math.pow(10, (eloRival - puntajeELO) / 400.0));
		double resultado = gano ? 1.0 : 0.0;
		puntajeELO = (int) Math.round(puntajeELO + FACTOR_K * (resultado - esperado));
	}

	public List<PartidoPractica> getPartidos() {
		return partidos;
	}

	public void agregarPartido(PartidoPractica partido) {
		partidos.add(partido);
	}

	@Override
	public String toString() {
		return disciplina.getNombre() + ": " + nivel + ", ELO " + puntajeELO;
	}
}
