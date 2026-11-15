package modelo.academias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import excepciones.DatoInvalidoException;
import excepciones.ReglaDeportivaException;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de las clases del modelo de academias: nivel, inscripción ELO,
 * ranking, instalaciones, reservas y partidos de práctica (sencillos y dobles).
 */
public class AcademiaYReservaTest {

	private Socio socio(String login, String id) {
		return new Socio(login, "pass", "Nombre", login, id, LocalDate.of(1995, 1, 1));
	}

	@Test
	public void nivelAdyacenteYDesdeTexto() throws Exception {
		assertTrue(Nivel.PRINCIPIANTE.esAdyacenteA(Nivel.INTERMEDIO));
		assertTrue(Nivel.INTERMEDIO.esAdyacenteA(Nivel.AVANZADO));
		assertFalse(Nivel.PRINCIPIANTE.esAdyacenteA(Nivel.AVANZADO));
		assertEquals(Nivel.INTERMEDIO, Nivel.desdeTexto("intermedio"));
		assertThrows(DatoInvalidoException.class, () -> Nivel.desdeTexto("experto"));
	}

	@Test
	public void inscripcionActualizaEloYDisciplinaOrdenaRanking() {
		Disciplina tenis = new Disciplina("Tenis", 2);
		Inscripcion i1 = new Inscripcion(socio("s1", "S1"), tenis, Nivel.PRINCIPIANTE, 1000);
		Inscripcion i2 = new Inscripcion(socio("s2", "S2"), tenis, Nivel.INTERMEDIO, 1200);
		tenis.agregarInscripcion(i1);
		tenis.agregarInscripcion(i2);

		i1.actualizarELO(1200, true);
		assertTrue(i1.getPuntajeELO() > 1000);

		List<Inscripcion> ranking = tenis.getRanking();
		assertEquals(2, ranking.size());
		assertTrue(ranking.get(0).getPuntajeELO() >= ranking.get(1).getPuntajeELO());
	}

	@Test
	public void instalacionesValidanCapacidadYBebidaCaliente() {
		Disciplina tenis = new Disciplina("Tenis", 2);
		Disciplina padel = new Disciplina("Pádel", 2);
		Disciplina mesa = new Disciplina("Tenis de mesa", 3);

		CanchaTenis ct = new CanchaTenis("I1", "Tenis 1", tenis);
		CanchaPadel cp = new CanchaPadel("I2", "Pádel 1", padel);
		MesaTenisMesa mt = new MesaTenisMesa("I3", "Mesa 1", mesa, true);
		EscenarioConjunto ec = new EscenarioConjunto("I4", "Fútbol", "Fútbol");

		assertTrue(ct.esReservable());
		assertTrue(ct.admite(2, Modalidad.SENCILLOS));
		assertFalse(ct.restringeBebidaCaliente());

		assertFalse(cp.admite(2, Modalidad.SENCILLOS));
		assertTrue(cp.admite(4, Modalidad.DOBLES));
		assertTrue(cp.restringeBebidaCaliente());

		assertTrue(mt.restringeBebidaCaliente());
		assertFalse(ec.esReservable());
	}

	@Test
	public void cancelarReservaCancelaPartidoAsociado() {
		Disciplina tenis = new Disciplina("Tenis", 2);
		CanchaTenis ct = new CanchaTenis("I1", "Tenis 1", tenis);
		Socio s1 = socio("s1", "S1");
		Socio s2 = socio("s2", "S2");
		Reserva reserva = new Reserva("R1", s1, ct, LocalDate.of(2026, 11, 2),
				LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);
		PartidoPractica partido = new PartidoPractica("PP1", reserva, Arrays.asList(s1, s2));

		reserva.cancelar();
		assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
		assertTrue(partido.estaCancelado());
	}

	@Test
	public void partidoDoblesActualizaEloConPromedioParejaRival() throws Exception {
		Disciplina padel = new Disciplina("Pádel", 2);
		CanchaPadel cp = new CanchaPadel("I4", "Pádel 1", padel);
		Socio s1 = socio("s1", "S1");
		Socio s2 = socio("s2", "S2");
		Socio s3 = socio("s3", "S3");
		Socio s4 = socio("s4", "S4");

		Inscripcion i1 = new Inscripcion(s1, padel, Nivel.INTERMEDIO, 1000);
		Inscripcion i2 = new Inscripcion(s2, padel, Nivel.INTERMEDIO, 1000);
		Inscripcion i3 = new Inscripcion(s3, padel, Nivel.INTERMEDIO, 1200);
		Inscripcion i4 = new Inscripcion(s4, padel, Nivel.INTERMEDIO, 1200);
		s1.agregarInscripcion(i1);
		s2.agregarInscripcion(i2);
		s3.agregarInscripcion(i3);
		s4.agregarInscripcion(i4);

		Reserva r = new Reserva("R1", s1, cp, LocalDate.of(2026, 11, 2),
				LocalTime.of(10, 0), LocalTime.of(11, 30), 4, Modalidad.DOBLES);
		PartidoPractica partido = new PartidoPractica("PP1", r, Arrays.asList(s1, s2, s3, s4));

		Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
		stats.put(s1, new EstadisticaTenisPadel(2, 12, 3, 1));
		stats.put(s2, new EstadisticaTenisPadel(2, 12, 2, 2));
		stats.put(s3, new EstadisticaTenisPadel(0, 5, 1, 4));
		stats.put(s4, new EstadisticaTenisPadel(0, 5, 0, 3));

		partido.registrarResultado(s1, stats);

		assertTrue(partido.isJugado());
		assertEquals(s1, partido.getGanador());
		// Pareja 1 (ELO 1000) vence a pareja 2 (promedio 1200): ambos ganan 24 puntos
		assertEquals(1024, i1.getPuntajeELO());
		assertEquals(1024, i2.getPuntajeELO());
		assertEquals(1176, i3.getPuntajeELO());
		assertEquals(1176, i4.getPuntajeELO());
	}

	@Test
	public void partidoDoblesRechazaSetsDistintosEnLaMismaPareja() {
		Disciplina padel = new Disciplina("Pádel", 2);
		CanchaPadel cp = new CanchaPadel("I4", "Pádel 1", padel);
		Socio s1 = socio("s1", "S1");
		Socio s2 = socio("s2", "S2");
		Socio s3 = socio("s3", "S3");
		Socio s4 = socio("s4", "S4");
		Reserva r = new Reserva("R1", s1, cp, LocalDate.of(2026, 11, 2),
				LocalTime.of(10, 0), LocalTime.of(11, 30), 4, Modalidad.DOBLES);
		PartidoPractica partido = new PartidoPractica("PP1", r, Arrays.asList(s1, s2, s3, s4));

		Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
		stats.put(s1, new EstadisticaTenisPadel(2, 12, 0, 0));
		stats.put(s2, new EstadisticaTenisPadel(1, 10, 0, 0));
		stats.put(s3, new EstadisticaTenisPadel(0, 5, 0, 0));
		stats.put(s4, new EstadisticaTenisPadel(0, 5, 0, 0));

		assertThrows(ReglaDeportivaException.class, () -> partido.registrarResultado(s1, stats));
	}
}

