package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.ClubException;
import excepciones.DatoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorCompetencias;
import logica.GestorDeportes;
import modelo.competencias.Competencia;
import modelo.competencias.FilaClasificacion;
import modelo.competencias.FilaTabla;
import modelo.competencias.GruposEliminacion;
import modelo.competencias.Liga;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.Partido;
import modelo.partidos.PartidoCompetencia;
import modelo.usuarios.Usuario;
import persistencia.CentralPersistencia;

/**
 * Programa de prueba PP2: competencias y posiciones (RF1, RF2, RF25, RF26, RF27).
 * Carga los datos de la carpeta datos/ (o la que se pase como argumento), corre los escenarios
 * de la sección 4.3 del análisis y muestra el estado final. No guarda nada, así se puede repetir.
 */
public class ConsolaPP2 {

	private static Club club;
	private static GestorCompetencias gc;
	private static GestorDeportes gd;
	private static int correctos = 0;
	private static int fallidos = 0;

	public static void main(String[] args) {
		String carpeta = "datos";
		if (args.length > 0) {
			carpeta = args[0];
		}
		System.out.println("=== PP2: Competencias y posiciones ===");
		System.out.println("Cargando los datos de " + carpeta + "/ ...");
		try {
			club = new CentralPersistencia().cargarClub(carpeta);
		} catch (PersistenciaException e) {
			System.out.println("No se pudieron cargar los datos: " + e.getMessage());
			return;
		}
		club.setFechaActual(LocalDate.of(2026, 10, 5));
		gc = new GestorCompetencias(club);
		gd = new GestorDeportes(club);
		System.out.println("Fecha de referencia: " + club.getFechaActual());

		try {
			Usuario admin = club.buscarUsuario("admin");
			Usuario s1 = club.buscarUsuario("s1");
			Usuario j1 = club.buscarUsuario("j1");

			escenariosRF25(admin);
			Competencia comp1 = club.buscarCompetencia("Copa Interclubes", 2026);
			Competencia comp2 = club.buscarCompetencia("ASCUN Baloncesto", 2026);
			escenariosRF26(admin, comp1, comp2);
			escenariosRF27(admin, j1, comp1, comp2);
			escenariosRF2(admin, s1, comp1, comp2);
			escenariosRF1(j1);
			mostrarEstado();
		} catch (ClubException e) {
			System.out.println("Faltan datos para correr los escenarios: " + e.getMessage());
			return;
		}

		System.out.println("\nResultado: " + correctos + " casos bien, " + fallidos + " con error.");
	}

	// RF25: crear una competencia
	private static void escenariosRF25(Usuario admin) throws ClubException {
		titulo("RF25 - Crear una competencia");
		DeporteEquipo futbol = club.buscarDeporte("Fútbol");
		DeporteEquipo baloncesto = club.buscarDeporte("Baloncesto");
		Categoria mayores = club.buscarCategoria("Mayores");

		try {
			Competencia comp1 = gc.crearCompetencia(admin, "Copa Interclubes", "Liga Bogotá", 2026, futbol, mayores, 11, 3, new Liga(false));
			gc.agregarRival(admin, comp1, "Universidad Norte", "Universidad del Norte", null);
			gc.agregarRival(admin, comp1, "Universidad Sur", "Universidad del Sur", null);
			aceptado("EP25.1 (a)", comp1 + ", nómina mínima " + comp1.getNominaMinima() + ", "
					+ comp1.getAmarillasParaSuspension() + " amarillas para suspensión, rivales " + comp1.getParticipantes());
		} catch (ClubException e) {
			inesperado("EP25.1 (a)", e);
		}

		try {
			Competencia comp2 = gc.crearCompetencia(admin, "ASCUN Baloncesto", "ASCUN", 2026, baloncesto, mayores, 5, 3, new GruposEliminacion(2, 2));
			gc.agregarRival(admin, comp2, "UniAndes Basket", "Universidad de los Andes", "A");
			gc.agregarRival(admin, comp2, "Javeriana Basket", "Pontificia Universidad Javeriana", "A");
			gc.agregarRival(admin, comp2, "Nacional Basket", "Universidad Nacional", "B");
			gc.agregarRival(admin, comp2, "Rosario Basket", "Universidad del Rosario", "B");
			aceptado("EP25.1 (b)", comp2 + ", rivales " + comp2.getParticipantes());
		} catch (ClubException e) {
			inesperado("EP25.1 (b)", e);
		}
	}

	// RF26: inscribir un equipo en una competencia
	private static void escenariosRF26(Usuario admin, Competencia comp1, Competencia comp2) throws ClubException {
		titulo("RF26 - Inscribir un equipo en una competencia");

		try {
			gc.inscribirEquipo(admin, comp1, "Pumas Sub-17", new ArrayList<Integer>(), null);
			noRechazado("EP26.2 (a)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP26.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP26.2 (a)", e);
		}

		try {
			gc.inscribirEquipo(admin, comp1, "Titanes Basket", numeros(20, 26), null);
			noRechazado("EP26.2 (b)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP26.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP26.2 (b)", e);
		}

		// Lobos FC tiene 12 jugadores; 2 quedaron suspendidos en su último partido interno (ver datos/deportes.json).
		try {
			gc.inscribirEquipo(admin, comp1, "Lobos FC", numeros(1, 12), null);
			noRechazado("EP26.2 (c)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP26.2 (c)", e);
		} catch (ClubException e) {
			inesperado("EP26.2 (c)", e);
		}

		// El #7 es J2 en Titanes; en Águilas no existe.
		List<Integer> conJ2 = numerosAguilas();
		conJ2.remove(Integer.valueOf(16));
		conJ2.add(7);
		try {
			gc.inscribirEquipo(admin, comp1, "Águilas FC", conJ2, null);
			noRechazado("EP26.2 (d)");
		} catch (NoEncontradoException e) {
			rechazado("EP26.2 (d)", e);
		} catch (ClubException e) {
			inesperado("EP26.2 (d)", e);
		}

		try {
			aceptado("EP26.1", "nómina de " + gc.inscribirEquipo(admin, comp1, "Águilas FC", numerosAguilas(), null).getNomina().size()
					+ " jugadores de Águilas FC en " + comp1.getNombre());
		} catch (ClubException e) {
			inesperado("EP26.1", e);
		}

		// Para los escenarios de COMP2: Titanes en el grupo A.
		List<Integer> nominaTitanes = numeros(20, 26);
		nominaTitanes.add(7);
		gc.inscribirEquipo(admin, comp2, "Titanes Basket", nominaTitanes, "A");
		System.out.println("   Titanes Basket se inscribe en " + comp2.getNombre() + " (grupo A)");
	}

	// RF27: programar y registrar partidos de competencia
	private static void escenariosRF27(Usuario admin, Usuario j1, Competencia comp1, Competencia comp2) throws ClubException {
		titulo("RF27 - Programar y registrar partidos de competencia");
		Equipo aguilas = club.buscarEquipo("Águilas FC");
		JugadorEquipo jugadorJ1 = aguilas.buscarJugador(10);
		JugadorEquipo jugadorJ9 = aguilas.buscarJugador(9);

		try {
			PartidoCompetencia p = gc.programarPartido(admin, comp1, "Águilas FC", "Universidad Norte", LocalDate.of(2026, 10, 10), "Estadio El Campín", "Fecha 1");
			gc.registrarResultado(admin, p, 2, 1);
			gc.registrarEstadistica(admin, p, jugadorJ1, new EstadisticaFutbol(jugadorJ1, 1, 0, 1, 0));
			gc.registrarEstadistica(admin, p, jugadorJ9, new EstadisticaFutbol(jugadorJ9, 1, 1, 0, 0));
			aceptado("EP27.1 (a)", p + " (Águilas suma 3 puntos y Universidad Norte 0)");
		} catch (ClubException e) {
			inesperado("EP27.1 (a)", e);
		}

		try {
			PartidoCompetencia p = gc.programarPartido(admin, comp1, "Águilas FC", "Universidad Sur", LocalDate.of(2026, 10, 17), "Estadio El Campín", "Fecha 2");
			gc.registrarResultado(admin, p, 1, 1);
			gc.registrarEstadistica(admin, p, jugadorJ1, new EstadisticaFutbol(jugadorJ1, 1, 0, 1, 0));
			aceptado("EP27.1 (b)", p + " (1 punto cada uno)");
		} catch (ClubException e) {
			inesperado("EP27.1 (b)", e);
		}

		try {
			PartidoCompetencia p = gc.programarPartido(admin, comp1, "Universidad Norte", "Universidad Sur", LocalDate.of(2026, 10, 17), "Estadio Norte", "Fecha 2");
			gc.registrarResultado(admin, p, 3, 0);
			aceptado("EP27.1 (c)", p + " (es entre dos externos y también cuenta en la tabla)");
		} catch (ClubException e) {
			inesperado("EP27.1 (c)", e);
		}

		try {
			PartidoCompetencia p = gc.programarPartido(admin, comp1, "Universidad Sur", "Águilas FC", LocalDate.of(2026, 10, 24), "Estadio Sur", "Fecha 3");
			gc.registrarResultado(admin, p, 0, 2);
			gc.registrarEstadistica(admin, p, jugadorJ1, new EstadisticaFutbol(jugadorJ1, 1, 1, 1, 0));
			aceptado("EP27.1 (d)", "J1 llega a 3 amarillas y queda suspendido: " + jugadorJ1.getSuspensionPendiente());
		} catch (ClubException e) {
			inesperado("EP27.1 (d)", e);
		}

		// Partidos de COMP2 (grupo A y B)
		PartidoCompetencia andesJaveriana = gc.programarPartido(admin, comp2, "UniAndes Basket", "Javeriana Basket", LocalDate.of(2026, 10, 11), "Coliseo", "Grupos");
		try {
			gc.registrarResultado(admin, andesJaveriana, 70, 70);
			noRechazado("EP27.2 (a)");
		} catch (ReglaDeportivaException e) {
			rechazado("EP27.2 (a)", e);
		} catch (ClubException e) {
			inesperado("EP27.2 (a)", e);
		}
		gc.registrarResultado(admin, andesJaveriana, 80, 60);
		PartidoCompetencia titanesAndes = gc.programarPartido(admin, comp2, "Titanes Basket", "UniAndes Basket", LocalDate.of(2026, 10, 18), "Coliseo", "Grupos");
		gc.registrarResultado(admin, titanesAndes, 70, 65);
		JugadorEquipo j2 = club.buscarEquipo("Titanes Basket").buscarJugador(7);
		JugadorEquipo b1 = club.buscarEquipo("Titanes Basket").buscarJugador(20);
		gc.registrarEstadistica(admin, titanesAndes, j2, new EstadisticaBaloncesto(j2, 25, 8));
		gc.registrarEstadistica(admin, titanesAndes, b1, new EstadisticaBaloncesto(b1, 12, 4));
		gc.registrarResultado(admin, gc.programarPartido(admin, comp2, "Nacional Basket", "Rosario Basket", LocalDate.of(2026, 10, 11), "Coliseo", "Grupos"), 60, 62);
		System.out.println("   Se juegan en COMP2: UniAndes 80-60 Javeriana, Titanes 70-65 UniAndes, Nacional 60-62 Rosario");

		// S8 entra a Águilas después de la inscripción, así que no está en la nómina.
		JugadorEquipo s8 = gd.inscribirJugador(admin, "S8", "Águilas FC", 4, "defensa");
		PartidoCompetencia fecha4 = gc.programarPartido(admin, comp1, "Águilas FC", "Universidad Norte", LocalDate.of(2026, 10, 31), "Estadio El Campín", "Fecha 4");
		try {
			gc.registrarEstadistica(admin, fecha4, s8, new EstadisticaFutbol(s8, 0, 0, 0, 0));
			noRechazado("EP27.2 (b)");
		} catch (JugadorNoHabilitadoException e) {
			rechazado("EP27.2 (b)", e);
		} catch (ClubException e) {
			inesperado("EP27.2 (b)", e);
		}

		try {
			gc.registrarResultado(admin, fecha4, -1, 2);
			noRechazado("EP27.2 (c)");
		} catch (DatoInvalidoException e) {
			rechazado("EP27.2 (c)", e);
		} catch (ClubException e) {
			inesperado("EP27.2 (c)", e);
		}

		try {
			gc.registrarEstadistica(admin, fecha4, jugadorJ1, new EstadisticaFutbol(jugadorJ1, 0, 0, 0, 0));
			noRechazado("EP27.2 (d)");
		} catch (JugadorNoHabilitadoException e) {
			rechazado("EP27.2 (d)", e);
		} catch (ClubException e) {
			inesperado("EP27.2 (d)", e);
		}
	}

	// RF2: tabla de posiciones y clasificaciones
	private static void escenariosRF2(Usuario admin, Usuario s1, Competencia comp1, Competencia comp2) throws ClubException {
		titulo("RF2 - Tabla de posiciones y clasificaciones");

		try {
			aceptado("EP2.1 (a)", "tabla de " + comp1.getNombre() + " consultada por S1:");
			imprimirTabla(gc.consultarTabla(s1, comp1, null));
		} catch (ClubException e) {
			inesperado("EP2.1 (a)", e);
		}

		try {
			List<FilaTabla> grupoA = gc.consultarTabla(s1, comp2, "A");
			aceptado("EP2.1 (b)", grupoA.get(0).getParticipante().getNombre() + " y " + grupoA.get(1).getParticipante().getNombre()
					+ " empatan a puntos; queda arriba " + grupoA.get(0).getParticipante().getNombre() + " porque ganó el partido entre ellos");
			aceptado("EP2.1 (c)", "grupo A de " + comp2.getNombre() + ":");
			imprimirTabla(grupoA);
			System.out.println("     Clasificados: " + gc.consultarClasificados(s1, comp2));
		} catch (ClubException e) {
			inesperado("EP2.1 (b) / (c)", e);
		}

		try {
			aceptado("EP2.1 (d)", "goleadores de " + comp1.getNombre() + ": " + gc.consultarGoleadores(s1, comp1));
			aceptado("EP2.1 (d)", "destacados de " + comp1.getNombre() + " (asistencias): " + gc.consultarDestacados(s1, comp1));
			aceptado("EP2.1 (d)", "destacados de " + comp2.getNombre() + " (puntos): " + gc.consultarDestacados(s1, comp2));
		} catch (ClubException e) {
			inesperado("EP2.1 (d)", e);
		}

		aceptado("EP2.1 (e)", "competencias de baloncesto: " + gc.consultarCompetencias(s1, "Baloncesto", null));

		try {
			gc.consultarCompetencia(s1, "Copa Fantasma", 2026);
			noRechazado("EP2.2");
		} catch (NoEncontradoException e) {
			aceptado("EP2.2", "Copa Fantasma: sin información disponible");
		}
		try {
			Competencia cerros = gc.crearCompetencia(admin, "Torneo de los Cerros", "Cerros", 2026, club.buscarDeporte("Fútbol"),
					club.buscarCategoria("Mayores"), 11, 3, new Liga(true));
			if (sinResultados(gc.consultarTabla(s1, cerros, null))) {
				aceptado("EP2.2", cerros.getNombre() + ": sin información disponible");
			} else {
				noRechazado("EP2.2 (sin resultados)");
			}
		} catch (ClubException e) {
			inesperado("EP2.2", e);
		}
	}

	// RF1: información deportiva del socio
	private static void escenariosRF1(Usuario j1) {
		titulo("RF1 - Información deportiva del socio");
		try {
			List<JugadorEquipo> participaciones = gd.consultarInformacion(j1);
			aceptado("EP1.1 (a)", "información deportiva de " + participaciones.get(0).getSocio().getNombreCompleto() + ":");
			for (JugadorEquipo participacion : participaciones) {
				System.out.println("     " + participacion.getEquipo() + ", " + participacion.getPosicion() + " #" + participacion.getNumeroCamiseta());
				for (Partido partido : gd.consultarPartidosDe(j1, participacion)) {
					Estadistica estadistica = partido.getEstadisticaDe(participacion);
					System.out.println("        " + partido + " | " + (estadistica == null ? "no jugó" : estadistica));
				}
				Suspension suspension = participacion.getSuspensionPendiente();
				if (suspension != null) {
					aceptado("EP1.1 (b)", "suspendido por " + suspension.getMotivo() + ", desde el partido #" + suspension.getOrigen().getId()
							+ "; se cumple en el siguiente partido de " + ((PartidoCompetencia) suspension.getOrigen()).getCompetencia().getNombre());
				}
			}
		} catch (ClubException e) {
			inesperado("EP1.1", e);
		}
	}

	// Estado final
	private static void mostrarEstado() throws ClubException {
		titulo("Estado final");
		for (Competencia competencia : club.getCompetencias()) {
			System.out.println(competencia + ", " + competencia.getParticipantes().size() + " participantes, "
					+ competencia.getPartidos().size() + " partidos");
			for (PartidoCompetencia partido : competencia.getPartidos()) {
				System.out.println("     " + partido + (partido.isJugado() ? "" : " (pendiente)"));
			}
			for (FilaClasificacion fila : gc.consultarDestacados(null, competencia)) {
				System.out.println("     Destacado: " + fila);
			}
		}
		System.out.println("\nSuspensiones pendientes de jugadores del club:");
		for (Equipo equipo : club.getEquipos()) {
			for (JugadorEquipo jugador : equipo.getJugadores()) {
				if (jugador.getSuspensionPendiente() != null) {
					System.out.println("  " + jugador + ": " + jugador.getSuspensionPendiente());
				}
			}
		}
	}

	// Auxiliares
	private static List<Integer> numeros(int desde, int hasta) {
		List<Integer> lista = new ArrayList<Integer>();
		for (int i = desde; i <= hasta; i++) {
			lista.add(i);
		}
		return lista;
	}

	// Los 12 números de Águilas FC en datos/deportes.json.
	private static List<Integer> numerosAguilas() {
		int[] camisetas = { 10, 2, 3, 5, 6, 8, 9, 11, 13, 14, 15, 16 };
		List<Integer> lista = new ArrayList<Integer>();
		for (int camiseta : camisetas) {
			lista.add(camiseta);
		}
		return lista;
	}

	private static boolean sinResultados(List<FilaTabla> tabla) {
		for (FilaTabla fila : tabla) {
			if (fila.getJugados() > 0) {
				return false;
			}
		}
		return true;
	}

	private static void imprimirTabla(List<FilaTabla> tabla) {
		int posicion = 1;
		for (FilaTabla fila : tabla) {
			System.out.println("     " + posicion + ". " + fila);
			posicion++;
		}
	}

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
