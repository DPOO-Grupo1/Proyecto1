package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.ConflictoHorarioException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.EstadoReserva;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.usuarios.Socio;

/**
 * Pruebas de integración de academias, reservas y prácticas: RF6 a RF11 y RF36.
 */
public class GestorAcademiasTest {

	private static final LocalDate MANANA = LocalDate.of(2026, 11, 2);

	private ClubDePruebaAcademias datos;
	private GestorAcademias gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaAcademias();
		gestor = new GestorAcademias(datos.club);
	}

	// RF6: inscribir en academia

	@Test
	public void rf6SocioSeInscribeEnAcademiaYAdminPuedeInscribir() throws Exception {
		Inscripcion i6 = gestor.inscribirEnAcademia(datos.s6, "S6", "Tenis", "Principiante");
		assertEquals(Nivel.PRINCIPIANTE, i6.getNivel());
		assertEquals(Inscripcion.ELO_INICIAL, i6.getPuntajeELO());

		Inscripcion iAdmin = gestor.inscribirEnAcademia(datos.admin, "S6", "Pádel", "Intermedio");
		assertEquals(Nivel.INTERMEDIO, iAdmin.getNivel());
	}

	@Test
	public void rf6RechazaInscripcionDuplicadaOPorOtroSocio() {
		assertThrows(DuplicadoException.class,
				() -> gestor.inscribirEnAcademia(datos.s1, "S1", "Tenis", "Intermedio"));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.inscribirEnAcademia(datos.s2, "S6", "Tenis", "Principiante"));
	}

	// RF7: reservar e ingresar

	@Test
	public void rf7SocioReservaCanchaEIngresaSinBebidaCaliente() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(10, 0), LocalTime.of(11, 0),
				2, Modalidad.SENCILLOS);
		assertEquals(EstadoReserva.ACTIVA, r.getEstado());
		assertTrue(gestor.registrarIngreso(datos.s1, r.getId(), false));
	}

	@Test
	public void rf7RechazaEscenarioConjuntoMorosoYCupoSimultaneo() throws Exception {
		// Escenario de conjunto no es reservable
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.reservar(datos.s1, "I6", MANANA, LocalTime.of(10, 0), LocalTime.of(11, 0),
						2, Modalidad.SENCILLOS));

		// Socio moroso
		gestor.inscribirEnAcademia(datos.admin, "S7", "Tenis", "Principiante");
		assertThrows(EstadoInvalidoException.class,
				() -> gestor.reservar(datos.sMoroso, "I1", MANANA, LocalTime.of(10, 0), LocalTime.of(11, 0),
						2, Modalidad.SENCILLOS));

		// Cupo simultáneo de Tenis = 2
		gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(14, 0), LocalTime.of(15, 0), 2, Modalidad.SENCILLOS);
		gestor.reservar(datos.s2, "I2", MANANA, LocalTime.of(14, 0), LocalTime.of(15, 0), 2, Modalidad.SENCILLOS);
		assertThrows(CupoExcedidoException.class,
				() -> gestor.reservar(datos.s3, "I3", MANANA, LocalTime.of(14, 0), LocalTime.of(15, 0),
						2, Modalidad.SENCILLOS));
	}

	@Test
	public void rf7RechazaIngresoConBebidaCalienteEnPadel() throws Exception {
		Reserva r = gestor.reservar(datos.s4, "I4", MANANA, LocalTime.of(16, 0), LocalTime.of(17, 0),
				4, Modalidad.DOBLES);
		assertThrows(EstadoInvalidoException.class, () -> gestor.registrarIngreso(datos.s4, r.getId(), true));
	}

	// RF8: cancelar reserva

	@Test
	public void rf8TitularCancelaConMasDeDosHorasYCancelaPartido() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", ClubDePruebaAcademias.HOY, LocalTime.of(15, 0), LocalTime.of(16, 0),
				2, Modalidad.SENCILLOS);
		PartidoPractica p = gestor.programarPartidoPractica(datos.s1, r.getId(), Arrays.asList("S1", "S2"));

		gestor.cancelarReserva(datos.s1, r.getId(), LocalTime.of(12, 0));
		assertEquals(EstadoReserva.CANCELADA, r.getEstado());
		assertTrue(p.estaCancelado());
	}

	@Test
	public void rf8RechazaCancelarTardeYPorOtroUsuario() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", ClubDePruebaAcademias.HOY, LocalTime.of(15, 0), LocalTime.of(16, 0),
				2, Modalidad.SENCILLOS);

		assertThrows(PermisoDenegadoException.class,
				() -> gestor.cancelarReserva(datos.s2, r.getId(), LocalTime.of(10, 0)));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.cancelarReserva(datos.admin, r.getId(), LocalTime.of(10, 0)));
		assertThrows(EstadoInvalidoException.class,
				() -> gestor.cancelarReserva(datos.s1, r.getId(), LocalTime.of(13, 30)));
	}

	// RF9: programar partido de práctica

	@Test
	public void rf9ProgramaPartidoEntreNivelesAdyacentes() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		PartidoPractica p = gestor.programarPartidoPractica(datos.s1, r.getId(), Arrays.asList("S1", "S2"));
		assertNotNull(p);
		assertEquals(2, p.getParticipantes().size());
	}

	@Test
	public void rf9RechazaNivelesNoAdyacentesYChoqueDeHorario() throws Exception {
		Reserva r1 = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		// s1 es Principiante y s3 es Avanzado: no son adyacentes
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.programarPartidoPractica(datos.s1, r1.getId(), Arrays.asList("S1", "S3")));

		// s2 ya tiene otra reserva en el mismo horario
		gestor.reservar(datos.s2, "I2", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		assertThrows(ConflictoHorarioException.class,
				() -> gestor.programarPartidoPractica(datos.s1, r1.getId(), Arrays.asList("S1", "S2")));
	}

	// RF10: registrar resultado de práctica

	@Test
	public void rf10RegistraResultadoYActualizaElo() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		PartidoPractica p = gestor.programarPartidoPractica(datos.s1, r.getId(), Arrays.asList("S1", "S2"));

		Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
		stats.put(datos.s1, new EstadisticaTenisPadel(2, 12, 4, 2));
		stats.put(datos.s2, new EstadisticaTenisPadel(0, 4, 1, 5));

		gestor.registrarResultadoPractica(datos.s1, p.getId(), stats);
		assertTrue(p.isJugado());
		assertEquals(datos.s1, p.getGanador());
		assertTrue(datos.s1.getInscripcion(datos.tenis).getPuntajeELO() > 1000);
	}

	@Test
	public void rf10RechazaEmpateEnSetsYRegistroPorNoParticipante() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		PartidoPractica p = gestor.programarPartidoPractica(datos.s1, r.getId(), Arrays.asList("S1", "S2"));

		Map<Socio, EstadisticaPractica> empate = new HashMap<Socio, EstadisticaPractica>();
		empate.put(datos.s1, new EstadisticaTenisPadel(1, 6, 1, 1));
		empate.put(datos.s2, new EstadisticaTenisPadel(1, 6, 1, 1));

		assertThrows(PermisoDenegadoException.class,
				() -> gestor.registrarResultadoPractica(datos.s3, p.getId(), empate));
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.registrarResultadoPractica(datos.s1, p.getId(), empate));
	}

	// RF11: consultar perfil, historial y ranking

	@Test
	public void rf11ConsultaPerfilHistorialYRanking() throws Exception {
		Reserva r = gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0),
				2, Modalidad.SENCILLOS);
		PartidoPractica p = gestor.programarPartidoPractica(datos.s1, r.getId(), Arrays.asList("S1", "S2"));
		Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
		stats.put(datos.s1, new EstadisticaTenisPadel(2, 12, 3, 1));
		stats.put(datos.s2, new EstadisticaTenisPadel(1, 9, 2, 2));
		gestor.registrarResultadoPractica(datos.s1, p.getId(), stats);

		List<Inscripcion> perfil = gestor.consultarPerfil(datos.s1, "S1");
		assertEquals(1, perfil.size());

		List<PartidoPractica> historial = gestor.consultarHistorial(datos.s1, "S1", "Tenis", MANANA, MANANA);
		assertEquals(1, historial.size());

		List<Inscripcion> ranking = gestor.consultarRanking(datos.s1, "Tenis");
		assertEquals(3, ranking.size());
		assertTrue(ranking.get(0).getPuntajeELO() >= ranking.get(1).getPuntajeELO());
	}

	@Test
	public void rf11RechazaConsultarPerfilOHistorialDeOtroSocio() {
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarPerfil(datos.s2, "S1"));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.consultarHistorial(datos.s2, "S1", "Tenis", null, null));
	}

	// RF36: consultar reservas e instalaciones

	@Test
	public void rf36ConsultaReservasPorFechaEInstalacionesPorDisciplina() throws Exception {
		gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(9, 0), LocalTime.of(10, 0), 2, Modalidad.SENCILLOS);
		List<Reserva> reservas = gestor.consultarReservas(datos.admin, MANANA);
		assertEquals(1, reservas.size());

		List<Instalacion> canchasTenis = gestor.consultarInstalaciones(datos.admin, "Tenis");
		assertEquals(3, canchasTenis.size());
	}

	@Test
	public void rf36RechazaConsultarReservasAEmpleados() {
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarReservas(datos.e1, MANANA));
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarReservas(datos.f1, MANANA));
	}

	@Test
	public void rf7RechazaHorarioInvalido() {
		assertThrows(DatoInvalidoException.class,
				() -> gestor.reservar(datos.s1, "I1", MANANA, LocalTime.of(11, 0), LocalTime.of(10, 0),
						2, Modalidad.SENCILLOS));
	}
}

