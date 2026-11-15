package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.SesionEntrenamiento;

/**
 * Pruebas de integración de sesiones de entrenamiento: RF15 y RF16.
 */
public class GestorEntrenamientoTest {

	private static final LocalDate MANANA = LocalDate.of(2026, 11, 2);

	private ClubDePruebaAcademias datos;
	private GestorEntrenamiento gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaAcademias();
		gestor = new GestorEntrenamiento(datos.club);
	}

	// RF15: planear sesiones individuales, grupales y de equipo

	@Test
	public void rf15EntrenadorPlaneaSesionGrupalYDeEquipo() throws Exception {
		SesionEntrenamiento grupal = gestor.planearSesion(datos.e1, "Tenis", "I1", MANANA,
				LocalTime.of(8, 0), 60, Arrays.asList("S1", "S2"));
		assertNotNull(grupal);
		assertEquals(2, grupal.getConvocatorias().size());

		SesionEntrenamiento equipo = gestor.planearSesionEquipo(datos.e1, "Águilas FC", "I6", MANANA,
				LocalTime.of(10, 0), 90);
		assertNotNull(equipo);
		assertEquals(2, equipo.getConvocatorias().size());
	}

	@Test
	public void rf15RechazaFisioterapeutaSocioYChoqueDeHorario() throws Exception {
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.planearSesion(datos.f1, "Tenis", "I1", MANANA,
						LocalTime.of(8, 0), 60, Arrays.asList("S1")));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.planearSesion(datos.s1, "Tenis", "I1", MANANA,
						LocalTime.of(8, 0), 60, Arrays.asList("S1")));

		// Instalación incompatible
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.planearSesion(datos.e1, "Tenis", "I4", MANANA,
						LocalTime.of(8, 0), 60, Arrays.asList("S1")));

		// Choque de horario del entrenador
		gestor.planearSesion(datos.e1, "Tenis", "I1", MANANA, LocalTime.of(8, 0), 60, Arrays.asList("S1"));
		assertThrows(ConflictoHorarioException.class,
				() -> gestor.planearSesion(datos.e1, "Tenis", "I2", MANANA,
						LocalTime.of(8, 30), 60, Arrays.asList("S2")));
	}

	// RF16: asistencia, evaluación y consultas

	@Test
	public void rf16FinalizaSesionRegistraAsistenciaEvaluacionYConsultaConvocatorias() throws Exception {
		SesionEntrenamiento sesion = gestor.planearSesion(datos.e1, "Tenis", "I1", MANANA,
				LocalTime.of(8, 0), 60, Arrays.asList("S1", "S2"));

		gestor.finalizarSesion(datos.e1, sesion.getId());
		assertTrue(sesion.estaFinalizada());

		gestor.registrarAsistencia(datos.e1, sesion.getId(), "S1", "asistió");
		gestor.registrarAsistencia(datos.e1, sesion.getId(), "S2", "llegó tarde");
		gestor.registrarEvaluacion(datos.e1, sesion.getId(), "S1", 5, "Excelente derecha");

		List<Convocatoria> convS1 = gestor.consultarConvocatoriasSocio(datos.s1, "S1");
		assertEquals(1, convS1.size());
		assertEquals(EstadoAsistencia.ASISTIO, convS1.get(0).getAsistencia());
		assertEquals(5, convS1.get(0).getEvaluacion().getNota());

		assertEquals(1, gestor.consultarSesiones(datos.e1, MANANA).size());
	}

	@Test
	public void rf16RechazaRegistrarAntesDeFinalizarOtroEntrenadorYEstadoPendiente() throws Exception {
		SesionEntrenamiento sesion = gestor.planearSesion(datos.e1, "Tenis", "I1", MANANA,
				LocalTime.of(8, 0), 60, Arrays.asList("S1"));

		// Sesión no finalizada
		assertThrows(EstadoInvalidoException.class,
				() -> gestor.registrarAsistencia(datos.e1, sesion.getId(), "S1", "asistió"));
		assertThrows(EstadoInvalidoException.class,
				() -> gestor.registrarEvaluacion(datos.e1, sesion.getId(), "S1", 4, "Bien"));

		gestor.finalizarSesion(datos.e1, sesion.getId());

		// Otro entrenador no responsable
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.registrarAsistencia(datos.e2, sesion.getId(), "S1", "asistió"));

		// Estado pendiente y nota fuera de rango
		assertThrows(DatoInvalidoException.class,
				() -> gestor.registrarAsistencia(datos.e1, sesion.getId(), "S1", "pendiente"));
		assertThrows(DatoInvalidoException.class,
				() -> gestor.registrarEvaluacion(datos.e1, sesion.getId(), "S1", 6, "Nota inválida"));
	}
}

