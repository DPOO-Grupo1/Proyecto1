package modelo.deportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.competencias.Competencia;
import modelo.competencias.EquipoExterno;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.Liga;
import modelo.partidos.PartidoCompetencia;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de Equipo (cupo, números, baja) y de dónde aplica una suspensión.
 */
public class EquipoYSuspensionTest {

	private static final LocalDate FECHA = LocalDate.of(2026, 11, 8);

	private DeporteEquipo futbol;
	private Categoria mayores;
	private Equipo aguilas;
	private Equipo lobos;
	private JugadorEquipo j1;
	private JugadorEquipo j2;

	@BeforeEach
	public void setUp() {
		futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		mayores = new Categoria("Mayores", 18, 0);
		aguilas = new Equipo("Águilas", futbol, mayores, 2);
		lobos = new Equipo("Lobos", futbol, mayores, 10);
		j1 = agregar(aguilas, "j1", 10);
		j2 = agregar(aguilas, "j2", 7);
	}

	private JugadorEquipo agregar(Equipo equipo, String login, int numero) {
		Socio socio = new Socio(login, "pass", "Nombre", login, login.toUpperCase(), LocalDate.of(1995, 1, 1));
		JugadorEquipo jugador = new JugadorEquipo(socio, equipo, numero, "volante");
		equipo.agregarJugador(jugador);
		socio.agregarParticipacion(jugador);
		return jugador;
	}

	@Test
	public void elCupoYLosNumerosSoloCuentanJugadoresActivos() {
		assertFalse(aguilas.tieneCupo());
		assertFalse(aguilas.numeroDisponible(10));

		j1.desactivar();

		assertTrue(aguilas.tieneCupo());
		assertTrue(aguilas.numeroDisponible(10));
		assertEquals(1, aguilas.getJugadoresActivos().size());
		assertEquals(2, aguilas.getJugadores().size());
	}

	@Test
	public void alDarDeBajaElEquipoSusJugadoresQuedanInactivos() {
		aguilas.desactivar();

		assertFalse(aguilas.estaActivo());
		assertFalse(j1.estaActivo());
		assertFalse(j2.getSocio().juegaEnDeporte(futbol));
	}

	@Test
	public void laSuspensionDeUnInternoAplicaSoloEnLosSiguientesInternos() {
		PartidoInterno origen = new PartidoInterno(1, FECHA, aguilas, lobos, "práctica");
		PartidoInterno siguiente = new PartidoInterno(2, FECHA.plusDays(7), aguilas, lobos, "práctica");
		Competencia liga = new Competencia("Copa", "Liga", 2026, futbol, mayores, 1, 3, new Liga(false));
		EquipoInscrito inscrito = new EquipoInscrito(aguilas, aguilas.getJugadoresActivos(), null, FECHA);
		PartidoCompetencia oficial = new PartidoCompetencia(3, FECHA, liga, inscrito,
				new EquipoExterno("Rival", "U", null), "Cancha", "Fecha 1");

		j1.agregarSuspension(new Suspension(Suspension.MOTIVO_ROJA, origen));

		assertTrue(j1.estaHabilitadoPara(origen));
		assertFalse(j1.estaHabilitadoPara(siguiente));
		assertTrue(j1.estaHabilitadoPara(oficial));
		assertFalse(j1.estaHabilitado());
	}

	@Test
	public void unaSuspensionCumplidaSigueImpidiendoJugarEsePartido() {
		PartidoInterno origen = new PartidoInterno(1, FECHA, aguilas, lobos, "práctica");
		PartidoInterno siguiente = new PartidoInterno(2, FECHA.plusDays(7), aguilas, lobos, "práctica");
		PartidoInterno otro = new PartidoInterno(3, FECHA.plusDays(14), aguilas, lobos, "práctica");
		Suspension suspension = new Suspension(Suspension.MOTIVO_DOS_AMARILLAS, origen);
		j1.agregarSuspension(suspension);

		suspension.cumplir(siguiente);

		assertNull(j1.getSuspensionPendiente());
		assertSame(suspension, j1.getSuspensionQueImpide(siguiente));
		assertTrue(j1.estaHabilitadoPara(otro));
	}
}
