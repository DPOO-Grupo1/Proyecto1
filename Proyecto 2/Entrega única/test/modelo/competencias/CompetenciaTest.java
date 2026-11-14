package modelo.competencias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.PartidoCompetencia;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de Competencia: la tabla se calcula con los partidos jugados,
 * los formatos deciden grupos y clasificados, y las amarillas se cuentan desde la última sanción.
 */
public class CompetenciaTest {

	private static final LocalDate FECHA = LocalDate.of(2026, 11, 8);

	private DeporteEquipo futbol;
	private Categoria mayores;
	private Competencia liga;
	private EquipoInscrito aguilas;
	private EquipoExterno andes;
	private EquipoExterno javeriana;
	private JugadorEquipo jugador;
	private int siguienteId;

	@BeforeEach
	public void setUp() {
		futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		mayores = new Categoria("Mayores", 18, 0);
		liga = new Competencia("Copa", "Liga", 2026, futbol, mayores, 1, 2, new Liga(false));
		Equipo equipo = new Equipo("Águilas", futbol, mayores, 10);
		Socio socio = new Socio("j1", "pass", "Ana", "Gómez", "J1", LocalDate.of(1995, 1, 1));
		jugador = new JugadorEquipo(socio, equipo, 10, "delantero");
		equipo.agregarJugador(jugador);
		aguilas = new EquipoInscrito(equipo, equipo.getJugadoresActivos(), null, FECHA);
		andes = new EquipoExterno("Andes", "Uniandes", null);
		javeriana = new EquipoExterno("Javeriana", "PUJ", null);
		liga.agregarParticipante(aguilas);
		liga.agregarParticipante(andes);
		liga.agregarParticipante(javeriana);
		siguienteId = 1;
	}

	private PartidoCompetencia partido(ParticipanteCompetencia local, ParticipanteCompetencia visitante) {
		PartidoCompetencia partido = new PartidoCompetencia(siguienteId, FECHA, liga, local, visitante, "Cancha", "Fecha");
		siguienteId++;
		liga.agregarPartido(partido);
		return partido;
	}

	@Test
	public void laTablaSoloCuentaPartidosJugados() {
		partido(aguilas, andes).registrarResultado(2, 0);
		partido(andes, javeriana);

		List<FilaTabla> tabla = liga.calcularTabla(null);

		// Javeriana no ha jugado y queda sobre Andes, que perdió por 2
		assertSame(aguilas, tabla.get(0).getParticipante());
		assertSame(javeriana, tabla.get(1).getParticipante());
		assertEquals(0, tabla.get(1).getJugados());
		assertEquals(-2, tabla.get(2).getDiferencia());
	}

	@Test
	public void elEnfrentamientoDirectoSumaLosPuntosEntreLosDos() {
		partido(aguilas, andes).registrarResultado(1, 0);
		partido(andes, aguilas).registrarResultado(1, 1);

		assertEquals(4, liga.puntosEnfrentamientoDirecto(aguilas, andes));
		assertEquals(1, liga.puntosEnfrentamientoDirecto(andes, aguilas));
		assertEquals(0, liga.puntosEnfrentamientoDirecto(aguilas, javeriana));
	}

	@Test
	public void cadaFormatoDecideQueGruposAcepta() {
		GruposEliminacion grupos = new GruposEliminacion(3, 2);

		assertTrue(new Liga(false).grupoValido(null));
		assertFalse(new Liga(false).grupoValido("A"));
		assertEquals(3, grupos.getGrupos().size());
		assertTrue(grupos.grupoValido("c"));
		assertFalse(grupos.grupoValido("D"));
		assertFalse(grupos.grupoValido(null));
	}

	@Test
	public void lasAmarillasSeCuentanDesdeLaUltimaSancion() {
		PartidoCompetencia primero = partido(aguilas, andes);
		primero.agregarEstadistica(new EstadisticaFutbol(jugador, 0, 0, 1, 0));
		PartidoCompetencia segundo = partido(javeriana, aguilas);
		segundo.agregarEstadistica(new EstadisticaFutbol(jugador, 0, 0, 1, 0));
		assertEquals(2, liga.contarAmarillasDesdeUltimaSancion(jugador));

		jugador.agregarSuspension(new Suspension(Suspension.MOTIVO_ACUMULACION, segundo));

		assertEquals(0, liga.contarAmarillasDesdeUltimaSancion(jugador));
	}

	@Test
	public void unaRojaODosAmarillasDanSuMotivoDeSuspension() {
		assertEquals(Suspension.MOTIVO_ROJA, new EstadisticaFutbol(jugador, 0, 0, 1, 1).motivoSuspension());
		assertEquals(Suspension.MOTIVO_DOS_AMARILLAS, new EstadisticaFutbol(jugador, 0, 0, 2, 0).motivoSuspension());
		assertNull(new EstadisticaFutbol(jugador, 3, 1, 1, 0).motivoSuspension());
	}
}
