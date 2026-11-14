package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.ConflictoHorarioException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.competencias.Competencia;
import modelo.competencias.EquipoExterno;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.Liga;
import modelo.partidos.PartidoCompetencia;
import modelo.torneos.BonoDescuento;
import modelo.torneos.Competitivo;
import modelo.torneos.EliminacionDirecta;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.InscripcionEquipo;
import modelo.torneos.InscripcionIndividual;
import modelo.torneos.Recreativo;
import modelo.torneos.TodosContraTodos;
import modelo.torneos.Torneo;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Usuario;

/**
 * Pruebas de integración de los torneos, al menos una por historia de usuario (HU_T1 a HU_T10).
 * Cada prueba arma su propio club con ClubDePruebaTorneos.
 */
public class GestorTorneosTest {

	private ClubDePruebaTorneos datos;
	private GestorTorneos gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaTorneos();
		gestor = new GestorTorneos(datos.club);
	}

	private Torneo crearTenis(int cupos, boolean competitivo) throws Exception {
		return gestor.crearTorneoIndividual(datos.admin, "Cuadro de tenis", ClubDePruebaTorneos.SABADO, cupos,
				datos.tenis, Nivel.INTERMEDIO, Modalidad.SENCILLOS,
				competitivo ? new Competitivo(40000) : new Recreativo(20), new EliminacionDirecta());
	}

	private Torneo crearFutbol(int cupos) throws Exception {
		return gestor.crearTorneoConjunto(datos.admin, "Copa de fútbol", ClubDePruebaTorneos.SABADO, cupos,
				datos.futbol, datos.mayores, new Recreativo(20), new TodosContraTodos());
	}

	// HU_T1: crear torneos

	@Test
	public void hut1ElAdministradorCreaUnTorneoConInscripcionesAbiertas() throws Exception {
		Torneo torneo = crearTenis(8, true);

		assertSame(torneo, datos.club.buscarTorneo("Cuadro de tenis"));
		assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado());
		assertEquals(2, torneo.getCuposReservados());
	}

	@Test
	public void hut1SoloElAdministradorCreaTorneos() {
		assertThrows(PermisoDenegadoException.class, () -> gestor.crearTorneoConjunto(datos.s1, "Copa", ClubDePruebaTorneos.SABADO,
				4, datos.futbol, datos.mayores, new Recreativo(20), new TodosContraTodos()));
	}

	@Test
	public void hut1RechazaNombreRepetidoFechaPasadaYValoresInvalidos() throws Exception {
		crearTenis(8, true);

		assertThrows(DuplicadoException.class, () -> crearTenis(8, false));
		assertThrows(DatoInvalidoException.class, () -> gestor.crearTorneoConjunto(datos.admin, "Viejo",
				LocalDate.of(2026, 10, 1), 4, datos.futbol, datos.mayores, new Recreativo(20), new TodosContraTodos()));
		assertThrows(DatoInvalidoException.class, () -> gestor.crearTorneoConjunto(datos.admin, "Sin tarifa",
				ClubDePruebaTorneos.SABADO, 4, datos.futbol, datos.mayores, new Competitivo(0), new TodosContraTodos()));
		assertThrows(DatoInvalidoException.class, () -> gestor.crearTorneoConjunto(datos.admin, "Un cupo",
				ClubDePruebaTorneos.SABADO, 1, datos.futbol, datos.mayores, new Recreativo(20), new TodosContraTodos()));
	}

	// HU_T2: participantes externos

	@Test
	public void hut2ElEstudianteRegistradoPuedeIniciarSesion() throws Exception {
		gestor.registrarParticipanteExterno(datos.admin, "x2", "clave", "Andrés", "Pardo", "2002", true);

		Usuario usuario = new GestorUsuarios(datos.club).autenticar("x2", "clave");
		assertTrue(usuario instanceof ParticipanteExterno);
		assertTrue(((ParticipanteExterno) usuario).isEstudiante());
	}

	@Test
	public void hut2NoSeRepitenLoginNiDocumento() {
		assertThrows(DuplicadoException.class,
				() -> gestor.registrarParticipanteExterno(datos.admin, "s1", "clave", "Otro", "Más", "3003", false));
		assertThrows(DuplicadoException.class,
				() -> gestor.registrarParticipanteExterno(datos.admin, "x9", "clave", "Otro", "Más", "1001", false));
	}

	// HU_T3: inscripción individual y cupos preferenciales

	@Test
	public void hut3ElMejorDelRankingTomaUnCupoPreferencial() throws Exception {
		Torneo torneo = crearTenis(5, false);

		InscripcionIndividual mejor = gestor.inscribirIndividual(datos.s1, torneo, null);
		InscripcionIndividual segundo = gestor.inscribirIndividual(datos.s2, torneo, null);

		assertTrue(mejor.isPreferencial());
		assertFalse(segundo.isPreferencial());
		assertEquals(0, torneo.getCuposPreferencialesLibres());
	}

	@Test
	public void hut3CuandoNoQuedanRegularesSoloEntraElPreferente() throws Exception {
		Torneo torneo = crearTenis(5, false);
		gestor.inscribirIndividual(datos.x1, torneo, null);
		gestor.inscribirIndividual(datos.f1, torneo, null);
		gestor.inscribirIndividual(datos.s5, torneo, null);
		gestor.inscribirIndividual(datos.s3, torneo, null);

		assertThrows(CupoExcedidoException.class, () -> gestor.inscribirIndividual(datos.s2, torneo, null));
		assertTrue(gestor.inscribirIndividual(datos.s1, torneo, null).isPreferencial());
		assertEquals(5, torneo.getInscripciones().size());
	}

	@Test
	public void hut3EnUnCompetitivoElEmpleadoNoPagaYLosDemasSi() throws Exception {
		Torneo torneo = crearTenis(8, true);

		assertEquals(0, gestor.inscribirIndividual(datos.f1, torneo, null).getValorPagado());
		assertEquals(40000, gestor.inscribirIndividual(datos.x1, torneo, null).getValorPagado());
		assertEquals(40000, torneo.getRecaudo());
	}

	@Test
	public void hut3ElEmpleadoDeTurnoEseDiaNoSePuedeInscribir() throws Exception {
		Torneo torneo = crearTenis(8, false);

		assertThrows(ConflictoHorarioException.class, () -> gestor.inscribirIndividual(datos.e1, torneo, null));
	}

	@Test
	public void hut3RechazaOtroNivelDobleInscripcionYAlAdministrador() throws Exception {
		Torneo torneo = crearTenis(8, false);
		gestor.inscribirIndividual(datos.s1, torneo, null);

		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirIndividual(datos.s4, torneo, null));
		assertThrows(DuplicadoException.class, () -> gestor.inscribirIndividual(datos.s1, torneo, null));
		assertThrows(PermisoDenegadoException.class, () -> gestor.inscribirIndividual(datos.admin, torneo, null));
	}

	@Test
	public void hut3EnDoblesSeInscribeConPareja() throws Exception {
		Torneo dobles = gestor.crearTorneoIndividual(datos.admin, "Dobles", ClubDePruebaTorneos.SABADO, 4, datos.tenis,
				Nivel.INTERMEDIO, Modalidad.DOBLES, new Recreativo(15), new EliminacionDirecta());

		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirIndividual(datos.s2, dobles, null));
		InscripcionIndividual pareja = gestor.inscribirIndividual(datos.s2, dobles, datos.x1);
		assertSame(pareja, dobles.buscarInscripcionDe(datos.x1));
		assertThrows(DuplicadoException.class, () -> gestor.inscribirIndividual(datos.s3, dobles, datos.x1));
	}

	// HU_T4: inscripción de equipos

	@Test
	public void hut4ElEntrenadorInscribeSuEquipoCompleto() throws Exception {
		Torneo torneo = crearFutbol(4);

		InscripcionEquipo inscripcion = gestor.inscribirEquipo(datos.e1, torneo, datos.aguilas);

		assertEquals(7, inscripcion.getJugadores().size());
		assertFalse(inscripcion.isPreferencial());
	}

	@Test
	public void hut4RechazaEquipoIncompletoOtroEntrenadorYJugadoresSueltos() throws Exception {
		Torneo torneo = crearFutbol(4);

		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirEquipo(datos.e1, torneo, datos.halcones));
		assertThrows(PermisoDenegadoException.class, () -> gestor.inscribirEquipo(datos.e1, torneo, datos.lobos));
		assertThrows(ReglaDeportivaException.class, () -> gestor.inscribirIndividual(datos.s1, torneo, null));
	}

	@Test
	public void hut4ElEquipoConPuntosEnCompetenciasEsPreferente() throws Exception {
		Competencia copa = new Competencia("Copa ASCUN", "ASCUN", 2026, datos.futbol, datos.mayores, 7, 3, new Liga(false));
		EquipoInscrito lobos = new EquipoInscrito(datos.lobos, datos.lobos.getJugadoresActivos(), null,
				ClubDePruebaTorneos.HOY);
		EquipoExterno rival = new EquipoExterno("Rival", "Universidad", null);
		copa.agregarParticipante(lobos);
		copa.agregarParticipante(rival);
		PartidoCompetencia partido = new PartidoCompetencia(1, ClubDePruebaTorneos.HOY, copa, lobos, rival, "Cancha 1",
				"Fecha 1");
		partido.registrarResultado(2, 0);
		copa.agregarPartido(partido);
		datos.club.agregarCompetencia(copa);
		Torneo torneo = crearFutbol(4);

		assertTrue(gestor.inscribirEquipo(datos.admin, torneo, datos.lobos).isPreferencial());
		assertFalse(gestor.inscribirEquipo(datos.admin, torneo, datos.aguilas).isPreferencial());
	}

	// HU_T5: salirse

	@Test
	public void hut5AlSalirseSeLiberaElCupo() throws Exception {
		Torneo torneo = crearTenis(5, false);
		InscripcionIndividual inscripcion = gestor.inscribirIndividual(datos.x1, torneo, null);
		assertEquals(3, torneo.getCuposRegularesLibres());

		gestor.retirarInscripcion(datos.x1, torneo, inscripcion);

		assertEquals(4, torneo.getCuposRegularesLibres());
		assertTrue(torneo.getInscripciones().isEmpty());
	}

	@Test
	public void hut5NadieRetiraInscripcionesAjenasNiDespuesDeEmpezar() throws Exception {
		Torneo torneo = crearTenis(5, false);
		InscripcionIndividual delExterno = gestor.inscribirIndividual(datos.x1, torneo, null);
		gestor.inscribirIndividual(datos.s1, torneo, null);

		assertThrows(PermisoDenegadoException.class, () -> gestor.retirarInscripcion(datos.s1, torneo, delExterno));
		gestor.iniciarTorneo(datos.admin, torneo);
		assertThrows(EstadoInvalidoException.class, () -> gestor.retirarInscripcion(datos.x1, torneo, delExterno));
	}

	// HU_T6: liberar cupos preferenciales

	@Test
	public void hut6AlLiberarlosEntraUnJugadorRegular() throws Exception {
		Torneo torneo = crearTenis(2, false);
		gestor.inscribirIndividual(datos.x1, torneo, null);
		assertThrows(CupoExcedidoException.class, () -> gestor.inscribirIndividual(datos.s5, torneo, null));

		gestor.liberarCuposPreferenciales(datos.admin, torneo);

		assertFalse(gestor.inscribirIndividual(datos.s5, torneo, null).isPreferencial());
		assertThrows(EstadoInvalidoException.class, () -> gestor.liberarCuposPreferenciales(datos.admin, torneo));
	}

	// HU_T7: iniciar

	@Test
	public void hut7AlIniciarSeArmanLosEncuentrosYSeCierranLasInscripciones() throws Exception {
		Torneo torneo = crearFutbol(4);
		gestor.inscribirEquipo(datos.admin, torneo, datos.aguilas);
		assertThrows(CupoExcedidoException.class, () -> gestor.iniciarTorneo(datos.admin, torneo));
		gestor.inscribirEquipo(datos.admin, torneo, datos.lobos);

		List<EncuentroTorneo> encuentros = gestor.iniciarTorneo(datos.admin, torneo);

		assertEquals(1, encuentros.size());
		assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
		assertThrows(EstadoInvalidoException.class, () -> gestor.inscribirEquipo(datos.admin, torneo, datos.halcones));
	}

	// HU_T8: resultados y premios

	@Test
	public void hut8ElRecreativoTerminaYDaUnBonoACadaJugadorDelGanador() throws Exception {
		Torneo torneo = crearFutbol(4);
		gestor.inscribirEquipo(datos.admin, torneo, datos.aguilas);
		gestor.inscribirEquipo(datos.admin, torneo, datos.lobos);
		gestor.iniciarTorneo(datos.admin, torneo);

		List<BonoDescuento> bonos = gestor.registrarResultado(datos.admin, torneo, torneo.getEncuentros().get(0), 3, 1);

		assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado());
		assertEquals("Águilas FC", torneo.getGanador().getNombre());
		assertEquals(7, bonos.size());
		assertEquals(7, datos.club.getBonos().size());
		assertEquals(20, bonos.get(0).getPorcentaje());
	}

	@Test
	public void hut8LaEliminacionAvanzaDeRondaYElEmpleadoGanadorNoRecibeEfectivo() throws Exception {
		Torneo torneo = crearTenis(4, true);
		gestor.inscribirIndividual(datos.s1, torneo, null);
		gestor.inscribirIndividual(datos.x1, torneo, null);
		gestor.inscribirIndividual(datos.f1, torneo, null);
		gestor.iniciarTorneo(datos.admin, torneo);
		// s1 es preferencial y pasa directo; juegan x1 contra f1
		EncuentroTorneo semifinal = torneo.getEncuentrosDeRonda(1).get(1);
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.registrarResultado(datos.admin, torneo, semifinal, 1, 1));

		gestor.registrarResultado(datos.admin, torneo, semifinal, 0, 2);
		EncuentroTorneo laFinal = torneo.getEncuentrosDeRonda(2).get(0);
		List<BonoDescuento> bonos = gestor.registrarResultado(datos.admin, torneo, laFinal, 1, 2);

		Competitivo tipo = (Competitivo) torneo.getTipo();
		assertTrue(torneo.getGanador().incluye(datos.f1));
		assertTrue(bonos.isEmpty());
		assertEquals(56000, tipo.calcularPremio(torneo));
		assertEquals(0, tipo.premioPara(torneo, datos.f1));
		assertThrows(EstadoInvalidoException.class, () -> gestor.registrarResultado(datos.admin, torneo, laFinal, 3, 0));
	}

	// HU_T9: consultas

	@Test
	public void hut9SeConsultanLosTorneosPorEstado() throws Exception {
		Torneo tenis = crearTenis(4, false);
		Torneo futbol = crearFutbol(4);
		gestor.inscribirEquipo(datos.admin, futbol, datos.aguilas);
		gestor.inscribirEquipo(datos.admin, futbol, datos.lobos);
		gestor.iniciarTorneo(datos.admin, futbol);

		List<Torneo> enCurso = gestor.consultarTorneos(datos.x1, EstadoTorneo.EN_CURSO);
		assertEquals(1, enCurso.size());
		assertSame(futbol, enCurso.get(0));
		assertEquals(2, gestor.consultarTorneos(datos.x1, null).size());
		assertSame(tenis, gestor.consultarTorneosAbiertosIndividuales(datos.x1).get(0));
		assertEquals(2, gestor.consultarPosiciones(datos.x1, futbol).size());
		assertSame(futbol, gestor.consultarMisTorneos(datos.e1).get(0));
	}

	// HU_T10: bonos

	@Test
	public void hut10ElSocioVeSusBonosYSoloElPuedeUsarlos() throws Exception {
		Torneo torneo = crearFutbol(4);
		gestor.inscribirEquipo(datos.admin, torneo, datos.aguilas);
		gestor.inscribirEquipo(datos.admin, torneo, datos.lobos);
		gestor.iniciarTorneo(datos.admin, torneo);
		gestor.registrarResultado(datos.admin, torneo, torneo.getEncuentros().get(0), 0, 2);

		Usuario jugadorLobos = datos.club.buscarUsuario("l1");
		List<BonoDescuento> bonos = gestor.consultarBonos(jugadorLobos);
		assertEquals(1, bonos.size());
		assertTrue(bonos.get(0).puedeUsar(jugadorLobos));
		assertFalse(bonos.get(0).puedeUsar(datos.s1));
		bonos.get(0).usar();
		assertFalse(bonos.get(0).puedeUsar(jugadorLobos));
		assertEquals(new ArrayList<BonoDescuento>(), gestor.consultarBonos(datos.s1));
	}
}
