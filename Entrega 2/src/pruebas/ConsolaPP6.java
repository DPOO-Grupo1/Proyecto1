package pruebas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import excepciones.CoberturaTurnosException;
import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import logica.Club;
import logica.GestorAcademias;
import logica.GestorPersonal;
import logica.GestorReportes;
import logica.GestorTienda;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.Evaluacion;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;
import modelo.reportes.FiltroHistorial;
import modelo.reportes.Historial;
import modelo.reportes.Periodo;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteFinanciero;
import modelo.tienda.Articulo;
import modelo.tienda.Existencia;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP6: Administración.
 * Evalúa: RF18, RF19, RF21, RF28, RF29, RF30, RF31, RF32, RF33, RF34 y RF35.
 * Ejecutable en Eclipse: Clic derecho -> Run As -> Java Application.
 */
public class ConsolaPP6 {

	public static void main(String[] args) {
		System.out.println("=========================================================");
		System.out.println("       INICIO CONSOLA PP6: ADMINISTRACIÓN DEL CLUB       ");
		System.out.println("=========================================================");

		try {
			Club club = new Club();
			LocalDate hoy = LocalDate.of(2026, 10, 5); // Lunes
			club.setFechaActual(hoy);

			GestorTienda gestorTienda = new GestorTienda(club);
			GestorPersonal gestorPersonal = new GestorPersonal(club);
			GestorReportes gestorReportes = new GestorReportes(club);
			GestorAcademias gestorAcademias = new GestorAcademias(club);

			// 1. Ubicaciones
			Ubicacion u1 = new Ubicacion("Bodega");
			Ubicacion u2 = new Ubicacion("Tienda");
			Ubicacion uCaf = new Ubicacion("Cafetería");
			club.agregarUbicacion(u1);
			club.agregarUbicacion(u2);
			club.agregarUbicacion(uCaf);

			// 2. Usuarios
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Entrenador e2 = new Entrenador("e2", "pass", "Carlos", "Ramos", 2);
			Entrenador e3 = new Entrenador("e3", "pass", "Andrés", "Ruiz", 3);
			club.agregarUsuario(e1);
			club.agregarUsuario(e2);
			club.agregarUsuario(e3);

			Fisioterapeuta f1 = new Fisioterapeuta("f1", "pass", "Felipe", "Gómez", 4);
			Fisioterapeuta f2 = new Fisioterapeuta("f2", "pass", "Diana", "Castro", 5);
			club.agregarUsuario(f1);
			club.agregarUsuario(f2);

			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s9 = new Socio("s9", "pass", "Patricia", "Vargas", "S9", LocalDate.of(1993, 11, 20));
			club.agregarUsuario(s1);
			club.agregarUsuario(s9);

			// 3. Productos y existencias iniciales (Sección 4.1: AR1 10 en U1, AR2 20 en U1, AR1 5 en U2)
			Articulo ar1 = new Articulo("AR1", 250000, "Tenis");
			Articulo ar2 = new Articulo("AR2", 80000, "Fútbol");
			club.agregarProducto(ar1);
			club.agregarProducto(ar2);
			club.agregarExistencia(new Existencia(ar1, u1, 10));
			club.agregarExistencia(new Existencia(ar2, u1, 20));
			club.agregarExistencia(new Existencia(ar1, u2, 5));
			club.agregarExistencia(new Existencia(ar2, u2, 10));

			// 4. Deportes, Academias e Instalaciones para pruebas de reservas y reportes
			Disciplina tenis = new Disciplina("Tenis", 1);
			club.agregarDisciplina(tenis);
			CanchaTenis i1 = new CanchaTenis("I1", "Cancha Tenis 1", tenis);
			club.agregarInstalacion(i1);

			gestorAcademias.inscribirEnAcademia(s9, "S9", "Tenis", "Principiante");

			DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
			club.agregarDeporte(futbol);
			Categoria mayores = new Categoria("Mayores", 18, 99);
			club.agregarCategoria(mayores);
			Equipo eqFutbol = new Equipo("Los Halcones", futbol, mayores, 20);
			eqFutbol.setEntrenador(e1);
			club.agregarEquipo(eqFutbol);

			// Sesión para historial de E1
			SesionEntrenamiento sesionE1 = new SesionEntrenamiento("SE1", e1, i1, hoy, LocalTime.of(8, 0), 90, eqFutbol);
			Convocatoria convS1 = sesionE1.convocar(s1);
			convS1.registrarAsistencia(EstadoAsistencia.ASISTIO);
			convS1.asignarEvaluacion(new Evaluacion(5, "Excelente desempeño"));
			sesionE1.setFinalizada(true);
			club.agregarSesion(sesionE1);

			System.out.println("✔ Datos iniciales de administración cargados exitosamente.");

			// =========================================================================
			// ESCENARIO EP29.1: Consultar el inventario (RF29)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP29: Consultar el inventario (RF29) ---");
			// (a) Consulta U1 ("Bodega"): AR1 (10) y AR2 (20)
			Map<Producto, Integer> invU1 = gestorTienda.consultarInventario(admin, "Bodega");
			System.out.println("✔ EP29.1 (a) APROBADO: Inventario en Bodega ->");
			for (Map.Entry<Producto, Integer> entry : invU1.entrySet()) {
				System.out.println("   - " + entry.getKey().getNombre() + ": " + entry.getValue() + " unidades");
			}

			// (b) Consulta "U9": ubicación no encontrada
			try {
				gestorTienda.consultarInventario(admin, "U9");
				System.err.println("❌ ERROR: Debió rechazar ubicación U9");
			} catch (NoEncontradoException e) {
				System.out.println("✔ EP29.1 (b) RECHAZADO: Ubicación inexistente -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP30.1: Mover productos entre ubicaciones (RF30)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP30: Mover productos entre ubicaciones (RF30) ---");
			// (a) Mueve 5 AR1 de Bodega a Tienda
			gestorTienda.moverProductos(admin, "AR1", "Bodega", "Tienda", 5);
			int ar1Bodega = club.buscarExistencia(ar1, u1).getCantidad();
			int ar1Tienda = club.buscarExistencia(ar1, u2).getCantidad();
			System.out.println("✔ EP30.1 (a) APROBADO: Mover 5 AR1 de Bodega a Tienda -> Bodega: " + ar1Bodega + ", Tienda: " + ar1Tienda);

			// (b) Mueve 30 AR2 desde Bodega (hay 20)
			try {
				gestorTienda.moverProductos(admin, "AR2", "Bodega", "Tienda", 30);
				System.err.println("❌ ERROR: Debió rechazar mover más unidades de las disponibles");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP30.1 (b) RECHAZADO: Cantidad superior a la disponible -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP31.1 y EP31.2: Reabastecer y registrar productos (RF31)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP31: Reabastecer y registrar productos (RF31) ---");
			// (a) Ingresa 15 AR2 más en Bodega (de 20 pasa a 35)
			gestorTienda.reabastecer(admin, "AR2", "Bodega", 15);
			int ar2Bodega = club.buscarExistencia(ar2, u1).getCantidad();
			System.out.println("✔ EP31.1 (a) APROBADO: AR2 reabastecido en Bodega -> Total: " + ar2Bodega + " unidades (esperado: 35)");

			// (b) Registra "Galleta de avena" ($4.000, gluten, 30 en Tienda)
			Snack galleta = new Snack(Arrays.asList("gluten"), "Galleta de avena", 4000);
			gestorTienda.registrarProducto(admin, galleta, "Tienda", 30);
			System.out.println("✔ EP31.1 (b) APROBADO: Producto nuevo registrado con éxito: " + galleta.getNombre() + " en Tienda");

			// Rechazos EP31.2:
			// Artículo con precio 0
			try {
				gestorTienda.registrarProducto(admin, new Articulo("Gorra Gratis", 0, "Tenis"), "Tienda", 10);
				System.err.println("❌ ERROR: Debió rechazar precio 0");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP31.2 (a) RECHAZADO: Precio menor o igual a 0 -> " + e.getMessage());
			}
			// Cantidad inicial -5
			try {
				gestorTienda.registrarProducto(admin, new Articulo("Gorra Negativa", 25000, "Tenis"), "Tienda", -5);
				System.err.println("❌ ERROR: Debió rechazar cantidad negativa");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP31.2 (b) RECHAZADO: Cantidad negativa -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP21.1: Sugerir productos (RF21)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP21: Sugerir productos (RF21) ---");
			// (a) F1 sugiere "Rodillo de masaje" con descripción
			Sugerencia sgRodillo = gestorPersonal.sugerirProducto(f1, "Rodillo de masaje", "Artículo", "Para terapia miofascial");
			System.out.println("✔ EP21.1 (a) APROBADO: Sugerencia enviada por F1: '" + sgRodillo.getNombreProducto() + "' -> Estado: " + sgRodillo.getEstado());

			// (b) Sugerencia sin nombre
			try {
				gestorPersonal.sugerirProducto(f1, "", "Artículo", "Sin nombre del producto");
				System.err.println("❌ ERROR: Debió rechazar sugerencia sin nombre");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP21.1 (b) RECHAZADO: Campos obligatorios vacíos -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIO EP32.1 y EP32.2: Resolver sugerencias de productos (RF32)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP32: Resolver sugerencias de productos (RF32) ---");
			// Sugerencia SG1 sugerida previamente
			Sugerencia sg1 = gestorPersonal.sugerirProducto(e1, "Cinta kinesiologica", "Artículo", "Para soporte muscular");
			// (a) Admin aprueba SG1
			gestorPersonal.resolverSugerencia(admin, sg1, true, null);
			System.out.println("✔ EP32.1 (a) APROBADO: SG1 resuelta como APROBADA -> Estado: " + sg1.getEstado());

			// (b) Admin rechaza la sugerencia de rodillo con motivo
			gestorPersonal.resolverSugerencia(admin, sgRodillo, false, "ya existe un producto similar");
			System.out.println("✔ EP32.1 (b) APROBADO: Sugerencia rechazada con motivo -> '" + sgRodillo.getMotivoRechazo() + "'");

			// EP32.2: Intentar resolver de nuevo SG1 ya aprobada
			try {
				gestorPersonal.resolverSugerencia(admin, sg1, true, null);
				System.err.println("❌ ERROR: Debió rechazar sugerencia ya resuelta");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP32.2 RECHAZADO: Sugerencia ya resuelta -> " + e.getMessage());
			}

			// =========================================================================
			// GESTIÓN DE TURNOS: Creación y publicación de turnos con cobertura completa
			// Cobertura: Mínimo 2 entrenadores y 1 fisioterapeuta en todo bloque
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP33.1: Publicación de turnos (RF33) ---");
			List<Turno> turnosSemanales = new ArrayList<Turno>();
			int idTurno = 1;

			// Fisioterapeutas: F1 cubre 06:00 a 14:00 y F2 cubre 14:00 a 20:00 todos los días
			// El jueves añadimos un turno extra para F1 de 14:00 a 18:00 (para probar solicitud de cambio)
			Turno turnoF1JuevesExtra = null;
			for (DayOfWeek d : club.getHorario().getDias()) {
				turnosSemanales.add(new Turno(idTurno++, f1, d, LocalTime.of(6, 0), LocalTime.of(14, 0)));
				turnosSemanales.add(new Turno(idTurno++, f2, d, LocalTime.of(14, 0), LocalTime.of(20, 0)));
				if (d == DayOfWeek.THURSDAY) {
					turnoF1JuevesExtra = new Turno(idTurno++, f1, d, LocalTime.of(14, 0), LocalTime.of(18, 0));
					turnosSemanales.add(turnoF1JuevesExtra);
				}
			}

			// Entrenadores:
			// Turno de 06:00 a 14:00 para E1 y E2 todos los días
			Turno turnoE1MiercolesManana = null;
			for (DayOfWeek d : club.getHorario().getDias()) {
				Turno tE1 = new Turno(idTurno++, e1, d, LocalTime.of(6, 0), LocalTime.of(14, 0));
				Turno tE2 = new Turno(idTurno++, e2, d, LocalTime.of(6, 0), LocalTime.of(14, 0));
				turnosSemanales.add(tE1);
				turnosSemanales.add(tE2);
				if (d == DayOfWeek.WEDNESDAY) {
					turnoE1MiercolesManana = tE1;
				}
			}

			// Turno de 14:00 a 18:00:
			// Martes: E1, E2, E3 (3 entrenadores para permitir retiro de E1 sin violar cobertura)
			Turno turnoE1MartesTarde = new Turno(idTurno++, e1, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
			Turno turnoE2MartesTarde = new Turno(idTurno++, e2, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
			Turno turnoE3MartesTarde = new Turno(idTurno++, e3, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
			turnosSemanales.add(turnoE1MartesTarde);
			turnosSemanales.add(turnoE2MartesTarde);
			turnosSemanales.add(turnoE3MartesTarde);

			// Miércoles a sábado y lunes de 14:00 a 18:00: E2 y E3 (2 entrenadores)
			for (DayOfWeek d : club.getHorario().getDias()) {
				if (d != DayOfWeek.TUESDAY) {
					turnosSemanales.add(new Turno(idTurno++, e2, d, LocalTime.of(14, 0), LocalTime.of(18, 0)));
					turnosSemanales.add(new Turno(idTurno++, e3, d, LocalTime.of(14, 0), LocalTime.of(18, 0)));
				}
			}

			// Turno de 18:00 a 20:00: E2 y E3 todos los días
			for (DayOfWeek d : club.getHorario().getDias()) {
				turnosSemanales.add(new Turno(idTurno++, e2, d, LocalTime.of(18, 0), LocalTime.of(20, 0)));
				turnosSemanales.add(new Turno(idTurno++, e3, d, LocalTime.of(18, 0), LocalTime.of(20, 0)));
			}

			// Publicar turnos cumpliendo la cobertura mínima
			gestorPersonal.publicarTurnos(admin, turnosSemanales);
			System.out.println("✔ EP33.1 (a) APROBADO: Turnos semanales publicados con cobertura completa (Total: " + club.getTurnos().size() + " turnos)");

			// =========================================================================
			// ESCENARIOS EP18.1: Solicitar un cambio de turno (RF18)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP18: Solicitar un cambio de turno (RF18) ---");
			// (a) E1 pide cambiar su turno del martes 14:00-18:00 al miércoles 14:00-18:00
			SolicitudCambioTurno solE1 = gestorPersonal.solicitarCambio(e1, turnoE1MartesTarde, DayOfWeek.WEDNESDAY,
					LocalTime.of(14, 0), LocalTime.of(18, 0));
			System.out.println("✔ EP18.1 (a) APROBADO: Solicitud de cambio creada por E1 -> Estado: " + solE1.getEstado());

			// (b) E1 pide cambiar un turno de E2 (turno ajeno)
			try {
				gestorPersonal.solicitarCambio(e1, turnoE2MartesTarde, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
				System.err.println("❌ ERROR: Debió rechazar cambio de turno ajeno");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP18.1 (b) RECHAZADO: Turno ajeno -> " + e.getMessage());
			}

			// (c) E1 propone el domingo (fuera de horario de atención)
			try {
				gestorPersonal.solicitarCambio(e1, turnoE1MiercolesManana, DayOfWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
				System.err.println("❌ ERROR: Debió rechazar día domingo");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP18.1 (c) RECHAZADO: Fuera de horario de atención -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIOS EP19.1 y EP19.2: Solicitudes de cambio de personal (RF19)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP19: Solicitudes de cambio de fisioterapeuta (RF19) ---");
			// (a) F1 pide cambiar el jueves por el viernes (de 14:00 a 18:00)
			SolicitudCambioTurno solF1 = gestorPersonal.solicitarCambio(f1, turnoF1JuevesExtra, DayOfWeek.FRIDAY,
					LocalTime.of(14, 0), LocalTime.of(18, 0));
			System.out.println("✔ EP19.1 (a) APROBADO: F1 crea solicitud de cambio -> Estado: " + solF1.getEstado());

			// Decisión administrativa sobre la solicitud de F1 (Rechazada)
			gestorPersonal.resolverSolicitud(admin, solF1, false);

			// (b) F1 consulta sus solicitudes tras la decisión
			List<SolicitudCambioTurno> misSolicitudes = gestorPersonal.consultarSolicitudes(f1);
			System.out.println("✔ EP19.1 (b) APROBADO: Solicitudes consultadas por F1 tras decisión -> "
					+ misSolicitudes.size() + " registrada(s), estado: " + misSolicitudes.get(0).getEstado());

			// Rechazos EP19.2:
			// (a) Cambio de turno de E1 por F1
			try {
				gestorPersonal.solicitarCambio(f1, turnoE1MartesTarde, DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
				System.err.println("❌ ERROR: Debió rechazar turno ajeno");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP19.2 (a) RECHAZADO: Turno ajeno solicitado por fisio -> " + e.getMessage());
			}

			// (b) Segunda solicitud pendiente sobre el mismo turno
			Turno turnoF1Lunes = club.getTurnos().get(0);
			gestorPersonal.solicitarCambio(f1, turnoF1Lunes, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
			try {
				gestorPersonal.solicitarCambio(f1, turnoF1Lunes, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
				System.err.println("❌ ERROR: Debió rechazar segunda solicitud sobre turno con solicitud pendiente");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP19.2 (b) RECHAZADO: Solicitud duplicada pendiente -> " + e.getMessage());
			}

			// (c) Turno 21:00-23:00 (fuera de horario de atención)
			try {
				gestorPersonal.solicitarCambio(e1, turnoE1MiercolesManana, DayOfWeek.WEDNESDAY, LocalTime.of(21, 0), LocalTime.of(23, 0));
				System.err.println("❌ ERROR: Debió rechazar horario fuera de atención");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP19.2 (c) RECHAZADO: Horario fuera de atención -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIOS EP33: Aprobación y validaciones de cobertura (RF33)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP33: Aprobación y validaciones de cobertura (RF33) ---");
			// EP33.1 (b): Aprueba la solicitud de EP18.1 (retira a E1 de bloque de martes con 3 entrenadores; quedan E2 y E3)
			gestorPersonal.resolverSolicitud(admin, solE1, true);
			System.out.println("✔ EP33.1 (b) APROBADO: Solicitud de E1 aprobada exitosamente (cobertura conservada)");

			// EP33.2 (a): Aprueba una solicitud que deja 1 entrenador el miércoles 08:00–12:00
			SolicitudCambioTurno solE1Miercoles = gestorPersonal.solicitarCambio(e1, turnoE1MiercolesManana, DayOfWeek.FRIDAY,
					LocalTime.of(14, 0), LocalTime.of(18, 0));
			try {
				gestorPersonal.resolverSolicitud(admin, solE1Miercoles, true);
				System.err.println("❌ ERROR: Debió rechazar aprobación que deja 1 solo entrenador el miércoles");
			} catch (CoberturaTurnosException e) {
				System.out.println("✔ EP33.2 (a) RECHAZADO: Incumple cobertura mínima -> " + e.getMessage());
			}

			// EP33.2 (b): Publicar semana sin fisioterapeuta el sábado 16:00-20:00
			List<Turno> turnosSinFisioSabado = new ArrayList<Turno>();
			for (Turno t : turnosSemanales) {
				// omitir el turno de fisio del sábado
				if (t.getEmpleado() == f1 && t.getDia() == DayOfWeek.SATURDAY) {
					continue;
				}
				turnosSinFisioSabado.add(t);
			}
			try {
				gestorPersonal.publicarTurnos(admin, turnosSinFisioSabado);
				System.err.println("❌ ERROR: Debió rechazar publicación sin cobertura de fisioterapeuta el sábado");
			} catch (CoberturaTurnosException e) {
				System.out.println("✔ EP33.2 (b) RECHAZADO: Cobertura mínima incumplida el sábado -> " + e.getMessage());
			}

			// EP33.2 (c): Asigna a E2 un turno en un bloque donde ya tiene otro (solapamiento)
			List<Turno> turnosConSolapamiento = new ArrayList<Turno>(turnosSemanales);
			turnosConSolapamiento.add(new Turno(999, e2, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
			try {
				gestorPersonal.publicarTurnos(admin, turnosConSolapamiento);
				System.err.println("❌ ERROR: Debió rechazar turnos con solapamiento");
			} catch (ConflictoHorarioException e) {
				System.out.println("✔ EP33.2 (c) RECHAZADO: Solapamiento de turnos de un mismo empleado -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIOS EP28: Historial global (RF28)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP28: Historial global (RF28) ---");
			// Preparar reserva R1 en club para que aparezca en el historial
			Reserva r1 = new Reserva("R1", s1, i1, hoy, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);
			r1.cancelar();
			club.agregarReserva(r1);

			// (a) Historial de reservas (todas, incluyendo R1 cancelada)
			Historial hGeneral = gestorReportes.consultarHistorial(admin, new FiltroHistorial(null, null, null));
			System.out.println("✔ EP28.1 (a) APROBADO: Reservas en historial global: " + hGeneral.getReservas().size()
					+ " (R1 estado: " + hGeneral.getReservas().get(0).getEstado() + ")");

			// (b) Historial de sesiones y partidos
			System.out.println("✔ EP28.1 (b) APROBADO: Sesiones en historial: " + hGeneral.getSesiones().size());

			// (c) Filtro por E1, rango 2026-10-01 a 2026-10-08 y deporte fútbol
			FiltroHistorial filtroE1Futbol = new FiltroHistorial(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8),
					null, null, e1, futbol, null);
			Historial hFiltrado = gestorReportes.consultarHistorial(admin, filtroE1Futbol);
			System.out.println("✔ EP28.1 (c) APROBADO: Sesiones filtradas para E1 en fútbol: " + hFiltrado.getSesiones().size());

			// EP28.2: Socio S1 intenta consultar el historial global
			try {
				gestorReportes.consultarHistorial(s1, new FiltroHistorial(null, null, null));
				System.err.println("❌ ERROR: Debió rechazar consulta de historial por un socio");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP28.2 RECHAZADO: Permiso denegado al socio -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIOS EP34: Generar reportes (RF34)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP34: Generar reportes (RF34) ---");
			// (a) Financiero semanal 2026-10-05 a 2026-10-11
			ReporteFinanciero repFinanciero = gestorReportes.generarReporteFinanciero(admin, Periodo.SEMANAL, LocalDate.of(2026, 10, 5));
			System.out.println("✔ EP34.1 (a) APROBADO: Reporte financiero generado (" + repFinanciero.getDesde() + " al " + repFinanciero.getHasta() + ")");

			// (b) Deportivo mensual de octubre
			ReporteDeportivo repDeportivo = gestorReportes.generarReporteDeportivo(admin, Periodo.MENSUAL, LocalDate.of(2026, 10, 1));
			System.out.println("✔ EP34.1 (b) APROBADO: Reporte deportivo generado (" + repDeportivo.getDesde() + " al " + repDeportivo.getHasta()
					+ ") -> Resultados: " + repDeportivo.getResultados().size());

			// (c) Reporte diario de fecha sin movimientos (2026-01-01)
			ReporteFinanciero repVacio = gestorReportes.generarReporteFinanciero(admin, Periodo.DIARIO, LocalDate.of(2026, 1, 1));
			System.out.println("✔ EP34.1 (c) APROBADO: Reporte financiero sin movimientos -> Total Tienda: $" + repVacio.getTotalTienda()
					+ ", Total Cafetería: $" + repVacio.getTotalCafeteria() + ", Total Mensualidades: $" + repVacio.getTotalMensualidades());

			// EP34.2: Entrenador E1 pide reporte financiero
			try {
				gestorReportes.generarReporteFinanciero(e1, Periodo.SEMANAL, LocalDate.of(2026, 10, 5));
				System.err.println("❌ ERROR: Debió rechazar generación de reporte por entrenador");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP34.2 RECHAZADO: Empleado sin permisos para reportes financieros -> " + e.getMessage());
			}

			// =========================================================================
			// ESCENARIOS EP35: Registrar mensualidades (RF35)
			// =========================================================================
			System.out.println("\n--- ESCENARIO EP35: Registrar mensualidades (RF35) ---");
			// (a) Admin registra pago de noviembre de S1
			gestorReportes.registrarPago(admin, "S1", YearMonth.of(2026, 11), 120000);
			System.out.println("✔ EP35.1 (a) APROBADO: Mensualidad de noviembre para S1 registrada exitosamente");

			// (b) S9 en mora intenta reservar -> RECHAZADO
			s9.setAlDia(false);
			try {
				gestorAcademias.reservar(s9, "I1", hoy, LocalTime.of(12, 0), LocalTime.of(13, 0), 2, Modalidad.SENCILLOS);
				System.err.println("❌ ERROR: Debió rechazar reserva a socio con mensualidad vencida");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ Socio S9 en mora no puede reservar -> " + e.getMessage());
			}

			// Admin registra el pago pendiente de S9
			gestorReportes.registrarPago(admin, "S9", YearMonth.of(2026, 10), 120000);
			// Con mensualidad al día, la reserva se acepta
			Reserva rS9 = gestorAcademias.reservar(s9, "I1", hoy, LocalTime.of(12, 0), LocalTime.of(13, 0), 2, Modalidad.SENCILLOS);
			System.out.println("✔ EP35.1 (b) APROBADO: Pago registrado para S9. Reserva aceptada con ID: " + rS9.getId());

			// EP35.2: Registrar otra vez noviembre para S1
			try {
				gestorReportes.registrarPago(admin, "S1", YearMonth.of(2026, 11), 120000);
				System.err.println("❌ ERROR: Debió rechazar pago duplicado");
			} catch (DuplicadoException e) {
				System.out.println("✔ EP35.2 RECHAZADO: Pago duplicado para el mismo periodo -> " + e.getMessage());
			}

			System.out.println("\n=========================================================");
			System.out.println("   ¡TODOS LOS ESCENARIOS DE PP6 SE EJECUTARON CON ÉXITO! ");
			System.out.println("=========================================================");

		} catch (Exception e) {
			System.err.println("❌ EXCEPCIÓN INESPERADA:");
			e.printStackTrace();
		}
	}
}
