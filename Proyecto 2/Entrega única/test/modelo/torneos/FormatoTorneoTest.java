package modelo.torneos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.usuarios.ParticipanteExterno;

/**
 * Pruebas unitarias de los dos formatos: cómo arman los encuentros, cuándo terminan
 * y cómo ordenan las posiciones.
 */
public class FormatoTorneoTest {

	private static final LocalDate FECHA = LocalDate.of(2026, 11, 21);

	private Torneo crearIndividual(FormatoTorneo formato, int inscritos) {
		Torneo torneo = new TorneoIndividual("Prueba", FECHA, 8, new Recreativo(10), formato,
				new Disciplina("Tenis", 2), Nivel.INTERMEDIO, Modalidad.SENCILLOS);
		for (int i = 1; i <= inscritos; i++) {
			ParticipanteExterno jugador = new ParticipanteExterno("x" + i, "pass", "Jugador" + i, "Prueba", "" + i, true);
			torneo.agregarInscripcion(new InscripcionIndividual(jugador, null, FECHA, false));
		}
		return torneo;
	}

	@Test
	public void todosContraTodosArmaUnEncuentroPorPareja() {
		Torneo torneo = crearIndividual(new TodosContraTodos(), 4);

		List<EncuentroTorneo> encuentros = torneo.getFormato().crearEncuentrosIniciales(torneo);

		assertEquals(6, encuentros.size());
		assertEquals(1, encuentros.get(0).getNumero());
		assertEquals(6, encuentros.get(5).getNumero());
	}

	@Test
	public void todosContraTodosTerminaCuandoSeJueganTodos() {
		Torneo torneo = crearIndividual(new TodosContraTodos(), 3);
		torneo.agregarEncuentros(torneo.getFormato().crearEncuentrosIniciales(torneo));
		InscripcionTorneo primero = torneo.getInscripciones().get(0);

		torneo.getEncuentros().get(0).registrarResultado(2, 0);
		torneo.getEncuentros().get(1).registrarResultado(2, 1);
		assertFalse(torneo.getFormato().estaTerminado(torneo));
		assertNull(torneo.getGanador());

		torneo.getEncuentros().get(2).registrarResultado(0, 2);
		assertTrue(torneo.getFormato().estaTerminado(torneo));
		assertSame(primero, torneo.getGanador());
		assertEquals(2, torneo.calcularPosiciones().get(0).getPuntos());
	}

	@Test
	public void todosContraTodosDesempataPorDiferencia() {
		Torneo torneo = new TorneoConjunto("Liga", FECHA, 4, new Recreativo(10), new TodosContraTodos(),
				new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false), new Categoria("Mayores", 18, 0));
		for (int i = 1; i <= 3; i++) {
			ParticipanteExterno jugador = new ParticipanteExterno("x" + i, "pass", "Equipo" + i, "Prueba", "" + i, true);
			torneo.agregarInscripcion(new InscripcionIndividual(jugador, null, FECHA, false));
		}
		torneo.agregarEncuentros(torneo.getFormato().crearEncuentrosIniciales(torneo));
		torneo.getEncuentros().get(0).registrarResultado(1, 1);
		torneo.getEncuentros().get(1).registrarResultado(4, 0);
		torneo.getEncuentros().get(2).registrarResultado(1, 0);

		List<FilaTorneo> posiciones = torneo.calcularPosiciones();
		// 1 y 2 suman 4 puntos; el 1 tiene mejor diferencia (+4 contra +1)
		assertSame(torneo.getInscripciones().get(0), posiciones.get(0).getParticipante());
		assertSame(torneo.getInscripciones().get(1), posiciones.get(1).getParticipante());
		assertEquals(4, posiciones.get(0).getPuntos());
		assertEquals(4, posiciones.get(1).getPuntos());
	}

	@Test
	public void eliminacionEnfrentaAlMejorSembradoContraElPeor() {
		Torneo torneo = crearIndividual(new EliminacionDirecta(), 4);

		List<EncuentroTorneo> ronda = torneo.getFormato().crearEncuentrosIniciales(torneo);

		assertEquals(2, ronda.size());
		assertSame(torneo.getInscripciones().get(0), ronda.get(0).getLocal());
		assertSame(torneo.getInscripciones().get(3), ronda.get(0).getVisitante());
		assertSame(torneo.getInscripciones().get(1), ronda.get(1).getLocal());
		assertSame(torneo.getInscripciones().get(2), ronda.get(1).getVisitante());
	}

	@Test
	public void eliminacionSiembraPrimeroALosPreferenciales() {
		Torneo torneo = crearIndividual(new EliminacionDirecta(), 3);
		ParticipanteExterno mejor = new ParticipanteExterno("x9", "pass", "Mejor", "Prueba", "9", false);
		InscripcionIndividual preferencial = new InscripcionIndividual(mejor, null, FECHA, true);
		torneo.agregarInscripcion(preferencial);

		List<EncuentroTorneo> ronda = torneo.getFormato().crearEncuentrosIniciales(torneo);

		assertSame(preferencial, ronda.get(0).getLocal());
	}

	@Test
	public void conImparesElMejorSembradoPasaDirecto() {
		Torneo torneo = crearIndividual(new EliminacionDirecta(), 3);

		List<EncuentroTorneo> ronda = torneo.getFormato().crearEncuentrosIniciales(torneo);

		assertEquals(2, ronda.size());
		assertTrue(ronda.get(0).isPaseDirecto());
		assertTrue(ronda.get(0).isJugado());
		assertSame(torneo.getInscripciones().get(0), ronda.get(0).getGanador());
	}

	@Test
	public void eliminacionArmaLaFinalYDeclaraCampeon() {
		Torneo torneo = crearIndividual(new EliminacionDirecta(), 4);
		FormatoTorneo formato = torneo.getFormato();
		torneo.agregarEncuentros(formato.crearEncuentrosIniciales(torneo));
		torneo.getEncuentros().get(0).registrarResultado(2, 0);
		assertTrue(formato.crearSiguienteRonda(torneo).isEmpty());

		torneo.getEncuentros().get(1).registrarResultado(1, 2);
		List<EncuentroTorneo> laFinal = formato.crearSiguienteRonda(torneo);
		assertEquals(1, laFinal.size());
		assertEquals(2, laFinal.get(0).getRonda());
		torneo.agregarEncuentros(laFinal);
		assertFalse(formato.estaTerminado(torneo));

		laFinal.get(0).registrarResultado(0, 2);
		assertTrue(formato.estaTerminado(torneo));
		InscripcionTorneo campeon = torneo.getInscripciones().get(2);
		assertSame(campeon, torneo.getGanador());
		assertEquals("Campeón", torneo.calcularPosiciones().get(0).getEtapa());
		assertEquals("Eliminado en la ronda 2", torneo.calcularPosiciones().get(1).getEtapa());
	}
}
