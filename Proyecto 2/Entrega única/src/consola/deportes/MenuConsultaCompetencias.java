package consola.deportes;

import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import excepciones.ReglaDeportivaException;
import logica.GestorCompetencias;
import modelo.competencias.Competencia;
import modelo.competencias.FilaTabla;
import modelo.competencias.GruposEliminacion;

/**
 * Consultas de competencias que comparten el socio y el administrador (RF2):
 * elegir una competencia, ver su tabla, sus clasificados, goleadores y destacados.
 */
public abstract class MenuConsultaCompetencias extends Menu {

	protected GestorCompetencias gestorCompetencias;

	public MenuConsultaCompetencias(Consola consola) {
		super(consola);
		gestorCompetencias = new GestorCompetencias(club);
	}

	// RF2: tabla y clasificados
	protected void verTabla() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		String grupo = null;
		if (competencia.getFormato() instanceof GruposEliminacion) {
			GruposEliminacion formato = (GruposEliminacion) competencia.getFormato();
			grupo = consola.elegir("Grupo", formato.getGrupos());
			if (grupo == null) {
				return;
			}
		}
		List<FilaTabla> tabla = gestorCompetencias.consultarTabla(usuario, competencia, grupo);
		System.out.println("Tabla de " + competencia + (grupo == null ? "" : ", grupo " + grupo) + ":");
		int posicion = 1;
		for (FilaTabla fila : tabla) {
			System.out.println("   " + posicion + ". " + fila);
			posicion++;
		}
		if (tabla.isEmpty()) {
			System.out.println("   (sin información disponible)");
		}
		consola.mostrarLista("Clasificados:", gestorCompetencias.consultarClasificados(usuario, competencia));
	}

	// RF2: goleadores y destacados
	protected void verClasificaciones() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		try {
			consola.mostrarLista("Goleadores:", gestorCompetencias.consultarGoleadores(usuario, competencia));
		} catch (ReglaDeportivaException e) {
			System.out.println("Goleadores: " + e.getMessage());
		}
		consola.mostrarLista("Jugadores destacados:", gestorCompetencias.consultarDestacados(usuario, competencia));
	}

	/**
	 * Deja filtrar por deporte y categoría para no mostrar todas las competencias. Null si no hay o se cancela.
	 */
	protected Competencia elegirCompetencia() {
		String deporte = consola.leerTextoOpcional("Filtrar por deporte");
		String categoria = consola.leerTextoOpcional("Filtrar por categoría");
		List<Competencia> competencias = gestorCompetencias.consultarCompetencias(usuario,
				deporte.isEmpty() ? null : deporte, categoria.isEmpty() ? null : categoria);
		if (competencias.isEmpty()) {
			System.out.println("No hay competencias con ese filtro (sin información disponible).");
			return null;
		}
		return consola.elegir("Competencia", competencias);
	}
}
