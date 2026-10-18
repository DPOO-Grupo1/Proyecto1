package pruebas;

import java.time.LocalDate;

import excepciones.ClubException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.PersistenciaException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorDeportes;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.EstadisticaVoleibol;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Usuario;
import persistencia.CentralPersistencia;

/**
 * Programa de prueba PP1: gestión deportiva y partidos internos (RF12, RF13, RF14, RF22, RF23, RF24).
 * Carga los datos de la carpeta datos/ (o la que se pase como argumento), corre los escenarios
 * de la sección 4.3 del análisis y muestra el estado final. No guarda nada, así se puede repetir.
 */
public class ConsolaPP1 {

	private static Club club;
	private static GestorDeportes gestor;
	private static int correctos = 0;
	private static int fallidos = 0;

	public static void main(String[] args) {
		String carpeta = "datos";
		if (args.length > 0) {
			carpeta = args[0];
		}
		System.out.println("=== PP1: Gestión deportiva y partidos internos ===");
		System.out.println("Cargando los datos de " + carpeta + "/ ...");
		try {
			club = new CentralPersistencia().cargarClub(carpeta);
		} catch (PersistenciaException e) {
			System.out.println("No se pudieron cargar los datos: " + e.getMessage());
			return;
		}
		club.setFechaActual(LocalDate.of(2026, 10, 5));
		gestor = new GestorDeportes(club);
		System.out.println("Fecha de referencia: " + club.getFechaActual());

		try {
			Usuario admin = club.buscarUsuario("admin");
			Usuario e1 = club.buscarUsuario("e1");
			Usuario e2 = club.buscarUsuario("e2");
			Usuario e3 = club.buscarUsuario("e3");
			DeporteEquipo futbol = club.buscarDeporte("Fútbol");
			DeporteEquipo voleibol = club.buscarDeporte("Voleibol");
			Categoria mayores = club.buscarCategoria("Mayores");
			Categoria sub15 = club.buscarCategoria("Sub-15");
			Equipo eq1 = club.buscarEquipo("Águilas FC");
			Equipo eq2 = club.buscarEquipo("Titanes Basket");
			Equipo eq3 = club.buscarEquipo("Halcones FC");

			escenariosRF22(admin, futbol, voleibol, mayores, sub15);
			escenariosRF23(admin, eq2);
			PartidoInterno p1 = escenariosRF12(admin, e1, eq3);
			escenariosRF13yRF14(e1, e2, e3, eq1, p1);
			escenariosRF24(admin, e1, eq1, eq3);
		} catch (ClubException e) {
			// Los datos que se usan para preparar los escenarios deberían existir en datos/
			System.out.println("Faltan datos para correr los escenarios: " + e.getMessage());
			return;
		}

		mostrarEstado();
		System.out.println("\nResultado: " + correctos + " casos bien, " + fallidos + " con error.");
	}

	// RF22: crear un equipo
	private static void escenariosRF22(Usuario admin, DeporteEquipo futbol, DeporteEquipo voleibol,
			Categoria mayores, Categoria sub15) {
		titulo("RF22 - Crear un equipo");
		try {
			Equipo leones = gestor.crearEquipo(admin, "Leones", voleibol, sub15, 14);
			aceptado("EP22.1", leones + ", cupo " + leones.getCupoMaximo() + ", " + leones.getJugadores().size() + " jugadores");
		} catch (ClubException e) {
			inesperado("EP22.1", e);
		}

		try {
			gestor.crearEquipo(admin, "Águilas FC", futbol, mayores, 22);
			noRechazado("EP22.2 (a)");
		} catch (DuplicadoException e) {
			rechazado("EP22.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP22.2 (a)", e);
		}

		try {
			gestor.crearEquipo(admin, "Pumas FC", futbol, mayores, 6);
			noRechazado("EP22.2 (b)");
		} catch (CupoExcedidoException e) {
			rechazado("EP22.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP22.2 (b)", e);
		}
	}

	// RF23: inscribir un jugador en un equipo
	private static void escenariosRF23(Usuario admin, Equipo eq2) {
		titulo("RF23 - Inscribir un jugador en un equipo");
		try {
			aceptado("EP23.1 (a)", "" + gestor.inscribirJugador(admin, "S8", "Águilas FC", 4, "defensa"));
		} catch (ClubException e) {
			inesperado("EP23.1 (a)", e);
		}
		try {
			aceptado("EP23.1 (b)", "J1 también en baloncesto: " + gestor.inscribirJugador(admin, "J1", "Titanes Basket", 12, "escolta"));
		} catch (ClubException e) {
			inesperado("EP23.1 (b)", e);
		}

		try {
			gestor.inscribirJugador(admin, "J3", "Águilas FC", 17, "volante");
			noRechazado("EP23.2 (a)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP23.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP23.2 (a)", e);
		}

		try {
			gestor.inscribirJugador(admin, "J1", "Halcones FC", 9, "delantero");
			noRechazado("EP23.2 (b)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP23.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP23.2 (b)", e);
		}

		// En el análisis es S8, pero S8 ya entró a Águilas en el EP23.1 (a); se usa S9.
		try {
			gestor.inscribirJugador(admin, "S9", "Águilas FC", 10, "volante");
			noRechazado("EP23.2 (c)");
		} catch (DuplicadoException e) {
			rechazado("EP23.2 (c)", e);
		} catch (ClubException e) {
			inesperado("EP23.2 (c)", e);
		}

		// Se llena Titanes hasta su cupo (15) y luego se intenta uno más.
		try {
			for (int i = 8; i <= 13; i++) {
				gestor.inscribirJugador(admin, "B" + i, "Titanes Basket", 19 + i, "alero");
			}
			System.out.println("   Titanes Basket queda con " + eq2.getJugadoresActivos().size() + " activos (cupo " + eq2.getCupoMaximo() + ")");
		} catch (ClubException e) {
			inesperado("Preparación EP23.2 (d)", e);
		}
		try {
			gestor.inscribirJugador(admin, "B14", "Titanes Basket", 40, "pívot");
			noRechazado("EP23.2 (d)");
		} catch (CupoExcedidoException e) {
			rechazado("EP23.2 (d)", e);
		} catch (ClubException e) {
			inesperado("EP23.2 (d)", e);
		}
	}

	// RF12: programar un partido interno
	private static PartidoInterno escenariosRF12(Usuario admin, Usuario e1, Equipo eq3) {
		titulo("RF12 - Programar un partido interno");
		LocalDate fecha = LocalDate.of(2026, 10, 12);

		try {
			gestor.programarPartidoInterno(e1, "Águilas FC", "Titanes Basket", fecha, "práctica");
			noRechazado("EP12.2 (a)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP12.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP12.2 (a)", e);
		}

		try {
			gestor.programarPartidoInterno(e1, "Águilas FC", "Pumas Sub-17", fecha, "práctica");
			noRechazado("EP12.2 (b)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP12.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP12.2 (b)", e);
		}

		try {
			gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", fecha, "práctica");
			noRechazado("EP12.2 (c)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP12.2 (c)", e);
		} catch (ClubException e) {
			inesperado("EP12.2 (c)", e);
		}

		try {
			gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", LocalDate.of(2026, 9, 20), "práctica");
			noRechazado("EP12.2 (d)");
		} catch (DatoInvalidoException e) {
			rechazado("EP12.2 (d)", e);
		} catch (ClubException e) {
			inesperado("EP12.2 (d)", e);
		}

		// El EP12.1 pide Halcones con 8 habilitados: se inscriben 2 jugadores más.
		PartidoInterno partido = null;
		try {
			gestor.inscribirJugador(admin, "H7", "Halcones FC", 7, "medio");
			gestor.inscribirJugador(admin, "H8", "Halcones FC", 8, "medio");
			System.out.println("   Halcones FC queda con " + eq3.getJugadoresActivos().size() + " jugadores activos");
			partido = gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", fecha, "evaluación");
			aceptado("EP12.1", "" + partido);
		} catch (ClubException e) {
			inesperado("EP12.1", e);
		}
		return partido;
	}

	// RF13 y RF14: resultados, estadísticas, tarjetas y suspensiones
	private static void escenariosRF13yRF14(Usuario e1, Usuario e2, Usuario e3, Equipo eq1, PartidoInterno p1)
			throws ClubException {
		titulo("RF13 y RF14 - Resultados, estadísticas, tarjetas y suspensiones");
		JugadorEquipo j1 = eq1.buscarJugador(10);
		JugadorEquipo j4 = eq1.buscarJugador(2);
		JugadorEquipo j5 = eq1.buscarJugador(3);
		JugadorEquipo j2 = club.buscarEquipo("Titanes Basket").buscarJugador(7);

		try {
			gestor.registrarResultadoInterno(e1, p1, 2, 1);
			gestor.registrarEstadistica(e1, p1, j1, new EstadisticaFutbol(j1, 1, 1, 0, 1));
			aceptado("EP13.1 (a)", p1 + " | " + p1.getEstadisticaDe(j1));
			aceptado("EP14.1 (a)", "roja a J1, queda suspendido: " + j1.getSuspensionPendiente());
		} catch (ClubException e) {
			inesperado("EP13.1 (a) / EP14.1 (a)", e);
		}

		try {
			PartidoInterno voley = gestor.programarPartidoInterno(e3, "Gaviotas Voley", "Panteras Voley", LocalDate.of(2026, 10, 13), "evaluación");
			gestor.registrarResultadoInterno(e3, voley, 3, 1);
			JugadorEquipo v1 = club.buscarEquipo("Gaviotas Voley").buscarJugador(1);
			gestor.registrarEstadistica(e3, voley, v1, new EstadisticaVoleibol(v1, 4, 2));
			aceptado("EP13.1 (b)", voley + " (sets) | " + voley.getEstadisticaDe(v1));
		} catch (ClubException e) {
			inesperado("EP13.1 (b)", e);
		}

		try {
			gestor.registrarEstadistica(e1, p1, j1, new EstadisticaBaloncesto(j1, 0, 5));
			noRechazado("EP13.2 (a)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP13.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP13.2 (a)", e);
		}

		PartidoInterno basket = null;
		try {
			basket = gestor.programarPartidoInterno(e2, "Titanes Basket", "Toros Basket", LocalDate.of(2026, 10, 14), "práctica");
			gestor.registrarResultadoInterno(e2, basket, 80, 80);
			noRechazado("EP13.2 (b)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP13.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP13.2 (b)", e);
		}

		// En el análisis es S8, pero S8 ya está en Águilas (EP23.1 a); se usa J2, de Titanes.
		try {
			gestor.registrarEstadistica(e1, p1, j2, new EstadisticaFutbol(j2, 1, 0, 0, 0));
			noRechazado("EP13.2 (c)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP13.2 (c)", e);
		} catch (ClubException e) {
			inesperado("EP13.2 (c)", e);
		}

		try {
			gestor.registrarEstadistica(e1, p1, j4, new EstadisticaFutbol(j4, 0, 0, 2, 0));
			aceptado("EP14.1 (b)", "2 amarillas a J4, queda suspendido: " + j4.getSuspensionPendiente());
			gestor.registrarEstadistica(e1, p1, j5, new EstadisticaFutbol(j5, 0, 0, 1, 0));
			aceptado("EP14.1 (c)", "1 amarilla a J5, ¿suspendido? " + (j5.getSuspensionPendiente() != null));
		} catch (ClubException e) {
			inesperado("EP14.1 (b) / (c)", e);
		}

		// Siguiente partido de Águilas: J1 y J4 no pueden jugar; J5 sí.
		PartidoInterno p2 = gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", LocalDate.of(2026, 10, 19), "práctica");
		try {
			gestor.registrarEstadistica(e1, p2, j1, new EstadisticaFutbol(j1, 0, 0, 0, 0));
			noRechazado("EP14.2 (a)");
		} catch (JugadorNoHabilitadoException e) {
			rechazado("EP14.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP14.2 (a)", e);
		}
		try {
			gestor.registrarEstadistica(e1, p2, j5, new EstadisticaFutbol(j5, 0, 0, 0, 0));
			aceptado("EP14.1 (c)", "J5 se alinea en el siguiente partido (#" + p2.getId() + ")");
			gestor.registrarResultadoInterno(e1, p2, 1, 0);
			System.out.println("   Se juega el partido #" + p2.getId() + ": " + j1.getSuspensiones().get(0));
		} catch (ClubException e) {
			inesperado("EP14.1 (c)", e);
		}

		try {
			PartidoInterno p3 = gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", LocalDate.of(2026, 10, 26), "práctica");
			gestor.registrarEstadistica(e1, p3, j1, new EstadisticaFutbol(j1, 2, 0, 0, 0));
			gestor.registrarResultadoInterno(e1, p3, 3, 0);
			aceptado("EP14.1 (d)", "suspensión cumplida, J1 juega el partido #" + p3.getId());
		} catch (ClubException e) {
			inesperado("EP14.1 (d)", e);
		}

		try {
			gestor.registrarEstadistica(e2, basket, j2, new EstadisticaFutbol(j2, 0, 0, 1, 0));
			noRechazado("EP14.2 (b)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP14.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP14.2 (b)", e);
		}
	}

	// RF24: dar de baja equipos y desvincular jugadores
	private static void escenariosRF24(Usuario admin, Usuario e1, Equipo eq1, Equipo eq3) {
		titulo("RF24 - Dar de baja equipos y desvincular jugadores");

		// Águilas queda con un partido pendiente (contra Lobos), así que tiene compromisos.
		try {
			PartidoInterno pendiente = gestor.programarPartidoInterno(e1, "Águilas FC", "Lobos FC", LocalDate.of(2026, 11, 2), "práctica");
			System.out.println("   Águilas FC tiene pendiente el " + pendiente);
			gestor.darDeBajaEquipo(admin, "Águilas FC");
			noRechazado("EP24.2 (a)");
		} catch (EstadoInvalidoException e) {
			rechazado("EP24.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP24.2 (a)", e);
		}

		try {
			gestor.darDeBajaEquipo(admin, "Halcones FC");
			aceptado("EP24.1 (a)", "Halcones FC activo: " + eq3.estaActivo() + ", jugadores activos: "
					+ eq3.getJugadoresActivos().size() + ", historial: " + eq3.getJugadores().size() + " jugadores");
		} catch (ClubException e) {
			inesperado("EP24.1 (a)", e);
		}

		try {
			gestor.retirarJugador(admin, "Águilas FC", 10);
			aceptado("EP24.1 (b)", "J1 retirado de Águilas FC, ¿#10 libre? " + eq1.numeroDisponible(10));
		} catch (ClubException e) {
			inesperado("EP24.1 (b)", e);
		}

		try {
			gestor.programarPartidoInterno(e1, "Águilas FC", "Halcones FC", LocalDate.of(2026, 11, 9), "práctica");
			noRechazado("EP24.2 (b)");
		} catch (EstadoInvalidoException e) {
			rechazado("EP24.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP24.2 (b)", e);
		}
	}

	// Estado final
	private static void mostrarEstado() {
		titulo("Estado final");
		for (Equipo equipo : club.getEquipos()) {
			String entrenador = "sin entrenador";
			if (equipo.getEntrenador() != null) {
				entrenador = "entrenador " + equipo.getEntrenador().getNombreCompleto();
			}
			System.out.println(equipo + (equipo.estaActivo() ? "" : " [INACTIVO]") + ", " + entrenador + ", "
					+ equipo.getJugadoresActivos().size() + "/" + equipo.getCupoMaximo() + " activos");
			for (JugadorEquipo jugador : equipo.getJugadores()) {
				Suspension suspension = jugador.getSuspensionPendiente();
				if (!jugador.estaActivo() || suspension != null) {
					System.out.println("     " + jugador + (jugador.estaActivo() ? "" : " [retirado]")
							+ (suspension == null ? "" : " - suspendido: " + suspension));
				}
			}
		}
		System.out.println("\nPartidos internos:");
		for (PartidoInterno partido : club.getPartidosInternos()) {
			System.out.println("  " + partido);
			for (Estadistica estadistica : partido.getEstadisticas()) {
				System.out.println("     " + estadistica);
			}
		}
	}

	// Impresión de resultados
	private static void titulo(String texto) {
		System.out.println("\n--- " + texto + " ---");
	}

	private static void aceptado(String caso, String detalle) {
		correctos++;
		System.out.println(caso + " ACEPTADO: " + detalle);
	}

	private static void rechazado(String caso, ClubException e) {
		correctos++;
		System.out.println(caso + " RECHAZADO: " + e.getMessage());
	}

	private static void noRechazado(String caso) {
		fallidos++;
		System.out.println(caso + " ERROR: debía rechazarse y fue aceptado");
	}

	private static void inesperado(String caso, ClubException e) {
		fallidos++;
		System.out.println(caso + " ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
	}
}
