package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.ConflictoHorarioException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import logica.Club;
import logica.GestorAcademias;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP4: Academias individuales, reservas y ELO (RF6 a RF11 y RF36).
 * Ejecutable en Eclipse: Clic derecho -> Run As -> Java Application.
 */
public class ConsolaPP4 {

	public static void main(String[] args) {
		System.out.println("=========================================================");
		System.out.println("   INICIO CONSOLA PP4: ACADEMIAS, RESERVAS Y ELO         ");
		System.out.println("=========================================================");

		try {
			Club club = new Club();
			LocalDate fechaHoy = LocalDate.of(2026, 10, 5);
			club.setFechaActual(fechaHoy);
			GestorAcademias gestor = new GestorAcademias(club);

			// 1. Disciplinas
			Disciplina tenis = new Disciplina("Tenis", 2);
			Disciplina padel = new Disciplina("Pádel", 2);
			Disciplina tenisMesa = new Disciplina("Tenis de mesa", 3);
			club.agregarDisciplina(tenis);
			club.agregarDisciplina(padel);
			club.agregarDisciplina(tenisMesa);

			// 2. Instalaciones del documento (I1 a I6)
			Instalacion i1 = new CanchaTenis("I1", "Cancha Central Tenis", tenis);
			Instalacion i2 = new CanchaTenis("I2", "Cancha Auxiliar Tenis", tenis);
			Instalacion i3 = new CanchaPadel("I3", "Cancha Pádel 1", padel);
			Instalacion i4 = new CanchaPadel("I4", "Cancha Pádel 2", padel);
			Instalacion i5 = new MesaTenisMesa("I5", "Mesa Techada", tenisMesa, true);
			Instalacion i6 = new MesaTenisMesa("I6", "Mesa Aire Libre", tenisMesa, false);
			club.agregarInstalacion(i1);
			club.agregarInstalacion(i2);
			club.agregarInstalacion(i3);
			club.agregarInstalacion(i4);
			club.agregarInstalacion(i5);
			club.agregarInstalacion(i6);

			// 3. Usuarios comunes (Sección 4.1)
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s2 = new Socio("s2", "pass", "Bruno", "Cárdenas", "S2", LocalDate.of(1990, 5, 20));
			Socio s3 = new Socio("s3", "pass", "Carla", "Vega", "S3", LocalDate.of(1988, 8, 15));
			Socio s4 = new Socio("s4", "pass", "Diego", "Peña", "S4", LocalDate.of(1991, 10, 11));
			Socio s5 = new Socio("s5", "pass", "Elena", "Ruiz", "S5", LocalDate.of(1993, 4, 18));
			Socio s8 = new Socio("s8", "pass", "Helena", "Ramírez", "S8", LocalDate.of(1996, 12, 1));
			Socio s9 = new Socio("s9", "pass", "Iván", "Torres", "S9", LocalDate.of(1992, 11, 2));
			s9.setAlDia(false); // S9 en mora con mensualidad vencida

			// E1: Marta López (Entrenador de Tenis / Pádel)
			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Socio socioE1 = new Socio("e1_socio", "pass", "Marta", "López", "E1_S", LocalDate.of(1985, 1, 15));
			e1.setSocio(socioE1); // Socio que también es entrenador

			club.agregarUsuario(s1);
			club.agregarUsuario(s2);
			club.agregarUsuario(s3);
			club.agregarUsuario(s4);
			club.agregarUsuario(s5);
			club.agregarUsuario(s8);
			club.agregarUsuario(s9);
			club.agregarUsuario(socioE1);
			club.agregarUsuario(e1);

			// =========================================================================
			// RF6: INSCRIBIR EN ACADEMIA
			// =========================================================================
			System.out.println("\n--- [RF6] Inscripción en academias ---");
			// EP6.1 APROBADO: S1 se inscribe en Tenis con Principiante
			Inscripcion insS1 = gestor.inscribirEnAcademia(s1, "S1", "Tenis", "Principiante");
			System.out.println("✔ EP6.1 Aprobado: " + insS1);

			// Inscribir a los demás socios con sus niveles del documento
			gestor.inscribirEnAcademia(s2, "S2", "Tenis", "Intermedio");
			gestor.inscribirEnAcademia(s3, "S3", "Tenis", "Avanzado");
			gestor.inscribirEnAcademia(s4, "S4", "Pádel", "Intermedio");
			gestor.inscribirEnAcademia(s5, "S5", "Pádel", "Intermedio");
			gestor.inscribirEnAcademia(s8, "S8", "Tenis", "Principiante");
			gestor.inscribirEnAcademia(s9, "S9", "Tenis", "Principiante");
			gestor.inscribirEnAcademia(socioE1, "E1_S", "Tenis", "Avanzado");

			// EP6.2a RECHAZADO: S1 ya está inscrito en Tenis
			try {
				gestor.inscribirEnAcademia(s1, "S1", "Tenis", "Avanzado");
				throw new Exception("Debió rechazar inscripción duplicada");
			} catch (DuplicadoException e) {
				System.out.println("✔ EP6.2a Rechazado correctamente (duplicado): " + e.getMessage());
			}

			// EP6.2b RECHAZADO: Disciplina fuera de academias
			try {
				gestor.inscribirEnAcademia(s1, "S1", "Natación", "Principiante");
				throw new Exception("Debió rechazar disciplina inexistente");
			} catch (NoEncontradoException e) {
				System.out.println("✔ EP6.2b Rechazado correctamente (disciplina inválida): " + e.getMessage());
			}

			// EP6.2c RECHAZADO: Nivel inválido
			try {
				gestor.inscribirEnAcademia(s1, "S1", "Tenis", "Experto");
				throw new Exception("Debió rechazar nivel inválido");
			} catch (DatoInvalidoException e) {
				System.out.println("✔ EP6.2c Rechazado correctamente (nivel inválido): " + e.getMessage());
			}

			// =========================================================================
			// RF7: RESERVAR INSTALACIÓN
			// =========================================================================
			System.out.println("\n--- [RF7] Reservar instalación ---");
			// EP7.1a APROBADO: S3 reserva I2, sencillos, 2 jugadores, 2026-10-07 15:00-16:30
			LocalDate fechaRes = LocalDate.of(2026, 10, 7);
			Reserva rA = gestor.reservar(s3, "I2", fechaRes, LocalTime.of(15, 0), LocalTime.of(16, 30), 2, Modalidad.SENCILLOS);
			System.out.println("✔ EP7.1a Aprobado: " + rA);

			// EP7.1b APROBADO: S4 reserva I4 (pádel), dobles, 4 jugadores
			Reserva rB = gestor.reservar(s4, "I4", fechaRes, LocalTime.of(17, 0), LocalTime.of(18, 30), 4, Modalidad.DOBLES);
			System.out.println("✔ EP7.1b Aprobado: " + rB);

			// Reserva R1 del documento: S2 reserva I1 el 2026-10-05 de 14:00 a 15:30
			Reserva r1 = gestor.reservar(s2, "I1", fechaHoy, LocalTime.of(14, 0), LocalTime.of(15, 30), 2, Modalidad.SENCILLOS);

			// EP7.1c APROBADO: S3 reserva I1 el 2026-10-05 de 15:30 a 17:00 (bloque contiguo sin choque)
			Reserva rC = gestor.reservar(s3, "I1", fechaHoy, LocalTime.of(15, 30), LocalTime.of(17, 0), 2, Modalidad.SENCILLOS);
			System.out.println("✔ EP7.1c Aprobado (bloque contiguo): " + rC);

			// EP7.2a RECHAZADO: S1 (tenis) intenta reservar I3 (pádel) sin estar inscrito
			try {
				gestor.reservar(s1, "I3", fechaRes, LocalTime.of(10, 0), LocalTime.of(11, 30), 4, Modalidad.DOBLES);
				throw new Exception("Debió rechazar reserva en disciplina donde no está inscrito");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP7.2a Rechazado (disciplina no inscrita): " + e.getMessage());
			}

			// EP7.2b RECHAZADO: S4 reserva pádel con 2 jugadores (exige 4)
			try {
				gestor.reservar(s4, "I3", fechaRes, LocalTime.of(10, 0), LocalTime.of(11, 30), 2, Modalidad.SENCILLOS);
				throw new Exception("Debió rechazar pádel con 2");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP7.2b Rechazado (pádel exige 4): " + e.getMessage());
			}

			// EP7.3b RECHAZADO: Solapamiento con reserva activa R1 (15:00 a 16:00 sobre 14:00-15:30)
			try {
				gestor.reservar(s3, "I1", fechaHoy, LocalTime.of(15, 0), LocalTime.of(16, 0), 2, Modalidad.SENCILLOS);
				throw new Exception("Debió rechazar por solapamiento con R1");
			} catch (ConflictoHorarioException e) {
				System.out.println("✔ EP7.3b Rechazado (solapamiento): " + e.getMessage());
			}

			// EP7.4a RECHAZADO: S9 con mensualidad vencida intenta reservar
			try {
				gestor.reservar(s9, "I2", fechaHoy, LocalTime.of(18, 0), LocalTime.of(19, 0), 2, Modalidad.SENCILLOS);
				throw new Exception("Debió rechazar por mensualidad vencida");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP7.4a Rechazado (mensualidad vencida): " + e.getMessage());
			}

			// EP7.4c / RF36: Administrador intenta reservar para uso personal
			try {
				gestor.reservar(admin, "I1", fechaHoy, LocalTime.of(19, 0), LocalTime.of(20, 0), 2, Modalidad.SENCILLOS);
				throw new Exception("Debió rechazar reserva por administrador");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP7.4c / RF36 Rechazado (admin solo consulta): " + e.getMessage());
			}

			// =========================================================================
			// RF7 / RF4: REGISTRAR INGRESO CON BEBIDA CALIENTE
			// =========================================================================
			System.out.println("\n--- [RF7 / RF4] Ingreso con bebidas calientes ---");
			// Reserva R2 en Pádel I3
			Reserva r2Padel = gestor.reservar(s4, "I3", fechaHoy, LocalTime.of(18, 0), LocalTime.of(19, 30), 4, Modalidad.DOBLES);

			// EP7.4b / EP4.2 RECHAZADO: Intenta ingresar con bebida caliente a pádel
			try {
				gestor.registrarIngreso(s4, r2Padel.getId(), true);
				throw new Exception("Debió rechazar ingreso con bebida caliente a pádel");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP7.4b Rechazado (bebida caliente en pádel): " + e.getMessage());
			}

			// Aprobado sin bebida caliente
			boolean ingresoOk = gestor.registrarIngreso(s4, r2Padel.getId(), false);
			System.out.println("✔ Ingreso permitido a pádel sin bebida caliente: " + ingresoOk);

			// =========================================================================
			// RF8: CANCELAR RESERVA
			// =========================================================================
			System.out.println("\n--- [RF8] Cancelar reserva ---");
			// Reserva R_Cancelable a las 14:00 (S8)
			Reserva rCanc = gestor.reservar(s8, "I2", fechaHoy, LocalTime.of(14, 0), LocalTime.of(15, 30), 2, Modalidad.SENCILLOS);
			PartidoPractica ppCanc = gestor.programarPartidoPractica(s8, rCanc.getId(), Arrays.asList("S8", "S1"));

			// EP8.2b RECHAZADO: S3 intenta cancelar reserva de S8
			try {
				gestor.cancelarReserva(s3, rCanc.getId(), LocalTime.of(9, 0));
				throw new Exception("Debió rechazar cancelación por socio no titular");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP8.2b Rechazado (solo dueño cancela): " + e.getMessage());
			}

			// EP8.2a RECHAZADO: S8 intenta cancelar a las 12:30 (faltan 1.5 h < 2 h)
			try {
				gestor.cancelarReserva(s8, rCanc.getId(), LocalTime.of(12, 30));
				throw new Exception("Debió rechazar cancelación con menos de 2 horas");
			} catch (EstadoInvalidoException e) {
				System.out.println("✔ EP8.2a Rechazado (< 2 horas de anticipación): " + e.getMessage());
			}

			// EP8.1 APROBADO: S8 cancela a las 09:00 (faltan 5 horas >= 2h)
			gestor.cancelarReserva(s8, rCanc.getId(), LocalTime.of(9, 0));
			System.out.println("✔ EP8.1 Aprobado: Reserva cancelada exitosamente");
			if (!ppCanc.estaCancelado()) {
				throw new Exception("El partido asociado debió cancelarse en cascada automáticamente");
			}
			System.out.println("✔ Partido PP cancelado automáticamente en cascada (RF8).");

			// =========================================================================
			// RF9: PROGRAMAR PARTIDO DE PRÁCTICA
			// =========================================================================
			System.out.println("\n--- [RF9] Programar partido de práctica ---");
			// Reserva para pruebas de partidos
			Reserva rPartidos = gestor.reservar(s1, "I2", LocalDate.of(2026, 10, 8), LocalTime.of(10, 0), LocalTime.of(11, 30), 2, Modalidad.SENCILLOS);

			// EP9.1a APROBADO: Mismo nivel (S1 Principiante con S8 Principiante)
			PartidoPractica ppMismo = gestor.programarPartidoPractica(s1, rPartidos.getId(), Arrays.asList("S1", "S8"));
			System.out.println("✔ EP9.1a Aprobado (mismo nivel): " + ppMismo);

			// EP9.1b APROBADO: Niveles adyacentes (S2 Intermedio con S3 Avanzado sobre R1)
			PartidoPractica pp1 = gestor.programarPartidoPractica(s2, r1.getId(), Arrays.asList("S2", "S3"));
			System.out.println("✔ EP9.1b Aprobado (niveles adyacentes): " + pp1);

			// EP9.1c APROBADO: Entrenadora E1 exime regla de niveles
			Reserva rE1 = gestor.reservar(s1, "I1", LocalDate.of(2026, 10, 9), LocalTime.of(8, 0), LocalTime.of(9, 30), 2, Modalidad.SENCILLOS);
			PartidoPractica ppEntrenador = gestor.programarPartidoPractica(s1, rE1.getId(), Arrays.asList("S1", "E1_S"));
			System.out.println("✔ EP9.1c Aprobado (entrenadora exime regla de nivel): " + ppEntrenador);

			// EP9.2a RECHAZADO: S1 (Principiante) con S3 (Avanzado) sin entrenador
			Reserva rRech = gestor.reservar(s1, "I2", LocalDate.of(2026, 10, 9), LocalTime.of(14, 0), LocalTime.of(15, 30), 2, Modalidad.SENCILLOS);
			try {
				gestor.programarPartidoPractica(s1, rRech.getId(), Arrays.asList("S1", "S3"));
				throw new Exception("Debió rechazar niveles no adyacentes");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP9.2a Rechazado (niveles no adyacentes): " + e.getMessage());
			}

			// EP9.2b RECHAZADO: S2 (tenis) con S4 (pádel)
			try {
				gestor.programarPartidoPractica(s2, rRech.getId(), Arrays.asList("S2", "S4"));
				throw new Exception("Debió rechazar disciplinas distintas");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP9.2b Rechazado (disciplinas distintas): " + e.getMessage());
			}

			// EP9.2c RECHAZADO: S2 consigo mismo
			try {
				gestor.programarPartidoPractica(s2, rRech.getId(), Arrays.asList("S2", "S2"));
				throw new Exception("Debió rechazar socio consigo mismo");
			} catch (ReglaDeportivaException e) {
				System.out.println("✔ EP9.2c Rechazado (mismo participante repetido): " + e.getMessage());
			}

			// =========================================================================
			// RF10: REGISTRAR RESULTADO
			// =========================================================================
			System.out.println("\n--- [RF10] Registrar resultado de práctica ---");
			Map<Socio, EstadisticaPractica> statsPP1 = new HashMap<Socio, EstadisticaPractica>();
			statsPP1.put(s2, new EstadisticaTenisPadel(1, 6, 3, 8));
			statsPP1.put(s3, new EstadisticaTenisPadel(2, 12, 5, 6));

			int eloS2Antes = s2.getInscripcion(tenis).getPuntajeELO();
			int eloS3Antes = s3.getInscripcion(tenis).getPuntajeELO();
			gestor.registrarResultadoPractica(s3, pp1.getId(), statsPP1);
			System.out.println("✔ Resultado registrado. Ganador: " + pp1.getGanador().getNombreCompleto());
			System.out.println("  S2 ELO: " + eloS2Antes + " -> " + s2.getInscripcion(tenis).getPuntajeELO());
			System.out.println("  S3 ELO: " + eloS3Antes + " -> " + s3.getInscripcion(tenis).getPuntajeELO());

			// =========================================================================
			// RF11: CONSULTAR RANKING E HISTORIAL
			// =========================================================================
			System.out.println("\n--- [RF11] Ranking e historial ---");
			List<Inscripcion> rankingTenis = gestor.consultarRanking(s1, "Tenis");
			System.out.println("Ranking de Tenis ordenado por ELO:");
			for (int i = 0; i < rankingTenis.size(); i++) {
				Inscripcion ins = rankingTenis.get(i);
				System.out.println("  " + (i + 1) + ". " + ins.getSocio().getNombreCompleto()
						+ " (" + ins.getNivel() + ") - ELO: " + ins.getPuntajeELO());
			}

			// Consultar historial propio
			List<PartidoPractica> histS2 = gestor.consultarHistorial(s2, "S2", "Tenis", null, null);
			System.out.println("Historial de partidos de S2: " + histS2.size() + " partido(s)");

			// EP11.2b RECHAZADO: S2 consulta historial de S3
			try {
				gestor.consultarHistorial(s2, "S3", "Tenis", null, null);
				throw new Exception("Debió rechazar consulta de historial ajeno");
			} catch (PermisoDenegadoException e) {
				System.out.println("✔ EP11.2b Rechazado (solo historial propio): " + e.getMessage());
			}

			// =========================================================================
			// RF36: CONSULTA POR ADMINISTRADOR
			// =========================================================================
			System.out.println("\n--- [RF36] Consultas del Administrador ---");
			List<Reserva> resFecha = gestor.consultarReservas(admin, fechaHoy);
			System.out.println("Reservas encontradas para hoy por el admin: " + resFecha.size());
			List<Instalacion> instTenis = gestor.consultarInstalaciones(admin, "Tenis");
			System.out.println("Instalaciones de tenis encontradas: " + instTenis.size());
			System.out.println("✔ RF36 Aprobado (administrador consulta sin error).");

			System.out.println("\n=========================================================");
			System.out.println("     ¡TODAS LAS PRUEBAS DE PP4 PASARON CON ÉXITO!        ");
			System.out.println("=========================================================");

		} catch (Exception e) {
			System.err.println("FALLO EN CONSOLA PP4: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
