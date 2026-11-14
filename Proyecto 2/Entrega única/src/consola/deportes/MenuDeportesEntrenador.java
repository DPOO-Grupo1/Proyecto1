package consola.deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorDeportes;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.partidos.Estadistica;
import modelo.partidos.PartidoInterno;

/**
 * Equipos y partidos internos para el entrenador: ver sus equipos, programar partidos
 * internos y registrar resultados, estadísticas y tarjetas (RF12, RF13 y RF14).
 */
public class MenuDeportesEntrenador extends Menu {

	private GestorDeportes gestor;
	private LectorEstadistica lector;

	public MenuDeportesEntrenador(Consola consola) {
		super(consola);
		gestor = new GestorDeportes(club);
		lector = new LectorEstadistica(consola);
	}

	@Override
	public String getTitulo() {
		return "Equipos y partidos internos";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver mis equipos y sus jugadores");
		opciones.add("Ver los partidos internos");
		opciones.add("Programar un partido interno");
		opciones.add("Registrar el resultado de un partido interno");
		opciones.add("Registrar la estadística de un jugador");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verMisEquipos();
		} else if (opcion == 2) {
			consola.mostrarLista("Partidos internos:", club.getPartidosInternos());
		} else if (opcion == 3) {
			programarPartido();
		} else if (opcion == 4) {
			registrarResultado();
		} else if (opcion == 5) {
			registrarEstadistica();
		}
	}

	private void verMisEquipos() {
		boolean tiene = false;
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.getEntrenador() == usuario) {
				MenuEquiposAdministrador.mostrarEquipo(equipo);
				tiene = true;
			}
		}
		if (!tiene) {
			System.out.println("No tiene equipos asignados (sin información disponible).");
		}
	}

	// RF12
	private void programarPartido() throws ClubException {
		List<Equipo> activos = new ArrayList<Equipo>();
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.estaActivo()) {
				activos.add(equipo);
			}
		}
		Equipo local = consola.elegir("Equipo local", activos);
		if (local == null) {
			return;
		}
		List<Equipo> rivales = new ArrayList<Equipo>();
		for (Equipo equipo : activos) {
			if (equipo != local && equipo.getDeporte() == local.getDeporte()
					&& equipo.getCategoria() == local.getCategoria()) {
				rivales.add(equipo);
			}
		}
		Equipo visitante = consola.elegir("Equipo visitante (mismo deporte y categoría)", rivales);
		if (visitante == null) {
			return;
		}
		LocalDate fecha = consola.leerFecha("Fecha del partido");
		String proposito = consola.leerTexto("Propósito (práctica, evaluación...)");
		PartidoInterno partido = gestor.programarPartidoInterno(usuario, local.getNombre(), visitante.getNombre(),
				fecha, proposito);
		System.out.println("ACEPTADO: " + partido);
	}

	// RF13
	private void registrarResultado() throws ClubException {
		List<PartidoInterno> pendientes = new ArrayList<PartidoInterno>();
		for (PartidoInterno partido : club.getPartidosInternos()) {
			if (!partido.isJugado()) {
				pendientes.add(partido);
			}
		}
		PartidoInterno partido = consola.elegir("Partido", pendientes);
		if (partido == null) {
			return;
		}
		String unidad = partido.getLocal().getDeporte().isMarcadorPorSets() ? " (sets)" : "";
		int local = consola.leerEntero("Marcador de " + partido.getLocal().getNombre() + unidad, 0, 999);
		int visitante = consola.leerEntero("Marcador de " + partido.getVisitante().getNombre() + unidad, 0, 999);
		gestor.registrarResultadoInterno(usuario, partido, local, visitante);
		System.out.println("ACEPTADO: " + partido);
	}

	// RF13 y RF14
	private void registrarEstadistica() throws ClubException {
		PartidoInterno partido = consola.elegir("Partido", club.getPartidosInternos());
		if (partido == null) {
			return;
		}
		List<JugadorEquipo> todos = new ArrayList<JugadorEquipo>();
		todos.addAll(partido.getLocal().getJugadoresActivos());
		todos.addAll(partido.getVisitante().getJugadoresActivos());
		JugadorEquipo jugador = consola.elegir("Jugador habilitado", LectorEstadistica.habilitados(todos, partido));
		if (jugador == null) {
			return;
		}
		Estadistica estadistica = lector.leer(jugador);
		if (estadistica == null) {
			return;
		}
		gestor.registrarEstadistica(usuario, partido, jugador, estadistica);
		System.out.println("ACEPTADO: " + estadistica);
		if (jugador.getSuspensionPendiente() != null) {
			System.out.println("Queda suspendido: " + jugador.getSuspensionPendiente());
		}
	}
}
