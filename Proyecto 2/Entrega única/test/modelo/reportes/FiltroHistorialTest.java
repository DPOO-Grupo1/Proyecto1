package modelo.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.academias.CanchaPadel;
import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de FiltroHistorial y Historial: qué acepta cada filtro del historial global (RF28).
 */
public class FiltroHistorialTest {

	private static final LocalDate DIA = LocalDate.of(2026, 11, 2);

	private Socio ana;
	private Socio luis;
	private Entrenador marta;
	private Entrenador julian;
	private DeporteEquipo futbol;
	private Categoria mayores;
	private Equipo aguilas;
	private Equipo lobos;
	private Reserva reservaDeAna;
	private PartidoInterno partido;
	private SesionEntrenamiento sesionDeMarta;

	@BeforeEach
	public void setUp() {
		ana = new Socio("s1", "pass", "Ana", "Pérez", "S1", LocalDate.of(1995, 5, 5));
		luis = new Socio("s2", "pass", "Luis", "Gómez", "S2", LocalDate.of(1990, 1, 1));
		marta = new Entrenador("e1", "pass", "Marta", "López", 1);
		julian = new Entrenador("e2", "pass", "Julián", "Prieto", 2);
		futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		mayores = new Categoria("Mayores", 18, Categoria.SIN_EDAD_MAXIMA);
		aguilas = new Equipo("Águilas", futbol, mayores, 10);
		lobos = new Equipo("Lobos", futbol, mayores, 10);

		CanchaPadel cancha = new CanchaPadel("P1", "Cancha de pádel 1", new Disciplina("Pádel", 2));
		reservaDeAna = new Reserva("R1", ana, cancha, DIA, LocalTime.of(8, 0), LocalTime.of(9, 30), 4, Modalidad.DOBLES);
		partido = new PartidoInterno(1, DIA, aguilas, lobos, "práctica");
		sesionDeMarta = new SesionEntrenamiento("SE1", marta, cancha, DIA, LocalTime.of(10, 0), 60);
	}

	private FiltroHistorial filtro(LocalDate desde, LocalDate hasta, Equipo equipo, Socio socio, Entrenador entrenador) {
		return new FiltroHistorial(desde, hasta, equipo, socio, entrenador, null, null);
	}

	@Test
	public void sinFiltrosAceptaTodo() {
		FiltroHistorial todo = filtro(null, null, null, null, null);

		assertTrue(todo.acepta(reservaDeAna));
		assertTrue(todo.acepta(partido));
		assertTrue(todo.acepta(sesionDeMarta));
	}

	@Test
	public void elRangoDeFechasIncluyeLosExtremos() {
		assertTrue(filtro(DIA, DIA, null, null, null).acepta(reservaDeAna));
		assertFalse(filtro(DIA.plusDays(1), null, null, null, null).acepta(reservaDeAna));
		assertFalse(filtro(null, DIA.minusDays(1), null, null, null).acepta(partido));
	}

	@Test
	public void elFiltroPorSocioSoloAceptaSusReservas() {
		assertTrue(filtro(null, null, null, ana, null).acepta(reservaDeAna));
		assertFalse(filtro(null, null, null, luis, null).acepta(reservaDeAna));
	}

	@Test
	public void elFiltroPorEntrenadorSoloAceptaSusSesionesYNoLosPartidos() {
		assertTrue(filtro(null, null, null, null, marta).acepta(sesionDeMarta));
		assertFalse(filtro(null, null, null, null, julian).acepta(sesionDeMarta));
		assertFalse(filtro(null, null, null, null, marta).acepta(partido));
	}

	@Test
	public void elFiltroPorEquipoAceptaLosPartidosDondeJuegaYNoLasReservas() {
		Equipo otro = new Equipo("Pumas", futbol, mayores, 10);

		assertTrue(filtro(null, null, aguilas, null, null).acepta(partido));
		assertFalse(filtro(null, null, otro, null, null).acepta(partido));
		assertFalse(filtro(null, null, aguilas, null, null).acepta(reservaDeAna));
	}

	@Test
	public void elHistorialGuardaCadaTipoEnSuLista() {
		Historial historial = new Historial();
		historial.agregarReserva(reservaDeAna);
		historial.agregarPartido(partido);
		historial.agregarSesion(sesionDeMarta);

		assertEquals(1, historial.getReservas().size());
		assertEquals(1, historial.getPartidos().size());
		assertEquals(1, historial.getSesiones().size());
		assertTrue(historial.getPartidosPractica().isEmpty());
	}
}
