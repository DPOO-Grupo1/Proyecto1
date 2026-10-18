package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorDeportes;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.EstadisticaVoleibol;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP1: Gestión deportiva y partidos internos.
 * Evalúa: RF12, RF13, RF14, RF22, RF23 y RF24.
 * Ejecutable en Eclipse: Clic derecho -> Run As -> Java Application.
 */
public class ConsolaPP1 {

	public static void main(String[] args) {
		System.out.println("=========================================================");
		System.out.println("   INICIO CONSOLA PP1: GESTIÓN DEPORTIVA Y PARTIDOS      ");
		System.out.println("=========================================================");

		try {
			Club club = new Club();
			LocalDate fechaHoy = LocalDate.of(2026, 10, 5);
			club.setFechaActual(fechaHoy);
			GestorDeportes gestor = new GestorDeportes(club);

			// 1. Deportes y Categorías (Sección 4.1)
			DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
			DeporteEquipo basket = new DeporteEquipo("Baloncesto", 5, 2, 0, 1, false, false);
			DeporteEquipo voleibol = new DeporteEquipo("Voleibol", 6, 2, 0, 1, false, true);
			club.agregarDeporte(futbol);
			club.agregarDeporte(basket);
			club.agregarDeporte(voleibol);

			Categoria mayores = new Categoria("Mayores", 18, 99);
			Categoria sub17 = new Categoria("Sub-17", 15, 17);
			Categoria sub15 = new Categoria("Sub-15", 12, 14);
			club.agregarCategoria(mayores);
			club.agregarCategoria(sub17);
			club.agregarCategoria(sub15);

			// 2. Administrador y Entrenadores
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Entrenador e2 = new Entrenador("e2", "pass", "Carlos", "Ramos", 2);
			Entrenador e3 = new Entrenador("e3", "pass", "Andrés", "Ruiz", 3);
			club.agregarUsuario(e1);
			club.agregarUsuario(e2);
			club.agregarUsuario(e3);

			// 3. Socios comunes
			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s8 = new Socio("s8", "pass", "Helena", "Ramírez", "S8", LocalDate.of(1996, 12, 1));
			Socio s9 = new Socio("s9", "pass", "Iván", "Torres", "S9", LocalDate.of(1992, 11, 2));
			club.agregarUsuario(s1);
			club.agregarUsuario(s8);
			club.agregarUsuario(s9);

			// 4. Equipos base (Sección 4.1)
			// EQ1: Águilas FC (fútbol, Mayores, cupo 22, E1, 12 habilitados)
			Equipo eq1 = gestor.crearEquipo(admin, "Águilas FC", futbol, mayores, 22);
			gestor.asignarEntrenador(admin, "Águilas FC", e1.getIdEmpleado());
			List<Socio> sociosEQ1 = new ArrayList<Socio>();
			for (int i = 1; i <= 12; i++) {
				Socio sj = new Socio("socio_j" + i, "pass", "Jugador" + i, "Aguila", "J" + i, LocalDate.of(1998, 1, 1));
				club.agregarUsuario(sj);
				sociosEQ1.add(sj);
				gestor.inscribirJugador(admin, sj.getSocioID(), eq1.getNombre(), i, i <= 4 ? "Defensa" : "Delantero");
			}

			// EQ2: Titanes Basket (baloncesto, Mayores, cupo 15, E2, 8 habilitados con camisetas 4..11)
			Equipo eq2 = gestor.crearEquipo(admin, "Titanes Basket", basket, mayores, 15);
			gestor.asignarEntrenador(admin, "Titanes Basket", e2.getIdEmpleado());
			for (int i = 1; i <= 8; i++) {
				Socio sj = new Socio("socio_b" + i, "pass", "Basket" + i, "Titan", "B" + i, LocalDate.of(1997, 2, 2));
				club.agregarUsuario(sj);
				gestor.inscribirJugador(admin, sj.getSocioID(), eq2.getNombre(), i + 3, "Alero");
			}

			// EQ3: Halcones FC (fútbol, Mayores, cupo 20, E1, 6 habilitados)
			Equipo eq3 = gestor.crearEquipo(admin, "Halcones FC", futbol, mayores, 20);
			gestor.asignarEntrenador(admin, "Halcones FC", e1.getIdEmpleado());
			for (int i = 1; i <= 6; i++) {
				Socio sj = new Socio("socio_h" + i, "pass", "Halcon" + i, "FC", "H" + i, LocalDate.of(1999, 5, 5));
				club.agregarUsuario(sj);
				gestor.inscribirJugador(admin, sj.getSocioID(), eq3.getNombre(), i, "Medio");
			}

			// EQ4: Cóndores Voleibol (voleibol, Sub-17), dado de baja, sin compromisos
			Equipo eq4 = gestor.crearEquipo(admin, "Cóndores Voleibol", voleibol, sub17, 10);
			gestor.darDeBajaEquipo(admin, "Cóndores Voleibol");

			System.out.println("✔ Configuración inicial de datos comunes completada exitosamente.");

			// =========================================================================
			// ESCENARIO EP22.1 y EP22.2: Crear equipo (RF22)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP22: Crear equipo (RF22) ---");
			Equipo leones = gestor.crearEquipo(admin, "Leones", voleibol, sub15, 14);
			System.out.println("✔ EP22.1 APROBADO: Equipo registrado sin jugadores -> " + leones);

			// Rechazo (a): Nombre duplicado
			try {
				gestor.crearEquipo(admin, "Águilas FC", futbol, mayores, 22);
				System.err.println("❌ ERROR: Debió fallar por equipo duplicado");
			} catch (DuplicadoException e) {
				System.out.println("✔ EP22.2 (a) RECHAZADO: Nombre duplicado -> " + e.getMessage());
			}

			// Rechazo (b): Cupo menor al mínimo de habilitados (Fútbol exige 7)
			try {
				gestor.crearEquipo(admin, "Chiquitos FC", futbol, mayores, 6);
				System.err.println("❌ ERROR: Debió fallar por cupo menor al mínimo");
			} catch (CupoExcedidoException e) {
				System.out.println("✔ EP22.2 (b) RECHAZADO: Cupo menor a habilitados mínimos -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP23.1 y EP23.2: Inscribir jugador (RF23)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP23: Inscribir jugador en equipo (RF23) ---");
			// (a) S8 (Mayores) entra a EQ1 como defensa #20 con cupo
			JugadorEquipo j_s8 = gestor.inscribirJugador(admin, s8.getSocioID(), eq1.getNombre(), 20, "Defensa");
			System.out.println("✔ EP23.1 (a) APROBADO: " + j_s8);

			// (b) J1 (de fútbol en EQ1) entra a EQ2 (baloncesto) con el #12
			JugadorEquipo j1_en_basket = gestor.inscribirJugador(admin, sociosEQ1.get(0).getSocioID(), eq2.getNombre(), 12, "Base");
			System.out.println("✔ EP23.1 (b) APROBADO: Mismo socio en deporte distinto -> " + j1_en_basket);

			// Rechazos EP23.2:
			// (a) Edad fuera de categoría (J3 de 14 años en Mayores)
			Socio socioMenor = new Socio("j_menor", "pass", "Nino", "Sub15", "M15", LocalDate.of(2012, 1, 1)); // 14 años
			club.agregarUsuario(socioMenor);
			try {
				gestor.inscribirJugador(admin, socioMenor.getSocioID(), eq1.getNombre(), 21, "Delantero");
				System.err.println("❌ ERROR: Debió rechazar por edad fuera de categoría");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP23.2 (a) RECHAZADO: Edad fuera de categoría -> " + e.getMessage());
			}

			// (b) J1 en EQ3 (ya juega en EQ1, ambos son fútbol)
			try {
				gestor.inscribirJugador(admin, sociosEQ1.get(0).getSocioID(), eq3.getNombre(), 15, "Delantero");
				System.err.println("❌ ERROR: Debió rechazar por jugar ya en otro equipo de fútbol");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP23.2 (b) RECHAZADO: Un solo equipo por deporte -> " + e.getMessage());
			}

			// (c) S9 con número #20 de S8 en EQ1
			try {
				gestor.inscribirJugador(admin, s9.getSocioID(), eq1.getNombre(), 20, "Volante");
				System.err.println("❌ ERROR: Debió rechazar por camiseta ocupada");
			} catch (DuplicadoException e) {
				System.out.println("✔ EP23.2 (c) RECHAZADO: Número de camiseta ya en uso -> " + e.getMessage());
			}

			// (d) EQ2 con cupo lleno (15 activos) recibe uno más
			// Actualmente EQ2 tiene 8 iniciales + J1 (#12) = 9. Agreguemos 6 más para llegar a 15.
			for (int k = 1; k <= 6; k++) {
				Socio extra = new Socio("ext_b" + k, "pass", "Extra" + k, "T", "EB" + k, LocalDate.of(1995, 1, 1));
				club.agregarUsuario(extra);
				gestor.inscribirJugador(admin, extra.getSocioID(), eq2.getNombre(), 30 + k, "Alero");
			}
			try {
				Socio extraLleno = new Socio("ext_full", "pass", "Full", "T", "EBF", LocalDate.of(1995, 1, 1));
				club.agregarUsuario(extraLleno);
				gestor.inscribirJugador(admin, extraLleno.getSocioID(), eq2.getNombre(), 99, "Alero");
				System.err.println("❌ ERROR: Debió rechazar por cupo lleno");
			} catch (CupoExcedidoException e) {
				System.out.println("✔ EP23.2 (d) RECHAZADO: Cupo lleno -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP24.1 y EP24.2: Dar de baja equipos y desvincular (RF24)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP24: Dar de baja equipos y desvincular (RF24) ---");
			// Creamos equipo temporal sin compromisos
			Equipo eqTemp = gestor.crearEquipo(admin, "EquipoTemporal", futbol, mayores, 10);
			Socio sTemp = new Socio("s_temp", "pass", "Temp", "S", "ST1", LocalDate.of(1996, 1, 1));
			club.agregarUsuario(sTemp);
			gestor.inscribirJugador(admin, sTemp.getSocioID(), eqTemp.getNombre(), 7, "Delantero");

			// (a) Equipo sin compromisos se da de baja
			gestor.darDeBajaEquipo(admin, eqTemp.getNombre());
			System.out.println("✔ EP24.1 (a) APROBADO: Equipo dado de baja e integrantes liberados.");

			// (b) Desvincular a J12 de EQ1 (camiseta #12 liberada)
			gestor.retirarJugador(admin, eq1.getNombre(), 12);
			System.out.println("✔ EP24.1 (b) APROBADO: Jugador retirado, camiseta liberada.");
			// Verificamos que camiseta 12 quedó disponible en EQ1
			if (eq1.numeroDisponible(12)) {
				System.out.println("✔ Confirmado: Número #12 ahora disponible para nuevo jugador.");
			}

			// EP24.2 Rechazos:
			// (a) Equipo con compromisos activos no se puede dar de baja
			// Completar EQ3 a 8 habilitados para programar partido
			Socio h7 = new Socio("socio_h7", "pass", "Halcon7", "FC", "H7", LocalDate.of(1999, 1, 1));
			Socio h8 = new Socio("socio_h8", "pass", "Halcon8", "FC", "H8", LocalDate.of(1999, 1, 1));
			club.agregarUsuario(h7);
			club.agregarUsuario(h8);
			gestor.inscribirJugador(admin, h7.getSocioID(), eq3.getNombre(), 7, "Defensa");
			gestor.inscribirJugador(admin, h8.getSocioID(), eq3.getNombre(), 8, "Defensa");

			PartidoInterno pi1 = gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 10, 12), "evaluación");
			try {
				gestor.darDeBajaEquipo(admin, eq1.getNombre());
				System.err.println("❌ ERROR: Debió fallar porque EQ1 tiene partido pendiente");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP24.2 (a) RECHAZADO: Tiene compromisos activos -> " + e.getMessage());
			}

			// (b) Intentar programar con equipo inactivo EQ4
			try {
				gestor.programarPartidoInterno(e1, eq1.getNombre(), eq4.getNombre(), LocalDate.of(2026, 10, 15), "amistoso");
				System.err.println("❌ ERROR: Debió rechazar programar con equipo inactivo");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP24.2 (b) RECHAZADO: Equipo inactivo -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP12.1 y EP12.2: Programar partido interno (RF12)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP12: Programar partido interno (RF12) ---");
			System.out.println("✔ EP12.1 APROBADO: Partido registrado con fecha futura y propósito -> " + pi1);

			// Rechazos EP12.2:
			// (a) Distinto deporte (Fútbol vs Baloncesto)
			try {
				gestor.programarPartidoInterno(e1, eq1.getNombre(), eq2.getNombre(), LocalDate.of(2026, 10, 20), "amistoso");
				System.err.println("❌ ERROR: Debió fallar por deportes distintos");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP12.2 (a) RECHAZADO: Equipos de deportes distintos -> " + e.getMessage());
			}

			// (b) Distinta categoría
			Equipo eqSub17 = gestor.crearEquipo(admin, "Águilas Sub-17", futbol, sub17, 15);
			try {
				gestor.programarPartidoInterno(e1, eq1.getNombre(), eqSub17.getNombre(), LocalDate.of(2026, 10, 20), "amistoso");
				System.err.println("❌ ERROR: Debió fallar por categorías distintas");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP12.2 (b) RECHAZADO: Categorías distintas -> " + e.getMessage());
			}

			// (c) Mínimo habilitados (Fútbol exige 7, creemos equipo con solo 4)
			Equipo eqInsuficiente = gestor.crearEquipo(admin, "Incompletos FC", futbol, mayores, 10);
			for (int i = 1; i <= 4; i++) {
				Socio sf = new Socio("sf" + i, "pass", "F" + i, "FC", "SF" + i, LocalDate.of(1998, 1, 1));
				club.agregarUsuario(sf);
				gestor.inscribirJugador(admin, sf.getSocioID(), eqInsuficiente.getNombre(), i, "Medio");
			}
			try {
				gestor.programarPartidoInterno(e1, eq1.getNombre(), eqInsuficiente.getNombre(), LocalDate.of(2026, 10, 20), "amistoso");
				System.err.println("❌ ERROR: Debió fallar por no cumplir mínimo de habilitados");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP12.2 (c) RECHAZADO: Menor a mínimo de habilitados -> " + e.getMessage());
			}

			// (d) Fecha no futura (2026-09-20 cuando hoy es 2026-10-05)
			try {
				gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 9, 20), "evaluación");
				System.err.println("❌ ERROR: Debió fallar por fecha pasada");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP12.2 (d) RECHAZADO: Fecha no futura -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP13.1 y EP13.2: Registrar resultado y estadísticas (RF13)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP13: Registrar resultado y estadísticas (RF13) ---");
			// (a) Fútbol EQ1 2-1 Halcones: estadísticas a J1 (1 gol, 1 asist)
			JugadorEquipo j1_eq1 = eq1.buscarJugador(1);
			gestor.registrarEstadistica(e1, pi1, j1_eq1, new EstadisticaFutbol(j1_eq1, 1, 1, 0, 0));
			gestor.registrarResultadoInterno(e1, pi1, 2, 1);
			System.out.println("✔ EP13.1 (a) APROBADO: Resultado 2-1 y estadística registrada.");

			// (b) Voleibol: Marcador por sets (3-1)
			Equipo vol1 = gestor.crearEquipo(admin, "Panteras Voley", voleibol, sub15, 10);
			for (int i = 1; i <= 6; i++) {
				Socio sv = new Socio("sv1_" + i, "pass", "V" + i, "P", "SV1_" + i, LocalDate.of(2013, 2, 2));
				club.agregarUsuario(sv);
				gestor.inscribirJugador(admin, sv.getSocioID(), vol1.getNombre(), i, "Rematador");
				Socio sl = new Socio("sl_" + i, "pass", "L" + i, "L", "SL_" + i, LocalDate.of(2013, 2, 2));
				club.agregarUsuario(sl);
				gestor.inscribirJugador(admin, sl.getSocioID(), leones.getNombre(), i, "Receptor");
			}
			PartidoInterno piVoley = gestor.programarPartidoInterno(e3, vol1.getNombre(), leones.getNombre(), LocalDate.of(2026, 10, 18), "amistoso");
			JugadorEquipo jVoley = vol1.buscarJugador(1);
			gestor.registrarEstadistica(e3, piVoley, jVoley, new EstadisticaVoleibol(jVoley, 4, 2)); // 4 aces, 2 bloqueos
			gestor.registrarResultadoInterno(e3, piVoley, 3, 1);
			System.out.println("✔ EP13.1 (b) APROBADO: Voleibol 3-1 por sets con aces y bloqueos.");

			// Rechazos EP13.2:
			// (a) Rebotes a J1 en fútbol (EstadisticaBaloncesto en fútbol)
			PartidoInterno piNuevo = gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 10, 25), "amistoso");
			try {
				gestor.registrarEstadistica(e1, piNuevo, j1_eq1, new EstadisticaBaloncesto(j1_eq1, 10, 5));
				System.err.println("❌ ERROR: Debió rechazar estadística de baloncesto en fútbol");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP13.2 (a) RECHAZADO: Estadística de otro deporte -> " + e.getMessage());
			}

			// (b) Empate 80-80 en baloncesto
			Equipo eqBasket2 = gestor.crearEquipo(admin, "Halcones Basket", basket, mayores, 10);
			for (int i = 1; i <= 5; i++) {
				Socio sb2 = new Socio("sb2_" + i, "pass", "B2" + i, "H", "SB2_" + i, LocalDate.of(1996, 3, 3));
				club.agregarUsuario(sb2);
				gestor.inscribirJugador(admin, sb2.getSocioID(), eqBasket2.getNombre(), i, "Pivot");
			}
			PartidoInterno piBasket = gestor.programarPartidoInterno(e2, eq2.getNombre(), eqBasket2.getNombre(), LocalDate.of(2026, 10, 22), "amistoso");
			try {
				gestor.registrarResultadoInterno(e2, piBasket, 80, 80);
				System.err.println("❌ ERROR: Debió rechazar empate en baloncesto");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP13.2 (b) RECHAZADO: Sin empates en baloncesto -> " + e.getMessage());
			}

			// (c) Estadísticas a S8 (no hace parte de panteras ni leones de voleibol)
			try {
				gestor.registrarEstadistica(e3, piVoley, j_s8, new EstadisticaVoleibol(j_s8, 1, 1));
				System.err.println("❌ ERROR: Debió rechazar jugador ajeno a los equipos");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP13.2 (c) RECHAZADO: Jugador ajeno a los equipos que disputan -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP14.1 y EP14.2: Tarjetas y suspensiones (RF14)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP14: Tarjetas y suspensiones (RF14) ---");
			PartidoInterno piTarjetas = gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 10, 26), "amistoso");
			JugadorEquipo j1 = eq1.buscarJugador(1);
			JugadorEquipo j4 = eq1.buscarJugador(4);
			JugadorEquipo j5 = eq1.buscarJugador(5);

			// (a) Roja a J1 -> suspendido
			gestor.registrarEstadistica(e1, piTarjetas, j1, new EstadisticaFutbol(j1, 0, 0, 0, 1));
			// (b) 2 amarillas a J4 -> suspendido
			gestor.registrarEstadistica(e1, piTarjetas, j4, new EstadisticaFutbol(j4, 0, 0, 2, 0));
			// (c) 1 sola amarilla a J5 -> NO suspende
			gestor.registrarEstadistica(e1, piTarjetas, j5, new EstadisticaFutbol(j5, 0, 0, 1, 0));

			gestor.registrarResultadoInterno(e1, piTarjetas, 1, 0);

			System.out.println("✔ EP14.1 (a) J1 con roja: suspendido=" + !j1.estaHabilitado());
			System.out.println("✔ EP14.1 (b) J4 con 2 amarillas: suspendido=" + !j4.estaHabilitado());
			System.out.println("✔ EP14.1 (c) J5 con 1 amarilla: habilitado=" + j5.estaHabilitado());

			// Siguiente partido de EQ1
			PartidoInterno piSiguiente = gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 10, 28), "amistoso");

			// EP14.2 (a) E1 alinea a J1 (suspendido) en el siguiente partido -> RECHAZADO
			try {
				gestor.registrarEstadistica(e1, piSiguiente, j1, new EstadisticaFutbol(j1, 1, 0, 0, 0));
				System.err.println("❌ ERROR: Debió rechazar alinear a J1 suspendido");
			} catch (JugadorNoHabilitadoException e) {
				System.out.println("✔ EP14.2 (a) RECHAZADO: Jugador suspendido no puede alinearse -> " + e.getMessage());
			}

			// (d) EQ1 juega el partido de sanción sin J1; J1 cumple su sanción y puede alinearse en el posterior
			gestor.registrarResultadoInterno(e1, piSiguiente, 2, 0);
			System.out.println("✔ Suspensión de J1 cumplida tras jugarse el partido de sanción: pendiente=" + j1.getSuspensiones().get(0).estaPendiente());

			PartidoInterno piPosterior = gestor.programarPartidoInterno(e1, eq1.getNombre(), eq3.getNombre(), LocalDate.of(2026, 10, 30), "amistoso");
			gestor.registrarEstadistica(e1, piPosterior, j1, new EstadisticaFutbol(j1, 2, 0, 0, 0));
			gestor.registrarResultadoInterno(e1, piPosterior, 3, 1);
			System.out.println("✔ EP14.1 (d) APROBADO: J1 se alinea exitosamente en el partido posterior tras cumplir suspensión.");

			System.out.println("\n=========================================================");
			System.out.println("   ¡TODOS LOS ESCENARIOS DE PP1 SE EJECUTARON CON ÉXITO! ");
			System.out.println("=========================================================");

		} catch (Exception e) {
			System.err.println("❌ EXCEPCIÓN INESPERADA:");
			e.printStackTrace();
		}
	}
}
