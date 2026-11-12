package consola.deportes;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import excepciones.ReglaDeportivaException;
import logica.GestorCompetencias;
import logica.GestorDeportes;
import modelo.competencias.Competencia;
import modelo.competencias.FilaTabla;
import modelo.competencias.GruposEliminacion;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.Partido;

/**
 * Menú de deportes y competencias para el socio: su información deportiva (RF1)
 * y las tablas y clasificaciones de las competencias (RF2).
 */
public class MenuDeportesSocio extends Menu {

	private GestorDeportes gestorDeportes;
	private GestorCompetencias gestorCompetencias;

	public MenuDeportesSocio(Consola consola) {
		super(consola);
		gestorDeportes = new GestorDeportes(club);
		gestorCompetencias = new GestorCompetencias(club);
	}

	@Override
	public String getTitulo() {
		return "Deportes y competencias";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver mi información deportiva");
		opciones.add("Ver la tabla de posiciones de una competencia");
		opciones.add("Ver goleadores y jugadores destacados de una competencia");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verInformacion();
		} else if (opcion == 2) {
			verTabla();
		} else if (opcion == 3) {
			verClasificaciones();
		}
	}

	// RF1
	private void verInformacion() throws ClubException {
		List<JugadorEquipo> participaciones = gestorDeportes.consultarInformacion(usuario);
		if (participaciones.isEmpty()) {
			System.out.println("No está inscrito en ningún equipo.");
			return;
		}
		for (JugadorEquipo participacion : participaciones) {
			System.out.println(participacion.getEquipo() + ", " + participacion.getPosicion() + " #"
					+ participacion.getNumeroCamiseta() + (participacion.estaActivo() ? "" : " (retirado)"));
			for (Partido partido : gestorDeportes.consultarPartidosDe(usuario, participacion)) {
				Estadistica estadistica = partido.getEstadisticaDe(participacion);
				System.out.println("   " + partido + " | " + (estadistica == null ? "no jugó" : estadistica));
			}
			Suspension suspension = participacion.getSuspensionPendiente();
			if (suspension != null) {
				System.out.println("   Suspendido: " + suspension);
			}
		}
	}

	// RF2: tabla
	private void verTabla() throws ClubException {
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
	private void verClasificaciones() throws ClubException {
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

	private Competencia elegirCompetencia() {
		String deporte = consola.leerTextoOpcional("Filtrar por deporte");
		List<Competencia> competencias = gestorCompetencias.consultarCompetencias(usuario,
				deporte.isEmpty() ? null : deporte, null);
		if (competencias.isEmpty()) {
			System.out.println("No hay competencias con ese filtro (sin información disponible).");
			return null;
		}
		return consola.elegir("Competencia", competencias);
	}
}
