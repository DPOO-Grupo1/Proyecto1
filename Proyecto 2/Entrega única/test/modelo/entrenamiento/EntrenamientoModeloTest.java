package modelo.entrenamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import excepciones.DatoInvalidoException;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de las clases del modelo de entrenamiento:
 * SesionEntrenamiento, Convocatoria, Evaluacion y EstadoAsistencia.
 */
public class EntrenamientoModeloTest {

	@Test
	public void evaluacionValidaRangoDeNota() throws Exception {
		Evaluacion eval = new Evaluacion(4, "  Buen revés  ");
		assertEquals(4, eval.getNota());
		assertEquals("Buen revés", eval.getComentario());

		assertThrows(DatoInvalidoException.class, () -> new Evaluacion(0, "Mala"));
		assertThrows(DatoInvalidoException.class, () -> new Evaluacion(6, "Exceso"));
	}

	@Test
	public void estadoAsistenciaConvierteTextosYRechazaInvalidos() throws Exception {
		assertEquals(EstadoAsistencia.ASISTIO, EstadoAsistencia.desdeTexto("asistió"));
		assertEquals(EstadoAsistencia.FALTO, EstadoAsistencia.desdeTexto("falto"));
		assertEquals(EstadoAsistencia.TARDE, EstadoAsistencia.desdeTexto("llegó tarde"));
		assertThrows(DatoInvalidoException.class, () -> EstadoAsistencia.desdeTexto("excusado"));
	}

	@Test
	public void convocatoriaRegistraAsistenciaYEvaluacion() throws Exception {
		Disciplina tenis = new Disciplina("Tenis", 2);
		CanchaTenis cancha = new CanchaTenis("I1", "Tenis 1", tenis);
		Entrenador entrenador = new Entrenador("e1", "pass", "Marta", "López", 1);
		Socio socio = new Socio("s1", "pass", "Ana", "Gómez", "S1", LocalDate.of(1998, 3, 10));
		SesionEntrenamiento sesion = new SesionEntrenamiento("SE1", entrenador, cancha,
				LocalDate.of(2026, 11, 2), LocalTime.of(8, 0), 60, tenis);

		Convocatoria conv = new Convocatoria(socio, sesion);
		assertEquals(EstadoAsistencia.PENDIENTE, conv.getAsistencia());
		assertFalse(conv.tieneEvaluacion());

		assertThrows(DatoInvalidoException.class, () -> conv.registrarAsistencia(EstadoAsistencia.PENDIENTE));
		conv.registrarAsistencia(EstadoAsistencia.ASISTIO);
		assertEquals(EstadoAsistencia.ASISTIO, conv.getAsistencia());

		conv.asignarEvaluacion(new Evaluacion(5, "Excelente"));
		assertTrue(conv.tieneEvaluacion());
	}

	@Test
	public void sesionDetectaConflictoHorarioYBuscaConvocatoria() {
		Disciplina tenis = new Disciplina("Tenis", 2);
		CanchaTenis cancha = new CanchaTenis("I1", "Tenis 1", tenis);
		Entrenador entrenador = new Entrenador("e1", "pass", "Marta", "López", 1);
		Socio s1 = new Socio("s1", "pass", "Ana", "Gómez", "S1", LocalDate.of(1998, 3, 10));
		LocalDate fecha = LocalDate.of(2026, 11, 2);

		SesionEntrenamiento sesion = new SesionEntrenamiento("SE1", entrenador, cancha,
				fecha, LocalTime.of(8, 0), 60, tenis);
		sesion.agregarConvocatoria(new Convocatoria(s1, sesion));

		assertTrue(sesion.tieneConflictoCon(fecha, LocalTime.of(8, 30), LocalTime.of(9, 30)));
		assertFalse(sesion.tieneConflictoCon(fecha, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		assertNotNull(sesion.buscarConvocatoria("S1"));
		assertNull(sesion.buscarConvocatoria("S99"));

		assertFalse(sesion.estaFinalizada());
		sesion.finalizar();
		assertTrue(sesion.estaFinalizada());
	}
}
