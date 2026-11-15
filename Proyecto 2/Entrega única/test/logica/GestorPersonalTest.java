package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.CoberturaTurnosException;
import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import modelo.personal.EstadoSolicitud;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;

/**
 * Pruebas de integración de turnos y sugerencias: RF18, RF19, RF21, RF32 y RF33.
 * El club de prueba empieza con e1, e2 y f1 de lunes a sábado de 6:00 a 20:00.
 */
public class GestorPersonalTest {

	private static final LocalTime SEIS = LocalTime.of(6, 0);
	private static final LocalTime DOCE = LocalTime.of(12, 0);
	private static final LocalTime VEINTE = LocalTime.of(20, 0);

	private ClubDePruebaTienda datos;
	private GestorPersonal gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaTienda();
		gestor = new GestorPersonal(datos.club);
	}

	// RF18: solicitar cambio de turno

	@Test
	public void rf18ElEntrenadorPideCambiarSuTurnoYQuedaPendiente() throws Exception {
		Turno martes = datos.turnoDe(datos.e1, DayOfWeek.TUESDAY);

		SolicitudCambioTurno solicitud = gestor.solicitarCambio(datos.e1, martes, DayOfWeek.WEDNESDAY, SEIS, DOCE);

		assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
		assertTrue(martes.getSolicitudes().contains(solicitud));
		assertEquals(DayOfWeek.TUESDAY, martes.getDia());
	}

	@Test
	public void rf18RechazaTurnoAjenoDomingoYHorarioFueraDeAtencion() {
		Turno martesE1 = datos.turnoDe(datos.e1, DayOfWeek.TUESDAY);
		Turno martesE2 = datos.turnoDe(datos.e2, DayOfWeek.TUESDAY);

		assertThrows(PermisoDenegadoException.class,
				() -> gestor.solicitarCambio(datos.e1, martesE2, DayOfWeek.WEDNESDAY, SEIS, DOCE));
		assertThrows(DatoInvalidoException.class,
				() -> gestor.solicitarCambio(datos.e1, martesE1, DayOfWeek.SUNDAY, SEIS, DOCE));
		assertThrows(DatoInvalidoException.class, () -> gestor.solicitarCambio(datos.e1, martesE1,
				DayOfWeek.WEDNESDAY, LocalTime.of(21, 0), LocalTime.of(23, 0)));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.solicitarCambio(datos.admin, martesE1, DayOfWeek.WEDNESDAY, SEIS, DOCE));
		assertTrue(martesE1.getSolicitudes().isEmpty());
	}

	// RF19: el fisioterapeuta pide cambio y consulta sus solicitudes

	@Test
	public void rf19NoAceptaUnaSegundaSolicitudPendienteYMuestraElEstado() throws Exception {
		Turno jueves = datos.turnoDe(datos.f1, DayOfWeek.THURSDAY);
		SolicitudCambioTurno solicitud = gestor.solicitarCambio(datos.f1, jueves, DayOfWeek.FRIDAY, SEIS, DOCE);

		assertThrows(EstadoInvalidoException.class,
				() -> gestor.solicitarCambio(datos.f1, jueves, DayOfWeek.SATURDAY, SEIS, DOCE));

		gestor.resolverSolicitud(datos.admin, solicitud, false);
		List<SolicitudCambioTurno> mias = gestor.consultarSolicitudes(datos.f1);
		assertEquals(1, mias.size());
		assertEquals(EstadoSolicitud.RECHAZADA, mias.get(0).getEstado());
	}

	// RF21: sugerir productos

	@Test
	public void rf21ElEmpleadoSugiereUnProductoQueQuedaPendiente() throws Exception {
		Sugerencia sugerencia = gestor.sugerirProducto(datos.f1, "Rodillo de masaje", "Artículo", "Para recuperación");

		assertTrue(sugerencia.estaPendiente());
		assertTrue(datos.club.getSugerencias().contains(sugerencia));
		assertTrue(datos.f1.getSugerencias().contains(sugerencia));
	}

	@Test
	public void rf21RechazaSugerenciaIncompletaYDeUnSocio() {
		assertThrows(DatoInvalidoException.class, () -> gestor.sugerirProducto(datos.f1, "", "Artículo", "algo"));
		assertThrows(DatoInvalidoException.class, () -> gestor.sugerirProducto(datos.f1, "Rodillo", "Artículo", " "));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.sugerirProducto(datos.s1, "Rodillo", "Artículo", "algo"));
		assertTrue(datos.club.getSugerencias().isEmpty());
	}

	// RF32: resolver sugerencias

	@Test
	public void rf32ApruebaUnaYRechazaOtraConMotivo() throws Exception {
		Sugerencia rodillo = gestor.sugerirProducto(datos.f1, "Rodillo", "Artículo", "Para recuperación");
		Sugerencia gomitas = gestor.sugerirProducto(datos.e1, "Gomitas", "Snack", "Para los niños");

		gestor.resolverSugerencia(datos.admin, rodillo, true, null);
		gestor.resolverSugerencia(datos.admin, gomitas, false, "Ya existe un producto similar");

		assertEquals(EstadoSolicitud.APROBADA, rodillo.getEstado());
		assertEquals(EstadoSolicitud.RECHAZADA, gomitas.getEstado());
		assertEquals("Ya existe un producto similar", gomitas.getMotivoRechazo());
	}

	@Test
	public void rf32RechazaSinMotivoDosVecesYSinPermiso() throws Exception {
		Sugerencia rodillo = gestor.sugerirProducto(datos.f1, "Rodillo", "Artículo", "Para recuperación");

		assertThrows(DatoInvalidoException.class, () -> gestor.resolverSugerencia(datos.admin, rodillo, false, ""));
		assertThrows(PermisoDenegadoException.class, () -> gestor.resolverSugerencia(datos.e1, rodillo, true, null));
		gestor.resolverSugerencia(datos.admin, rodillo, true, null);
		assertThrows(EstadoInvalidoException.class, () -> gestor.resolverSugerencia(datos.admin, rodillo, true, null));
	}

	// RF33: publicar turnos

	@Test
	public void rf33PublicaUnaSemanaConCoberturaMinima() throws Exception {
		List<Turno> semana = datos.semanaCompleta();

		gestor.publicarTurnos(datos.admin, semana);

		assertSame(semana, datos.club.getTurnos());
		assertEquals(18, datos.club.getTurnos().size());
	}

	@Test
	public void rf33AlPublicarDeNuevoCadaEmpleadoQuedaSoloConSusTurnosNuevos() throws Exception {
		gestor.publicarTurnos(datos.admin, datos.semanaCompleta());

		assertEquals(6, datos.e1.getTurnos().size());
		assertEquals(6, datos.f1.getTurnos().size());
	}

	@Test
	public void rf33RechazaSemanaSinFisioterapeutaYTurnosQueSeSolapan() {
		List<Turno> sinFisio = datos.semanaCompleta();
		sinFisio.remove(sinFisio.size() - 1);
		assertThrows(CoberturaTurnosException.class, () -> gestor.publicarTurnos(datos.admin, sinFisio));

		List<Turno> solapados = datos.semanaCompleta();
		solapados.add(new Turno(99, datos.e2, DayOfWeek.MONDAY, DOCE, VEINTE));
		assertThrows(ConflictoHorarioException.class, () -> gestor.publicarTurnos(datos.admin, solapados));

		assertThrows(PermisoDenegadoException.class, () -> gestor.publicarTurnos(datos.e1, datos.semanaCompleta()));
	}

	@Test
	public void rf33RechazaTurnosFueraDelHorarioDeAtencion() {
		List<Turno> semana = datos.semanaCompleta();
		semana.add(new Turno(99, datos.e3, DayOfWeek.MONDAY, LocalTime.of(21, 0), LocalTime.of(23, 0)));

		assertThrows(DatoInvalidoException.class, () -> gestor.publicarTurnos(datos.admin, semana));
	}

	// RF33: resolver solicitudes de cambio

	@Test
	public void rf33ApruebaUnCambioQueMantieneLaCobertura() throws Exception {
		List<Turno> semana = datos.semanaCompleta();
		semana.add(new Turno(99, datos.e3, DayOfWeek.MONDAY, SEIS, VEINTE));
		gestor.publicarTurnos(datos.admin, semana);
		Turno lunes = semana.get(0);
		SolicitudCambioTurno solicitud = gestor.solicitarCambio(datos.e1, lunes, DayOfWeek.MONDAY, SEIS, DOCE);

		gestor.resolverSolicitud(datos.admin, solicitud, true);

		assertEquals(EstadoSolicitud.APROBADA, solicitud.getEstado());
		assertEquals(DOCE, lunes.getHoraFin());
	}

	@Test
	public void rf33RechazaUnCambioQueDejaUnBloqueSinCobertura() throws Exception {
		Turno lunes = datos.turnoDe(datos.e1, DayOfWeek.MONDAY);
		SolicitudCambioTurno solicitud = gestor.solicitarCambio(datos.e1, lunes, DayOfWeek.MONDAY, SEIS, DOCE);

		assertThrows(CoberturaTurnosException.class, () -> gestor.resolverSolicitud(datos.admin, solicitud, true));

		assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
		assertEquals(VEINTE, lunes.getHoraFin());
	}
}
