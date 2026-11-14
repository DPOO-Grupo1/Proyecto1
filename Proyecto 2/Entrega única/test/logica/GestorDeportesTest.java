package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.Partido;
import modelo.partidos.PartidoInterno;

/**
 * Pruebas de integración de equipos y partidos internos: RF1, RF12, RF13, RF14, RF22, RF23 y RF24.
 * Cada prueba arma su propio club con ClubDePruebaDeportes.
 */
public class GestorDeportesTest {

	private static final LocalDate EN_UNA_SEMANA = LocalDate.of(2026, 11, 8);
	private static final LocalDate EN_DOS_SEMANAS = LocalDate.of(2026, 11, 15);

	private ClubDePruebaDeportes datos;
	private GestorDeportes gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaDeportes();
		gestor = new GestorDeportes(datos.club);
	}

	private PartidoInterno programarAguilasLobos(LocalDate fecha) throws Exception {
		return gestor.programarPartidoInterno(datos.e1, "Águilas FC", "Lobos FC", fecha, "práctica");
	}

	// RF22: crear equipos y asignar entrenador

	@Test
	public void rf22ElAdministradorCreaUnEquipoYLeAsignaEntrenador() throws Exception {
		Equipo halcones = gestor.crearEquipo(datos.admin, "Halcones FC", datos.futbol, datos.mayores, 12);
		gestor.asignarEntrenador(datos.admin, "Halcones FC", 1);

		assertSame(halcones, datos.club.buscarEquipo("halcones fc"));
		assertSame(datos.e1, halcones.getEntrenador());
		assertTrue(halcones.getJugadores().isEmpty());
	}

	@Test
	public void rf22RechazaNombreRepetidoCupoBajoYUsuariosSinPermiso() {
		assertThrows(DuplicadoException.class,
				() -> gestor.crearEquipo(datos.admin, "Águilas FC", datos.futbol, datos.mayores, 12));
		assertThrows(CupoExcedidoException.class,
				() -> gestor.crearEquipo(datos.admin, "Halcones FC", datos.futbol, datos.mayores, 6));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.crearEquipo(datos.e1, "Halcones FC", datos.futbol, datos.mayores, 12));
		assertThrows(NoEncontradoException.class, () -> gestor.asignarEntrenador(datos.admin, "Lobos FC", 2));
	}

	// RF23: inscribir jugadores

	@Test
	public void rf23InscribeAUnSocioConNumeroYPosicion() throws Exception {
		JugadorEquipo jugador = gestor.inscribirJugador(datos.admin, "S1", "Águilas FC", 20, "arquero");

		assertTrue(datos.aguilas.getJugadoresActivos().contains(jugador));
		assertTrue(datos.s1.getParticipaciones().contains(jugador));
		assertFalse(datos.aguilas.numeroDisponible(20));
	}

	@Test
	public void rf23RechazaNumeroUsadoEquipoLlenoYSegundoEquipoDelMismoDeporte() {
		assertThrows(DuplicadoException.class, () -> gestor.inscribirJugador(datos.admin, "S1", "Águilas FC", 3, "defensa"));
		assertThrows(CupoExcedidoException.class, () -> gestor.inscribirJugador(datos.admin, "S1", "Lobos FC", 20, "defensa"));
		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirJugador(datos.admin, "L1", "Águilas FC", 20, "defensa"));
		assertThrows(DatoInvalidoException.class, () -> gestor.inscribirJugador(datos.admin, "S1", "Águilas FC", 0, "defensa"));
	}

	@Test
	public void rf23LaEdadQueCuentaEsLaDelPrimeroDeEnero() throws Exception {
		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirJugador(datos.admin, "S14", "Pumas", 7, "volante"));
		assertNotNull(gestor.inscribirJugador(datos.admin, "S15", "Pumas", 7, "volante"));
		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirJugador(datos.admin, "S15", "Pumas", 8, "volante"));
	}

	// RF24: bajas

	@Test
	public void rf24AlRetirarAUnJugadorSuNumeroQuedaLibre() throws Exception {
		gestor.retirarJugador(datos.admin, "Águilas FC", 3);

		assertTrue(datos.aguilas.numeroDisponible(3));
		assertFalse(datos.jugador(datos.aguilas, 3).estaActivo());
		assertNotNull(gestor.inscribirJugador(datos.admin, "S1", "Águilas FC", 3, "defensa"));
	}

	@Test
	public void rf24NoSeDaDeBajaUnEquipoConPartidosPendientes() throws Exception {
		PartidoInterno partido = programarAguilasLobos(EN_UNA_SEMANA);
		assertThrows(EstadoInvalidoException.class, () -> gestor.darDeBajaEquipo(datos.admin, "Lobos FC"));

		gestor.registrarResultadoInterno(datos.e1, partido, 1, 0);
		gestor.darDeBajaEquipo(datos.admin, "Lobos FC");

		assertFalse(datos.lobos.estaActivo());
		assertTrue(datos.lobos.getJugadoresActivos().isEmpty());
		assertThrows(EstadoInvalidoException.class, () -> gestor.darDeBajaEquipo(datos.admin, "Lobos FC"));
		assertThrows(EstadoInvalidoException.class, () -> gestor.inscribirJugador(datos.admin, "S1", "Lobos FC", 20, "volante"));
	}

	// RF12: programar partidos internos

	@Test
	public void rf12ElEntrenadorProgramaUnPartidoInterno() throws Exception {
		PartidoInterno partido = programarAguilasLobos(EN_UNA_SEMANA);

		assertEquals(1, partido.getId());
		assertSame(partido, datos.club.buscarPartidoInterno(1));
		assertFalse(partido.isJugado());
	}

	@Test
	public void rf12RechazaMismoEquipoOtroDeporteFechaPasadaYAlAdministrador() {
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.programarPartidoInterno(datos.e1, "Lobos FC", "Lobos FC", EN_UNA_SEMANA, "práctica"));
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.programarPartidoInterno(datos.e1, "Lobos FC", "Titanes", EN_UNA_SEMANA, "práctica"));
		assertThrows(DatoInvalidoException.class, () -> programarAguilasLobos(ClubDePruebaDeportes.HOY));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.programarPartidoInterno(datos.admin, "Águilas FC", "Lobos FC", EN_UNA_SEMANA, "práctica"));
	}

	@Test
	public void rf12SinElMinimoDeHabilitadosNoSeProgramaYNoSeGastaElId() throws Exception {
		gestor.retirarJugador(datos.admin, "Lobos FC", 7);

		assertThrows(ReglaDeportivaException.class, () -> programarAguilasLobos(EN_UNA_SEMANA));
		assertEquals(1, datos.club.siguienteIdPartido());
	}

	// RF13: resultados y estadísticas

	@Test
	public void rf13RegistraElResultadoUnaSolaVez() throws Exception {
		PartidoInterno partido = programarAguilasLobos(EN_UNA_SEMANA);
		assertThrows(DatoInvalidoException.class, () -> gestor.registrarResultadoInterno(datos.e1, partido, -1, 0));

		gestor.registrarResultadoInterno(datos.e1, partido, 2, 2);

		assertTrue(partido.isJugado());
		assertEquals(2, partido.getMarcadorLocal());
		assertThrows(EstadoInvalidoException.class, () -> gestor.registrarResultadoInterno(datos.e1, partido, 3, 2));
	}

	@Test
	public void rf13EnBaloncestoNoHayEmpates() throws Exception {
		PartidoInterno partido = gestor.programarPartidoInterno(datos.e1, "Titanes", "Toros", EN_UNA_SEMANA, "evaluación");

		assertThrows(ReglaDeportivaException.class, () -> gestor.registrarResultadoInterno(datos.e1, partido, 80, 80));
		gestor.registrarResultadoInterno(datos.e1, partido, 80, 78);
		assertTrue(partido.isJugado());
	}

	@Test
	public void rf13LaEstadisticaDebeSerDelDeporteYDeUnJugadorDelPartido() throws Exception {
		PartidoInterno partido = programarAguilasLobos(EN_UNA_SEMANA);
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		JugadorEquipo t1 = datos.jugador(datos.titanes, 1);

		assertThrows(ReglaDeportivaException.class,
				() -> gestor.registrarEstadistica(datos.e1, partido, a1, new EstadisticaBaloncesto(a1, 10, 2)));
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.registrarEstadistica(datos.e1, partido, t1, new EstadisticaFutbol(t1, 1, 0, 0, 0)));

		gestor.registrarEstadistica(datos.e1, partido, a1, new EstadisticaFutbol(a1, 2, 1, 0, 0));
		assertTrue(partido.tieneEstadisticaDe(a1));
		assertThrows(DuplicadoException.class,
				() -> gestor.registrarEstadistica(datos.e1, partido, a1, new EstadisticaFutbol(a1, 1, 0, 0, 0)));
	}

	// RF14: tarjetas y suspensiones

	@Test
	public void rf14UnaRojaSuspendeParaElSiguienteInternoYSeCumpleAlJugarlo() throws Exception {
		PartidoInterno primero = programarAguilasLobos(EN_UNA_SEMANA);
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		gestor.registrarEstadistica(datos.e1, primero, a1, new EstadisticaFutbol(a1, 0, 0, 0, 1));
		gestor.registrarResultadoInterno(datos.e1, primero, 0, 1);

		Suspension suspension = a1.getSuspensionPendiente();
		assertEquals(Suspension.MOTIVO_ROJA, suspension.getMotivo());

		PartidoInterno segundo = programarAguilasLobos(EN_DOS_SEMANAS);
		assertThrows(JugadorNoHabilitadoException.class,
				() -> gestor.registrarEstadistica(datos.e1, segundo, a1, new EstadisticaFutbol(a1, 1, 0, 0, 0)));
		gestor.registrarResultadoInterno(datos.e1, segundo, 1, 1);

		assertSame(segundo, suspension.getCumplidaEn());
		assertNull(a1.getSuspensionPendiente());
	}

	@Test
	public void rf14DosAmarillasSuspendenYUnEquipoJustoNoAlcanzaElMinimo() throws Exception {
		PartidoInterno primero = programarAguilasLobos(EN_UNA_SEMANA);
		JugadorEquipo l1 = datos.jugador(datos.lobos, 1);
		gestor.registrarEstadistica(datos.e1, primero, l1, new EstadisticaFutbol(l1, 0, 0, 2, 0));
		gestor.registrarResultadoInterno(datos.e1, primero, 2, 0);

		assertEquals(Suspension.MOTIVO_DOS_AMARILLAS, l1.getSuspensionPendiente().getMotivo());
		// Lobos tiene 7 jugadores y uno suspendido: quedan 6 y el fútbol pide 7
		assertThrows(ReglaDeportivaException.class, () -> programarAguilasLobos(EN_DOS_SEMANAS));
	}

	// RF1: información deportiva del socio

	@Test
	public void rf1ElSocioVeSusEquiposYLosPartidosDeCadaUno() throws Exception {
		PartidoInterno partido = programarAguilasLobos(EN_UNA_SEMANA);
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		gestor.registrarEstadistica(datos.e1, partido, a1, new EstadisticaFutbol(a1, 1, 0, 0, 0));

		List<JugadorEquipo> participaciones = gestor.consultarInformacion(a1.getSocio());
		List<Partido> partidos = gestor.consultarPartidosDe(a1.getSocio(), participaciones.get(0));

		assertEquals(1, participaciones.size());
		assertSame(datos.aguilas, participaciones.get(0).getEquipo());
		assertEquals(1, partidos.size());
		assertNotNull(partidos.get(0).getEstadisticaDe(a1));
	}

	@Test
	public void rf1NadieConsultaLaInformacionDeOtroSocio() {
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);

		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarPartidosDe(datos.s1, a1));
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarInformacion(datos.admin));
		assertTrue(datos.s1.getParticipaciones().isEmpty());
	}
}
