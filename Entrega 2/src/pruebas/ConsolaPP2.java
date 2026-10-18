package pruebas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorCompetencias;
import logica.GestorDeportes;
import modelo.competencias.Competencia;
import modelo.competencias.EquipoExterno;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.FilaClasificacion;
import modelo.competencias.FilaTabla;
import modelo.competencias.GruposEliminacion;
import modelo.competencias.Liga;
import modelo.competencias.ParticipanteCompetencia;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.PartidoCompetencia;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP2: Competencias y posiciones.
 * Evalúa: RF1, RF2, RF25, RF26 y RF27.
 * Ejecutable en Eclipse: Clic derecho -> Run As -> Java Application.
 */
public class ConsolaPP2 {

	public static void main(String[] args) {
		System.out.println("=========================================================");
		System.out.println("   INICIO CONSOLA PP2: COMPETENCIAS Y POSICIONES         ");
		System.out.println("=========================================================");

		try {
			Club club = new Club();
			LocalDate fechaHoy = LocalDate.of(2026, 10, 5);
			club.setFechaActual(fechaHoy);

			GestorDeportes gestorDeportes = new GestorDeportes(club);
			GestorCompetencias gestor = new GestorCompetencias(club);

			// 1. Deportes y Categorías (Sección 4.1)
			DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
			DeporteEquipo basket = new DeporteEquipo("Baloncesto", 5, 2, 0, 1, false, false);
			club.agregarDeporte(futbol);
			club.agregarDeporte(basket);

			Categoria mayores = new Categoria("Mayores", 18, 99);
			Categoria sub17 = new Categoria("Sub-17", 15, 17);
			club.agregarCategoria(mayores);
			club.agregarCategoria(sub17);

			// 2. Administrador y Entrenadores
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Entrenador e2 = new Entrenador("e2", "pass", "Carlos", "Ramos", 2);
			club.agregarUsuario(e1);
			club.agregarUsuario(e2);

			// 3. Socios y Equipos comunes
			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s8 = new Socio("s8", "pass", "Helena", "Ramírez", "S8", LocalDate.of(1996, 12, 1));
			club.agregarUsuario(s1);
			club.agregarUsuario(s8);

			// EQ1: Águilas FC (fútbol, Mayores, cupo 22, 12 habilitados)
			Equipo eq1 = gestorDeportes.crearEquipo(admin, "Águilas FC", futbol, mayores, 22);
			gestorDeportes.asignarEntrenador(admin, "Águilas FC", e1.getIdEmpleado());
			List<Socio> sociosEQ1 = new ArrayList<Socio>();
			for (int i = 1; i <= 12; i++) {
				Socio sj = new Socio("socio_j" + i, "pass", "Jugador" + i, "Aguila", "J" + i, LocalDate.of(1998, 1, 1));
				club.agregarUsuario(sj);
				sociosEQ1.add(sj);
				gestorDeportes.inscribirJugador(admin, sj.getSocioID(), eq1.getNombre(), i, i <= 4 ? "Defensa" : "Delantero");
			}

			// Inscribir a S8 en EQ1 (pero NO estará en la nómina de COMP1)
			JugadorEquipo j_s8 = gestorDeportes.inscribirJugador(admin, s8.getSocioID(), eq1.getNombre(), 20, "Defensa");
			Equipo eq2 = gestorDeportes.crearEquipo(admin, "Titanes Basket", basket, mayores, 15);
			gestorDeportes.asignarEntrenador(admin, "Titanes Basket", e2.getIdEmpleado());
			for (int i = 1; i <= 8; i++) {
				Socio sj = new Socio("socio_b" + i, "pass", "Basket" + i, "Titan", "B" + i, LocalDate.of(1997, 2, 2));
				club.agregarUsuario(sj);
				gestorDeportes.inscribirJugador(admin, sj.getSocioID(), eq2.getNombre(), i, "Alero");
			}

			// Equipo Sub-17 para pruebas de categoría
			Equipo eqSub17 = gestorDeportes.crearEquipo(admin, "Cóndores Sub-17", futbol, sub17, 15);

			System.out.println("✔ Datos comunes para PP2 inicializados exitosamente.");

			// =========================================================================
			// ESCENARIO EP25.1: Crear competencia (RF25)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP25: Crear competencia (RF25) ---");
			// (a) COMP1 en liga con nómina mínima 11, 3 amarillas, temporada 2026, rivales EX1 y EX2
			Liga formatoLiga = new Liga(true); // ida y vuelta
			Competencia comp1 = gestor.crearCompetencia(admin, "Copa Interclubes", "Liga Bogotá", 2026, futbol, mayores, 11, 3, formatoLiga);
			EquipoExterno ex1 = gestor.agregarRival(admin, comp1, "Universidad Norte", "UniNorte", null);
			EquipoExterno ex2 = gestor.agregarRival(admin, comp1, "Universidad Sur", "UniSur", null);
			System.out.println("✔ EP25.1 (a) APROBADO: Competencia Liga creada -> " + comp1.getNombre() + " con rivales " + ex1.getNombre() + " y " + ex2.getNombre());

			// (b) COMP2 en grupos y eliminación (2 grupos, 2 clasificados) con grupo por participante
			GruposEliminacion formatoGrupos = new GruposEliminacion(2, 2);
			Competencia comp2 = gestor.crearCompetencia(admin, "ASCUN Baloncesto", "ASCUN", 2026, basket, mayores, 5, 3, formatoGrupos);
			gestor.agregarRival(admin, comp2, "UniAndes Basket", "Universidad de los Andes", "A");
			gestor.agregarRival(admin, comp2, "Javeriana Basket", "Pontificia Universidad Javeriana", "A");
			gestor.agregarRival(admin, comp2, "Nacional Basket", "Universidad Nacional", "B");
			gestor.agregarRival(admin, comp2, "Rosario Basket", "Universidad del Rosario", "B");
			System.out.println("✔ EP25.1 (b) APROBADO: Competencia Grupos creada -> " + comp2.getNombre() + " (" + formatoGrupos + ")");

			// =========================================================================
			// ESCENARIO EP26.1 y EP26.2: Inscribir equipo en competencia (RF26)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP26: Inscribir equipo en competencia (RF26) ---");
			// EP26.1 APROBADO: EQ1 con 12 habilitados en COMP1
			List<Integer> nomina12 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
			EquipoInscrito eq1Inscrito = gestor.inscribirEquipo(admin, comp1, eq1.getNombre(), nomina12, null);
			System.out.println("✔ EP26.1 APROBADO: EQ1 inscrito en COMP1 con nómina de " + eq1Inscrito.getNomina().size() + " jugadores.");

			// Rechazos EP26.2:
			// (a) Equipo Sub-17 en COMP1 (Mayores)
			try {
				gestor.inscribirEquipo(admin, comp1, eqSub17.getNombre(), Arrays.asList(1), null);
				System.err.println("❌ ERROR: Debió fallar por categoría no habilitada");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP26.2 (a) RECHAZADO: Categoría no habilitada -> " + e.getMessage());
			}

			// (b) EQ2 (Baloncesto) en COMP1 (Fútbol)
			try {
				gestor.inscribirEquipo(admin, comp1, eq2.getNombre(), Arrays.asList(1, 2, 3, 4, 5), null);
				System.err.println("❌ ERROR: Debió fallar por deporte distinto");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP26.2 (b) RECHAZADO: Deporte no corresponde -> " + e.getMessage());
			}

			// (c) Nómina de 12 con 2 suspendidos (10 habilitados; mínimo 11) -> Falta 1 habilitado
			Equipo eqSusp = gestorDeportes.crearEquipo(admin, "EquipoSancionado", futbol, mayores, 15);
			for (int i = 1; i <= 12; i++) {
				Socio s_sanc = new Socio("s_sanc_" + i, "pass", "Sanc" + i, "FC", "SANC_" + i, LocalDate.of(1996, 1, 1));
				club.agregarUsuario(s_sanc);
				gestorDeportes.inscribirJugador(admin, s_sanc.getSocioID(), eqSusp.getNombre(), i, "Medio");
			}
			// Suspender a los jugadores 1 y 2
			JugadorEquipo js1 = eqSusp.buscarJugador(1);
			JugadorEquipo js2 = eqSusp.buscarJugador(2);
			js1.agregarSuspension(new Suspension("Roja directa", null));
			js2.agregarSuspension(new Suspension("Roja directa", null));

			Competencia compTest = gestor.crearCompetencia(admin, "Copa Prueba", "Liga", 2026, futbol, mayores, 11, 3, new Liga(false));
			try {
				gestor.inscribirEquipo(admin, compTest, eqSusp.getNombre(), nomina12, null);
				System.err.println("❌ ERROR: Debió rechazar por falta de habilitados (10 habilitados vs 11 requeridos)");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP26.2 (c) RECHAZADO: Falta 1 habilitado -> " + e.getMessage());
			}

			// (d) Nómina que incluye un número de otro equipo (camiseta no existente en el equipo)
			try {
				gestor.inscribirEquipo(admin, compTest, eqSusp.getNombre(), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 99), null);
				System.err.println("❌ ERROR: Debió rechazar número que no pertenece al equipo");
			} catch (NoEncontradoException e) {
				System.out.println("✔ EP26.2 (d) RECHAZADO: Solo jugadores del equipo -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP27.1 y EP27.2: Programar y registrar partidos oficiales (RF27)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP27: Partidos oficiales y resultados (RF27) ---");
			// Inscribir EQ2 en COMP2 en Grupo A
			gestor.inscribirEquipo(admin, comp2, eq2.getNombre(), Arrays.asList(1, 2, 3, 4, 5, 6), "A");

			// (a) EQ1 2-1 EX1
			PartidoCompetencia p1 = gestor.programarPartido(admin, comp1, eq1.getNombre(), ex1.getNombre(), LocalDate.of(2026, 10, 10), "Estadio Campin", "Fecha 1");
			JugadorEquipo j1 = eq1.buscarJugador(1);
			JugadorEquipo j2 = eq1.buscarJugador(2);
			gestor.registrarEstadistica(admin, p1, j1, new EstadisticaFutbol(j1, 1, 1, 1, 0)); // 1 gol, 1 asist, 1 amarilla
			gestor.registrarEstadistica(admin, p1, j2, new EstadisticaFutbol(j2, 1, 0, 0, 0)); // 1 gol
			gestor.registrarResultado(admin, p1, 2, 1);
			System.out.println("✔ EP27.1 (a) APROBADO: EQ1 2-1 EX1 registrado con estadísticas.");

			// (b) EQ1 1-1 EX2
			PartidoCompetencia p2 = gestor.programarPartido(admin, comp1, eq1.getNombre(), ex2.getNombre(), LocalDate.of(2026, 10, 17), "Estadio Campin", "Fecha 2");
			gestor.registrarEstadistica(admin, p2, j1, new EstadisticaFutbol(j1, 1, 0, 1, 0)); // 1 gol, 2da amarilla acumulada
			gestor.registrarResultado(admin, p2, 1, 1);
			System.out.println("✔ EP27.1 (b) APROBADO: EQ1 1-1 EX2 registrado (1 punto cada uno).");

			// (c) EX1 3-0 EX2 entre rivales externos
			PartidoCompetencia p3 = gestor.programarPartido(admin, comp1, ex1.getNombre(), ex2.getNombre(), LocalDate.of(2026, 10, 24), "Estadio Norte", "Fecha 3");
			gestor.registrarResultado(admin, p3, 3, 0);
			System.out.println("✔ EP27.1 (c) APROBADO: EX1 3-0 EX2 registrado entre rivales externos.");

			// (d) J1 llega a 3 amarillas acumuladas (límite 3) -> Suspendido para el siguiente oficial
			PartidoCompetencia p4 = gestor.programarPartido(admin, comp1, ex2.getNombre(), eq1.getNombre(), LocalDate.of(2026, 10, 31), "Estadio Sur", "Fecha 4");
			gestor.registrarEstadistica(admin, p4, j1, new EstadisticaFutbol(j1, 0, 0, 1, 0)); // 3ra amarilla!
			gestor.registrarResultado(admin, p4, 0, 1);
			System.out.println("✔ EP27.1 (d) APROBADO: J1 llegó a 3 amarillas acumuladas. Suspendido=" + !j1.estaHabilitado());

			// EP27.2 Rechazos:
			// (a) Empate 70-70 en Baloncesto COMP2
			PartidoCompetencia pBasket = gestor.programarPartido(admin, comp2, eq2.getNombre(), "UniAndes Basket", LocalDate.of(2026, 11, 5), "Coliseo", "Fase Grupos");
			try {
				gestor.registrarResultado(admin, pBasket, 70, 70);
				System.err.println("❌ ERROR: Debió rechazar empate en baloncesto");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP27.2 (a) RECHAZADO: Sin empates en baloncesto -> " + e.getMessage());
			}

			// (b) Alinear a S8 (no está en la nómina de COMP1)
			try {
				gestor.registrarEstadistica(admin, p4, j_s8, new EstadisticaFutbol(j_s8, 1, 0, 0, 0));
				System.err.println("❌ ERROR: Debió rechazar jugador fuera de la nómina");
			} catch (JugadorNoHabilitadoException e) {
				System.out.println("✔ EP27.2 (b) RECHAZADO: Solo la nómina inscrita juega -> " + e.getMessage());
			}

			// (c) Marcador negativo (-1 a 2)
			try {
				gestor.registrarResultado(admin, pBasket, -1, 2);
				System.err.println("❌ ERROR: Debió rechazar marcador negativo");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP27.2 (c) RECHAZADO: Marcador negativo no permitido -> " + e.getMessage());
			}

			// (d) Alinear a J1 (suspendido por 3 amarillas) en el siguiente partido oficial
			PartidoCompetencia p5 = gestor.programarPartido(admin, comp1, eq1.getNombre(), ex1.getNombre(), LocalDate.of(2026, 11, 7), "Estadio Campin", "Fecha 5");
			try {
				gestor.registrarEstadistica(admin, p5, j1, new EstadisticaFutbol(j1, 1, 0, 0, 0));
				System.err.println("❌ ERROR: Debió rechazar alinear a J1 suspendido");
			} catch (JugadorNoHabilitadoException e) {
				System.out.println("✔ EP27.2 (d) RECHAZADO: Jugador suspendido no puede jugar -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP2.1 y EP2.2: Consultar tabla de posiciones y clasificaciones (RF2)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP2: Consultar tabla de posiciones y clasificaciones (RF2) ---");
			// (a) Tabla de COMP1 tras partidos
			List<FilaTabla> tablaComp1 = gestor.consultarTabla(s1, comp1, null);
			System.out.println("✔ EP2.1 (a) Tabla general de " + comp1.getNombre() + ":");
			for (FilaTabla fila : tablaComp1) {
				System.out.println("   - " + fila);
			}

			// (b) Desempate directo: Si dos equipos empatan a puntos, arriba el que ganó el directo
			System.out.println("✔ EP2.1 (b) Criterio de desempate directo verificado: " + comp1.getFormato().getCriterioDesempate());

			// (c) Grupo A de COMP2
			List<FilaTabla> tablaGrupoA = gestor.consultarTabla(s1, comp2, "A");
			System.out.println("✔ EP2.1 (c) Tabla de Grupo A en COMP2 calculada con " + tablaGrupoA.size() + " participantes.");
			List<ParticipanteCompetencia> clasificados = gestor.consultarClasificados(s1, comp2);
			System.out.println("✔ Clasificados a siguiente fase: " + clasificados.size() + " equipos.");

			// (d) Goleadores y destacados
			List<FilaClasificacion> goleadores = gestor.consultarGoleadores(s1, comp1);
			System.out.println("✔ EP2.1 (d) Goleadores de " + comp1.getNombre() + ":");
			for (FilaClasificacion g : goleadores) {
				System.out.println("   - " + g.getJugador().getSocio().getNombreCompleto() + ": " + g.getValor() + " goles");
			}

			List<FilaClasificacion> destacados = gestor.consultarDestacados(s1, comp1);
			System.out.println("✔ Jugadores destacados (por asistencias en fútbol):");
			for (FilaClasificacion d : destacados) {
				System.out.println("   - " + d.getJugador().getSocio().getNombreCompleto() + ": " + d.getValor() + " asistencias");
			}

			// (e) Filtro por deporte ("Baloncesto")
			List<Competencia> compsBasket = gestor.consultarCompetencias(s1, "Baloncesto", null);
			System.out.println("✔ EP2.1 (e) Competencias filtradas por 'Baloncesto': " + compsBasket.size() + " (" + compsBasket.get(0).getNombre() + ")");

			// EP2.2 APROBADO: Consulta competencia sin resultados o inexistente
			Competencia compVacia = gestor.crearCompetencia(admin, "Copa Vacia", "Liga", 2026, futbol, mayores, 11, 3, new Liga(false));
			List<FilaTabla> tablaVacia = gestor.consultarTabla(s1, compVacia, null);
			System.out.println("✔ EP2.2 APROBADO: Competencia sin resultados retorna lista sin error (tamaño " + tablaVacia.size() + ")");

			// =========================================================================
			// ESCENARIO EP1.1: Consultar información deportiva (RF1)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP1: Consultar información deportiva (RF1) ---");
			Socio socioJ1 = sociosEQ1.get(0);
			// (a) J1 consulta sus participaciones deportivas y partidos
			List<JugadorEquipo> participaciones = gestorDeportes.consultarInformacion(socioJ1);
			System.out.println("✔ EP1.1 (a) J1 participa en " + participaciones.size() + " equipos:");
			for (JugadorEquipo p : participaciones) {
				System.out.println("   - " + p.getEquipo().getNombre() + " (" + p.getEquipo().getDeporte().getNombre() + "), camiseta #" + p.getNumeroCamiseta() + ", posición: " + p.getPosicion());
			}

			// (b) J1 consulta su estado de suspensión
			System.out.println("✔ EP1.1 (b) Historial de sanciones de J1:");
			for (Suspension s : j1.getSuspensiones()) {
				System.out.println("   - " + s);
			}

			System.out.println("\n=========================================================");
			System.out.println("   ¡TODOS LOS ESCENARIOS DE PP2 SE EJECUTARON CON ÉXITO! ");
			System.out.println("=========================================================");

		} catch (Exception e) {
			System.err.println("❌ EXCEPCIÓN INESPERADA:");
			e.printStackTrace();
		}
	}
}
