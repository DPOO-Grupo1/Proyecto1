package pruebas;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import logica.Club;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.EscenarioConjunto;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.Evaluacion;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import persistencia.PersistenciaAcademias;
import persistencia.PersistenciaEntrenamiento;

/**
 * Prueba integral de persistencia del Área B: Academias y Entrenamiento.
 * Valida el ciclo completo de guardado y carga en archivos JSON.
 */
public class PruebaPersistenciaAreaB {

	public static void main(String[] args) {
		System.out.println("=========================================================");
		System.out.println("    INICIO PRUEBA PERSISTENCIA: ÁREA B (JSON)            ");
		System.out.println("=========================================================");

		try {
			String carpeta = "/private/tmp/p1_test_persistencia_b";
			new File(carpeta).mkdirs();
			String archivoAcademias = carpeta + "/academias.json";
			String archivoEntrenamiento = carpeta + "/entrenamiento.json";

			// 1. Crear Club original en memoria
			Club clubOriginal = new Club();
			clubOriginal.setFechaActual(LocalDate.of(2026, 10, 5));

			// Disciplinas
			Disciplina tenis = new Disciplina("Tenis", 2);
			Disciplina padel = new Disciplina("Pádel", 2);
			Disciplina tenisMesa = new Disciplina("Tenis de mesa", 3);
			clubOriginal.agregarDisciplina(tenis);
			clubOriginal.agregarDisciplina(padel);
			clubOriginal.agregarDisciplina(tenisMesa);

			// Instalaciones
			Instalacion i1 = new CanchaTenis("I1", "Cancha Central Tenis", tenis);
			Instalacion i3 = new CanchaPadel("I3", "Cancha Pádel 1", padel);
			Instalacion i5 = new MesaTenisMesa("I5", "Mesa Techada", tenisMesa, true);
			Instalacion i7 = new EscenarioConjunto("I7", "Cancha Fútbol", "Fútbol");
			clubOriginal.agregarInstalacion(i1);
			clubOriginal.agregarInstalacion(i3);
			clubOriginal.agregarInstalacion(i5);
			clubOriginal.agregarInstalacion(i7);

			// Usuarios
			Administrador admin = new Administrador("admin", "123");
			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			Socio s2 = new Socio("s2", "pass", "Bruno", "Cárdenas", "S2", LocalDate.of(1990, 5, 20));
			Socio s3 = new Socio("s3", "pass", "Carla", "Vega", "S3", LocalDate.of(1988, 8, 15));
			clubOriginal.agregarUsuario(admin);
			clubOriginal.agregarUsuario(e1);
			clubOriginal.agregarUsuario(s1);
			clubOriginal.agregarUsuario(s2);
			clubOriginal.agregarUsuario(s3);

			// Inscripciones
			Inscripcion ins1 = new Inscripcion(s1, tenis, Nivel.PRINCIPIANTE, 1000);
			Inscripcion ins2 = new Inscripcion(s2, tenis, Nivel.INTERMEDIO, 1050);
			Inscripcion ins3 = new Inscripcion(s3, tenis, Nivel.AVANZADO, 1200);
			s1.agregarInscripcion(ins1);
			s2.agregarInscripcion(ins2);
			s3.agregarInscripcion(ins3);
			tenis.agregarInscripcion(ins1);
			tenis.agregarInscripcion(ins2);
			tenis.agregarInscripcion(ins3);

			// Reserva y Partido de práctica
			Reserva r1 = new Reserva("R1", s2, i1, LocalDate.of(2026, 10, 7),
					LocalTime.of(15, 0), LocalTime.of(16, 30), 2, Modalidad.SENCILLOS);
			clubOriginal.agregarReserva(r1);
			i1.agregarReserva(r1);
			s2.agregarReserva(r1);

			PartidoPractica pp1 = new PartidoPractica("PP1", r1, Arrays.asList(s2, s3));
			Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
			stats.put(s2, new EstadisticaTenisPadel(1, 6, 2, 4));
			stats.put(s3, new EstadisticaTenisPadel(2, 8, 5, 1));
			pp1.registrarResultado(s3, stats);
			clubOriginal.agregarPartidoPractica(pp1);

			// Sesión de entrenamiento con convocatorias y evaluación
			SesionEntrenamiento se1 = new SesionEntrenamiento("SE1", e1, i1,
					LocalDate.of(2026, 10, 8), LocalTime.of(9, 0), 90, tenis);
			se1.setFinalizada(true);
			Convocatoria conv1 = new Convocatoria(s1, se1);
			conv1.registrarAsistencia(EstadoAsistencia.ASISTIO);
			conv1.asignarEvaluacion(new Evaluacion(4, "Excelente saque"));
			Convocatoria conv2 = new Convocatoria(s2, se1);
			conv2.registrarAsistencia(EstadoAsistencia.TARDE);
			se1.agregarConvocatoria(conv1);
			se1.agregarConvocatoria(conv2);
			clubOriginal.agregarSesion(se1);
			i1.agregarSesion(se1);

			// 2. Guardar en JSON
			PersistenciaAcademias persAcademias = new PersistenciaAcademias();
			PersistenciaEntrenamiento persEntrenamiento = new PersistenciaEntrenamiento();

			persAcademias.guardar(archivoAcademias, clubOriginal);
			persEntrenamiento.guardar(archivoEntrenamiento, clubOriginal);
			System.out.println("✔ Guardado exitoso de academias.json y entrenamiento.json");

			// 3. Cargar en un Club completamente nuevo
			Club clubCargado = new Club();
			clubCargado.setFechaActual(LocalDate.of(2026, 10, 5));
			// El club cargado necesita los usuarios previamente cargados (como hace CentralPersistencia)
			clubCargado.agregarUsuario(new Administrador("admin", "123"));
			clubCargado.agregarUsuario(new Entrenador("e1", "pass", "Marta", "López", 1));
			clubCargado.agregarUsuario(new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10)));
			clubCargado.agregarUsuario(new Socio("s2", "pass", "Bruno", "Cárdenas", "S2", LocalDate.of(1990, 5, 20)));
			clubCargado.agregarUsuario(new Socio("s3", "pass", "Carla", "Vega", "S3", LocalDate.of(1988, 8, 15)));

			persAcademias.cargar(archivoAcademias, clubCargado);
			persEntrenamiento.cargar(archivoEntrenamiento, clubCargado);
			System.out.println("✔ Carga exitosa desde los archivos JSON");

			// 4. Verificaciones de integridad de datos
			// Disciplinas
			Disciplina dTenis = clubCargado.buscarDisciplina("Tenis");
			if (dTenis.getCupoInstalaciones() != 2) {
				throw new Exception("Cupo de instalaciones de tenis incorrecto");
			}
			System.out.println("✔ Disciplina verificada: " + dTenis.getNombre() + " (cupo: " + dTenis.getCupoInstalaciones() + ")");

			// Instalaciones
			Instalacion cPadel = clubCargado.buscarInstalacion("I3");
			if (!(cPadel instanceof CanchaPadel)) {
				throw new Exception("I3 no fue restaurada como CanchaPadel");
			}
			Instalacion mMesa = clubCargado.buscarInstalacion("I5");
			if (!(mMesa instanceof MesaTenisMesa) || !((MesaTenisMesa) mMesa).isTechada()) {
				throw new Exception("I5 no fue restaurada como MesaTenisMesa techada");
			}
			Instalacion escConj = clubCargado.buscarInstalacion("I7");
			if (!(escConj instanceof EscenarioConjunto)) {
				throw new Exception("I7 no fue restaurada como EscenarioConjunto");
			}
			System.out.println("✔ Instalaciones polimórficas verificadas: CanchaPadel, MesaTechada y EscenarioConjunto");

			// Inscripciones y ELO
			Socio cS1 = clubCargado.buscarSocio("S1");
			Inscripcion cIns1 = cS1.getInscripcion(dTenis);
			if (cIns1 == null || cIns1.getNivel() != Nivel.PRINCIPIANTE || cIns1.getPuntajeELO() != 1000) {
				throw new Exception("Inscripción de S1 no coincide");
			}
			System.out.println("✔ Inscripción y ELO verificados: " + cS1.getNombreCompleto() + " (" + cIns1 + ")");

			// Ranking
			if (dTenis.getRanking().size() != 3) {
				throw new Exception("Ranking de tenis debe tener 3 inscripciones");
			}
			System.out.println("✔ Ranking de tenis restaurado: 1° " + dTenis.getRanking().get(0).getSocio().getNombreCompleto());

			// Reservas y enlace bidireccional
			Reserva cR1 = clubCargado.buscarReserva("R1");
			if (cR1.getNumJugadores() != 2 || cR1.getModalidad() != Modalidad.SENCILLOS) {
				throw new Exception("Datos de reserva R1 no coinciden");
			}
			if (cR1.getInstalacion().getReservas().isEmpty()) {
				throw new Exception("Enlace bidireccional Instalacion -> Reserva no restaurado");
			}
			if (cR1.getTitular().getReservas().isEmpty()) {
				throw new Exception("Enlace bidireccional Socio -> Reserva no restaurado");
			}
			System.out.println("✔ Reserva R1 y enlaces bidireccionales restaurados correctamente");

			// Partido de práctica y estadísticas
			PartidoPractica cPP1 = clubCargado.buscarPartidoPractica("PP1");
			if (!cPP1.isJugado() || cPP1.getGanador() == null || !cPP1.getGanador().getSocioID().equals("S3")) {
				throw new Exception("Estado del partido PP1 o ganador no coinciden");
			}
			EstadisticaTenisPadel statsS3 = (EstadisticaTenisPadel) cPP1.getEstadistica(clubCargado.buscarSocio("S3"));
			if (statsS3 == null || statsS3.getAces() != 5 || statsS3.getGamesGanados() != 8) {
				throw new Exception("Estadísticas de tenis de S3 no coinciden");
			}
			System.out.println("✔ Partido PP1, ganador (" + cPP1.getGanador().getNombreCompleto() + ") y estadísticas de sets/games/aces verificados");

			// Sesión de entrenamiento, convocatorias y evaluación
			SesionEntrenamiento cSE1 = clubCargado.buscarSesion("SE1");
			if (!cSE1.estaFinalizada() || cSE1.getDuracionMin() != 90) {
				throw new Exception("Sesión SE1 no coincide");
			}
			if (cSE1.getConvocatorias().size() != 2) {
				throw new Exception("Convocatorias de SE1 deben ser 2");
			}
			Convocatoria cConvS1 = cSE1.buscarConvocatoria(cS1);
			if (cConvS1 == null || cConvS1.getAsistencia() != EstadoAsistencia.ASISTIO) {
				throw new Exception("Asistencia de S1 en SE1 no coincide");
			}
			if (cConvS1.getEvaluacion() == null || cConvS1.getEvaluacion().getNota() != 4) {
				throw new Exception("Evaluación de S1 en SE1 no coincide");
			}
			System.out.println("✔ Sesión SE1, asistencias y evaluación restauradas: " + cConvS1);

			System.out.println("\n=========================================================");
			System.out.println("   ¡TODAS LAS PRUEBAS DE PERSISTENCIA PASARON CON ÉXITO! ");
			System.out.println("=========================================================");

		} catch (Exception e) {
			System.err.println("FALLO EN PRUEBA PERSISTENCIA: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
