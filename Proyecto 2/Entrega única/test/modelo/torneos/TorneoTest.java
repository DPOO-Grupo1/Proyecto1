package modelo.torneos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.usuarios.Empleado;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de Torneo: cupos preferenciales, cupos libres, recaudo y búsquedas.
 */
public class TorneoTest {

	private static final LocalDate FECHA = LocalDate.of(2026, 11, 21);

	private TorneoIndividual torneo;
	private Socio socio;
	private Empleado empleado;
	private ParticipanteExterno externo;

	@BeforeEach
	public void setUp() {
		torneo = new TorneoIndividual("Cuadro de tenis", FECHA, 10, new Competitivo(30000), new EliminacionDirecta(),
				new Disciplina("Tenis", 2), Nivel.INTERMEDIO, Modalidad.SENCILLOS);
		socio = new Socio("s1", "pass", "Ana", "Gómez", "S1", LocalDate.of(1995, 3, 10));
		empleado = new Empleado("e1", "pass", "Marta", "López", 1);
		externo = new ParticipanteExterno("x1", "pass", "Valentina", "Rojas", "1001", true);
	}

	@Test
	public void reservaElVeintePorCientoRedondeadoHaciaArriba() {
		assertEquals(2, torneo.getCuposReservados());
		Torneo seis = new TorneoIndividual("Seis", FECHA, 6, new Recreativo(10), new TodosContraTodos(),
				new Disciplina("Pádel", 2), Nivel.INTERMEDIO, Modalidad.DOBLES);
		assertEquals(2, seis.getCuposReservados());
		Torneo cinco = new TorneoIndividual("Cinco", FECHA, 5, new Recreativo(10), new TodosContraTodos(),
				new Disciplina("Pádel", 2), Nivel.INTERMEDIO, Modalidad.DOBLES);
		assertEquals(1, cinco.getCuposReservados());
	}

	@Test
	public void losCuposReservadosNoSeCuentanComoRegulares() {
		assertEquals(2, torneo.getCuposPreferencialesLibres());
		assertEquals(8, torneo.getCuposRegularesLibres());

		torneo.agregarInscripcion(new InscripcionIndividual(socio, null, FECHA, true));
		torneo.agregarInscripcion(new InscripcionIndividual(externo, null, FECHA, false));

		assertEquals(1, torneo.getCuposPreferencialesLibres());
		assertEquals(7, torneo.getCuposRegularesLibres());
	}

	@Test
	public void alLiberarLosPreferencialesPasanASerRegulares() {
		torneo.agregarInscripcion(new InscripcionIndividual(socio, null, FECHA, true));
		torneo.liberarCuposPreferenciales();

		assertEquals(0, torneo.getCuposPreferencialesLibres());
		assertEquals(9, torneo.getCuposRegularesLibres());
	}

	@Test
	public void alRetirarseQuedaElCupoLibre() {
		InscripcionIndividual inscripcion = new InscripcionIndividual(externo, null, FECHA, false);
		torneo.agregarInscripcion(inscripcion);
		assertEquals(7, torneo.getCuposRegularesLibres());

		torneo.retirarInscripcion(inscripcion);

		assertEquals(8, torneo.getCuposRegularesLibres());
		assertNull(torneo.buscarInscripcionDe(externo));
	}

	@Test
	public void encuentraLaInscripcionDeCadaJugadorDeUnaPareja() {
		InscripcionIndividual pareja = new InscripcionIndividual(socio, externo, FECHA, false);
		torneo.agregarInscripcion(pareja);

		assertSame(pareja, torneo.buscarInscripcionDe(socio));
		assertSame(pareja, torneo.buscarInscripcionDe(externo));
		assertNull(torneo.buscarInscripcionDe(empleado));
	}

	@Test
	public void losEmpleadosNoPaganTarifaEnUnCompetitivo() {
		InscripcionIndividual delEmpleado = new InscripcionIndividual(empleado, null, FECHA, false);
		InscripcionIndividual mixta = new InscripcionIndividual(socio, empleado, FECHA, false);
		InscripcionIndividual deDos = new InscripcionIndividual(socio, externo, FECHA, false);

		assertEquals(0, torneo.getTipo().tarifaPara(delEmpleado));
		assertEquals(30000, torneo.getTipo().tarifaPara(mixta));
		assertEquals(60000, torneo.getTipo().tarifaPara(deDos));
	}

	@Test
	public void elRecaudoSumaLoQuePagoCadaInscrito() {
		InscripcionIndividual primera = new InscripcionIndividual(socio, null, FECHA, false);
		primera.setValorPagado(30000);
		InscripcionIndividual segunda = new InscripcionIndividual(empleado, null, FECHA, false);
		torneo.agregarInscripcion(primera);
		torneo.agregarInscripcion(segunda);

		assertEquals(30000, torneo.getRecaudo());
	}

	@Test
	public void empiezaConInscripcionesAbiertasYSinRondas() {
		assertTrue(torneo.estaAbierto());
		assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado());
		assertEquals(0, torneo.getRondaActual());
		assertFalse(torneo.admiteEmpate());
	}
}
