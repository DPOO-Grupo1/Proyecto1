package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorAcademias;
import logica.GestorEntrenamiento;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.EscenarioConjunto;
import modelo.academias.Instalacion;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP5: Entrenamiento (RF15 y RF16: sesiones, convocatorias, asistencia y evaluación).
 */
public class ConsolaPP5 {

	public static void main(String[] args) {
		System.out.println("=== PP5: Entrenamiento ===");

		try {
			Club club = new Club();
			LocalDate fechaHoy = LocalDate.of(2026, 10, 5);
			club.setFechaActual(fechaHoy);

			GestorAcademias gestorAcademias = new GestorAcademias(club);
			GestorEntrenamiento gestor = new GestorEntrenamiento(club);

			// 1. Disciplinas
			Disciplina tenis = new Disciplina("Tenis", 2);
			Disciplina padel = new Disciplina("Pádel", 2);
			Disciplina tenisMesa = new Disciplina("Tenis de mesa", 3);
			club.agregarDisciplina(tenis);
			club.agregarDisciplina(padel);
			club.agregarDisciplina(tenisMesa);

			// 2. Deporte de equipo y categorías
			DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
			club.agregarDeporte(futbol);
			Categoria mayores = new Categoria("Mayores", 18, 99);

			// 3. Instalaciones
			Instalacion i1 = new CanchaTenis("I1", "Cancha Central Tenis", tenis);
			Instalacion i2 = new CanchaTenis("I2", "Cancha Auxiliar Tenis", tenis);
			Instalacion i3 = new CanchaPadel("I3", "Cancha Pádel 1", padel);
			Instalacion i6 = new MesaTenisMesa("I6", "Mesa Aire Libre", tenisMesa, false);
			Instalacion i7 = new EscenarioConjunto("I7", "Cancha de Fútbol 11", "Fútbol");
			club.agregarInstalacion(i1);
			club.agregarInstalacion(i2);
			club.agregarInstalacion(i3);
			club.agregarInstalacion(i6);
			club.agregarInstalacion(i7);

			// 4. Usuarios
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Entrenador e2 = new Entrenador("e2", "pass", "Carlos", "Gómez", 2);
			club.agregarUsuario(e1);
			club.agregarUsuario(e2);

			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s2 = new Socio("s2", "pass", "Bruno", "Cárdenas", "S2", LocalDate.of(1990, 5, 20));
			Socio s3 = new Socio("s3", "pass", "Carla", "Vega", "S3", LocalDate.of(1988, 8, 15));
			Socio s6 = new Socio("s6", "pass", "Fabio", "Ortiz", "S6", LocalDate.of(1997, 6, 12));
			Socio s7 = new Socio("s7", "pass", "Gabriela", "Soto", "S7", LocalDate.of(1994, 2, 28));
			Socio s8 = new Socio("s8", "pass", "Helena", "Ramírez", "S8", LocalDate.of(1996, 12, 1));
			Socio s9 = new Socio("s9", "pass", "Iván", "Torres", "S9", LocalDate.of(1992, 11, 2));
			club.agregarUsuario(s1);
			club.agregarUsuario(s2);
			club.agregarUsuario(s3);
			club.agregarUsuario(s6);
			club.agregarUsuario(s7);
			club.agregarUsuario(s8);
			club.agregarUsuario(s9);

			// Inscripciones en academias
			gestorAcademias.inscribirEnAcademia(s1, "S1", "Tenis", "Principiante");
			gestorAcademias.inscribirEnAcademia(s2, "S2", "Tenis", "Intermedio");
			gestorAcademias.inscribirEnAcademia(s3, "S3", "Tenis", "Avanzado");
			gestorAcademias.inscribirEnAcademia(s6, "S6", "Tenis de mesa", "Principiante");
			gestorAcademias.inscribirEnAcademia(s7, "S7", "Tenis de mesa", "Avanzado");

			// 5. Equipo EQ1: Águilas FC
			Equipo eq1 = new Equipo("Águilas FC", futbol, mayores, 22);
			eq1.setEntrenador(e1);
			JugadorEquipo j1 = new JugadorEquipo(s8, eq1, 10, "Delantero");
			JugadorEquipo j2 = new JugadorEquipo(s1, eq1, 4, "Defensa");
			eq1.agregarJugador(j1);
			eq1.agregarJugador(j2);
			club.agregarEquipo(eq1);

			// 6. Sesión previa finalizada SE1 (Sección 4.1 del documento):
			// Tenis, E1, 2026-10-01 de 07:00 a 08:30 en I2, convocados S1, S2, S3; finalizada
			SesionEntrenamiento se1 = new SesionEntrenamiento("SE1", e1, i2,
					LocalDate.of(2026, 10, 1), LocalTime.of(7, 0), 90, tenis);
			se1.convocar(s1);
			se1.convocar(s2);
			se1.convocar(s3);
			se1.setFinalizada(true);
			club.agregarSesion(se1);
			i2.agregarSesion(se1);

			// RF15: planear una sesión de entrenamiento
			System.out.println("\n--- [RF15] Planear sesión de entrenamiento ---");

			// EP15.1a APROBADO: Tenis, 2026-10-08 09:00-10:30 en I1, convocados S1, S2 y S3
			SesionEntrenamiento seTenis = gestor.planearSesion(e1, "Tenis", "I1",
					LocalDate.of(2026, 10, 8), LocalTime.of(9, 0), 90,
					Arrays.asList("S1", "S2", "S3"));
			System.out.println("EP15.1a Aprobado (grupal tenis): " + seTenis);
			System.out.println("  Convocados registrados: " + seTenis.getConvocatorias().size());

			// EP15.1b APROBADO: Individual de tenis de mesa en I6 con S7
			SesionEntrenamiento seIndividual = gestor.planearSesion(e1, "Tenis de mesa", "I6",
					LocalDate.of(2026, 10, 8), LocalTime.of(11, 0), 60,
					Arrays.asList("S7"));
			System.out.println("EP15.1b Aprobado (individual tenis de mesa): " + seIndividual);

			// EP15.1c APROBADO: EQ1 completo en I7 (escenario conjunto)
			SesionEntrenamiento seEquipo = gestor.planearSesionEquipo(e1, "Águilas FC", "I7",
					LocalDate.of(2026, 10, 8), LocalTime.of(15, 0), 120);
			System.out.println("EP15.1c Aprobado (equipo en escenario conjunto): " + seEquipo);
			System.out.println("  Convocados de equipo: " + seEquipo.getConvocatorias().size());

			// EP15.2a RECHAZADO: Sesión de EQ1 en la cancha de pádel I3 (instalación incompatible)
			try {
				gestor.planearSesionEquipo(e1, "Águilas FC", "I3",
						LocalDate.of(2026, 10, 9), LocalTime.of(10, 0), 90);
				throw new Exception("Debió rechazar fútbol en cancha de pádel");
			} catch (ReglaDeportivaException e) {
				System.out.println("EP15.2a Rechazado (instalación incompatible): " + e.getMessage());
			}

			// EP15.2b RECHAZADO: Solapamiento con reserva activa R3 en I1
			// Creamos reserva R3 en I1 el 2026-10-08 de 07:00 a 08:30 (S1)
			Reserva r3 = gestorAcademias.reservar(s1, "I1", LocalDate.of(2026, 10, 8),
					LocalTime.of(7, 0), LocalTime.of(8, 30), 2, Modalidad.SENCILLOS);
			try {
				// Intento de programar sesión en I1 cruzándose con R3 (07:30 a 09:00)
				gestor.planearSesion(e1, "Tenis", "I1", LocalDate.of(2026, 10, 8),
						LocalTime.of(7, 30), 60, Arrays.asList("S2"));
				throw new Exception("Debió rechazar por solapamiento con reserva R3");
			} catch (ConflictoHorarioException e) {
				System.out.println("EP15.2b Rechazado (solapamiento con reserva): " + e.getMessage());
			}

			// EP15.2c RECHAZADO: Convocar a S6 (tenis de mesa) a entrenamiento de tenis
			try {
				gestor.planearSesion(e1, "Tenis", "I2", LocalDate.of(2026, 10, 9),
						LocalTime.of(10, 0), 60, Arrays.asList("S6"));
				throw new Exception("Debió rechazar convocar a socio no inscrito en la disciplina");
			} catch (ReglaDeportivaException e) {
				System.out.println("EP15.2c Rechazado (convocado no inscrito en disciplina): " + e.getMessage());
			}

			// EP15.2d RECHAZADO: Duración 0
			try {
				gestor.planearSesion(e1, "Tenis", "I2", LocalDate.of(2026, 10, 9),
						LocalTime.of(10, 0), 0, Arrays.asList("S1"));
				throw new Exception("Debió rechazar duración 0");
			} catch (DatoInvalidoException e) {
				System.out.println("EP15.2d Rechazado (duración 0): " + e.getMessage());
			}

			// RF16: asistencia y evaluación
			System.out.println("\n--- [RF16] Asistencia y evaluación ---");

			// EP16.2a RECHAZADO: E2, no responsable, intenta registrar asistencia en SE1
			try {
				gestor.registrarAsistencia(e2, "SE1", "S1", "asistió");
				throw new Exception("Debió rechazar registro de asistencia por entrenador no responsable");
			} catch (PermisoDenegadoException e) {
				System.out.println("EP16.2a Rechazado (solo entrenador responsable): " + e.getMessage());
			}

			// EP16.2b RECHAZADO: Estado 'pendiente'
			try {
				gestor.registrarAsistencia(e1, "SE1", "S1", "pendiente");
				throw new Exception("Debió rechazar estado pendiente");
			} catch (DatoInvalidoException e) {
				System.out.println("EP16.2b Rechazado (estado pendiente no permitido): " + e.getMessage());
			}

			// EP16.2c RECHAZADO: Nota 6 y nota 0
			try {
				gestor.registrarEvaluacion(e1, "SE1", "S1", 6, "Excelente pero nota inválida");
				throw new Exception("Debió rechazar nota 6");
			} catch (DatoInvalidoException e) {
				System.out.println("EP16.2c Rechazado (nota 6 fuera de rango): " + e.getMessage());
			}
			try {
				gestor.registrarEvaluacion(e1, "SE1", "S1", 0, "Mala nota inválida");
				throw new Exception("Debió rechazar nota 0");
			} catch (DatoInvalidoException e) {
				System.out.println("EP16.2c Rechazado (nota 0 fuera de rango): " + e.getMessage());
			}

			// EP16.2d RECHAZADO: Asistencia de S9, no convocada
			try {
				gestor.registrarAsistencia(e1, "SE1", "S9", "asistió");
				throw new Exception("Debió rechazar socio no convocado");
			} catch (NoEncontradoException e) {
				System.out.println("EP16.2d Rechazado (socio no convocado): " + e.getMessage());
			}

			// EP16.2e RECHAZADO: Asistencia en sesión no finalizada (seTenis de EP15.1a)
			try {
				gestor.registrarAsistencia(e1, seTenis.getId(), "S1", "asistió");
				throw new Exception("Debió rechazar asistencia en sesión no finalizada");
			} catch (EstadoInvalidoException e) {
				System.out.println("EP16.2e Rechazado (sesión no finalizada): " + e.getMessage());
			}

			// EP16.1a APROBADO: SE1 finalizada: S1 asistió, S2 llegó tarde, S3 faltó
			gestor.registrarAsistencia(e1, "SE1", "S1", "asistió");
			gestor.registrarAsistencia(e1, "SE1", "S2", "llegó tarde");
			gestor.registrarAsistencia(e1, "SE1", "S3", "faltó");
			System.out.println("EP16.1a Aprobado: 3 asistencias registradas correctamente:");
			for (Convocatoria c : se1.getConvocatorias()) {
				System.out.println("  - " + c);
			}

			// EP16.1b APROBADO: Evaluación de S1 con nota 4 y comentario; S2 y S3 sin evaluar (opcional)
			gestor.registrarEvaluacion(e1, "SE1", "S1", 4, "Muy buen saque, mejorar volea");
			System.out.println("EP16.1b Aprobado: Evaluación asignada a S1 (opcional para los demás):");
			for (Convocatoria c : se1.getConvocatorias()) {
				System.out.println("  - " + c);
			}

			// Consultar historial del socio S1
			List<Convocatoria> convsS1 = gestor.consultarConvocatoriasSocio(s1, "S1");
			System.out.println("Consultar convocatorias de S1: " + convsS1.size() + " sesión(es)");

			System.out.println("\nFin de PP5.");

		} catch (Exception e) {
			System.err.println("Error inesperado en PP5: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
