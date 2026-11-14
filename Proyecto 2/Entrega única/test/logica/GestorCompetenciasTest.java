package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.competencias.Competencia;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.FilaClasificacion;
import modelo.competencias.FilaTabla;
import modelo.competencias.GruposEliminacion;
import modelo.competencias.Liga;
import modelo.competencias.ParticipanteCompetencia;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.PartidoCompetencia;

/**
 * Pruebas de integración de competencias externas: RF2, RF25, RF26 y RF27.
 * Cada prueba arma su propio club con ClubDePruebaDeportes.
 */
public class GestorCompetenciasTest {

	private static final LocalDate FECHA = LocalDate.of(2026, 11, 8);

	private ClubDePruebaDeportes datos;
	private GestorCompetencias gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaDeportes();
		gestor = new GestorCompetencias(datos.club);
	}

	private List<Integer> numeros(int desde, int hasta) {
		List<Integer> lista = new ArrayList<Integer>();
		for (int i = desde; i <= hasta; i++) {
			lista.add(i);
		}
		return lista;
	}

	/**
	 * Liga de fútbol con Águilas (nómina 1 a 7) y dos rivales externos. Dos amarillas acumuladas suspenden.
	 */
	private Competencia crearLigaConParticipantes() throws Exception {
		Competencia liga = gestor.crearCompetencia(datos.admin, "Torneo de los Cerros", "Liga Bogotá", 2026,
				datos.futbol, datos.mayores, 7, 2, new Liga(false));
		gestor.inscribirEquipo(datos.admin, liga, "Águilas FC", numeros(1, 7), null);
		gestor.agregarRival(datos.admin, liga, "Andes", "Universidad de los Andes", null);
		gestor.agregarRival(datos.admin, liga, "Javeriana", "Pontificia Universidad Javeriana", null);
		return liga;
	}

	private PartidoCompetencia jugar(Competencia competencia, String local, String visitante, int golesLocal,
			int golesVisitante) throws Exception {
		PartidoCompetencia partido = gestor.programarPartido(datos.admin, competencia, local, visitante, FECHA,
				"Cancha 1", "Fecha");
		gestor.registrarResultado(datos.admin, partido, golesLocal, golesVisitante);
		return partido;
	}

	// RF25: crear competencias y agregar rivales

	@Test
	public void rf25ElAdministradorCreaUnaCompetenciaPorTemporada() throws Exception {
		Competencia liga = gestor.crearCompetencia(datos.admin, "ASCUN", "ASCUN", 2026, datos.futbol, datos.mayores, 7,
				3, new Liga(true));

		assertSame(liga, datos.club.buscarCompetencia("ASCUN", 2026));
		assertThrows(DuplicadoException.class, () -> gestor.crearCompetencia(datos.admin, "ASCUN", "ASCUN", 2026,
				datos.futbol, datos.mayores, 7, 3, new Liga(true)));
		assertEquals("ASCUN", gestor.crearCompetencia(datos.admin, "ASCUN", "ASCUN", 2027, datos.futbol,
				datos.mayores, 7, 3, new Liga(true)).getNombre());
	}

	@Test
	public void rf25RechazaValoresInvalidosYUsuariosSinPermiso() {
		assertThrows(DatoInvalidoException.class, () -> gestor.crearCompetencia(datos.admin, "Copa", "Liga", 2026,
				datos.futbol, datos.mayores, 0, 3, new Liga(false)));
		assertThrows(PermisoDenegadoException.class, () -> gestor.crearCompetencia(datos.e1, "Copa", "Liga", 2026,
				datos.futbol, datos.mayores, 7, 3, new Liga(false)));
	}

	@Test
	public void rf25ElGrupoDelRivalDependeDelFormato() throws Exception {
		Competencia liga = crearLigaConParticipantes();
		Competencia grupos = gestor.crearCompetencia(datos.admin, "Copa Interuniversitaria", "ASCUN", 2026,
				datos.futbol, datos.mayores, 7, 3, new GruposEliminacion(2, 1));

		assertThrows(DatoInvalidoException.class, () -> gestor.agregarRival(datos.admin, liga, "Rosario", "UR", "A"));
		assertThrows(DuplicadoException.class, () -> gestor.agregarRival(datos.admin, liga, "andes", "Uniandes", null));
		assertThrows(DatoInvalidoException.class, () -> gestor.agregarRival(datos.admin, grupos, "Rosario", "UR", "C"));
		assertEquals("B", gestor.agregarRival(datos.admin, grupos, "Rosario", "UR", "B").getGrupo());
	}

	// RF26: inscribir equipos del club

	@Test
	public void rf26InscribeUnEquipoConSuNomina() throws Exception {
		Competencia liga = crearLigaConParticipantes();

		ParticipanteCompetencia aguilas = liga.getParticipanteDe(datos.aguilas);
		assertTrue(aguilas instanceof EquipoInscrito);
		assertEquals(7, aguilas.getNomina().size());
		assertTrue(aguilas.estaEnNomina(datos.jugador(datos.aguilas, 1)));
		assertTrue(!aguilas.estaEnNomina(datos.jugador(datos.aguilas, 8)));
	}

	@Test
	public void rf26LaNominaDebeTenerElMinimoSinRepetidosNiDesconocidos() throws Exception {
		Competencia liga = gestor.crearCompetencia(datos.admin, "Copa", "Liga", 2026, datos.futbol, datos.mayores, 7, 3,
				new Liga(false));

		ReglaDeportivaException corta = assertThrows(ReglaDeportivaException.class,
				() -> gestor.inscribirEquipo(datos.admin, liga, "Águilas FC", numeros(1, 6), null));
		assertTrue(corta.getMessage().contains("faltan 1"));
		List<Integer> repetida = numeros(1, 6);
		repetida.add(6);
		assertThrows(DatoInvalidoException.class, () -> gestor.inscribirEquipo(datos.admin, liga, "Águilas FC", repetida, null));
		List<Integer> conDesconocido = numeros(1, 6);
		conDesconocido.add(99);
		assertThrows(NoEncontradoException.class,
				() -> gestor.inscribirEquipo(datos.admin, liga, "Águilas FC", conDesconocido, null));
	}

	@Test
	public void rf26ElEquipoDebeSerDelDeporteYLaCategoriaYNoRepetirse() throws Exception {
		Competencia liga = crearLigaConParticipantes();

		assertThrows(ReglaDeportivaException.class,
				() -> gestor.inscribirEquipo(datos.admin, liga, "Titanes", numeros(1, 5), null));
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.inscribirEquipo(datos.admin, liga, "Pumas", new ArrayList<Integer>(), null));
		assertThrows(DuplicadoException.class,
				() -> gestor.inscribirEquipo(datos.admin, liga, "Águilas FC", numeros(1, 7), null));
	}

	// RF27: partidos oficiales, estadísticas y suspensiones

	@Test
	public void rf27ProgramaYRegistraPartidosInclusoEntreRivalesExternos() throws Exception {
		Competencia liga = crearLigaConParticipantes();

		PartidoCompetencia externo = jugar(liga, "Andes", "Javeriana", 0, 1);

		assertTrue(externo.isJugado());
		assertEquals(1, liga.getPartidos().size());
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.programarPartido(datos.admin, liga, "Andes", "Andes", FECHA, "Cancha 1", "Fecha"));
		assertThrows(NoEncontradoException.class,
				() -> gestor.programarPartido(datos.admin, liga, "Andes", "Rosario", FECHA, "Cancha 1", "Fecha"));
	}

	@Test
	public void rf27SoloJueganLosDeLaNominaConEstadisticaDelDeporte() throws Exception {
		Competencia liga = crearLigaConParticipantes();
		PartidoCompetencia partido = jugar(liga, "Águilas FC", "Andes", 2, 0);
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		JugadorEquipo a8 = datos.jugador(datos.aguilas, 8);

		assertThrows(JugadorNoHabilitadoException.class,
				() -> gestor.registrarEstadistica(datos.admin, partido, a8, new EstadisticaFutbol(a8, 1, 0, 0, 0)));
		assertThrows(ReglaDeportivaException.class,
				() -> gestor.registrarEstadistica(datos.admin, partido, a1, new EstadisticaBaloncesto(a1, 10, 0)));
		gestor.registrarEstadistica(datos.admin, partido, a1, new EstadisticaFutbol(a1, 2, 0, 0, 0));
		assertTrue(partido.tieneEstadisticaDe(a1));
	}

	@Test
	public void rf27LasAmarillasAcumuladasSuspendenSoloEnEsaCompetencia() throws Exception {
		Competencia liga = crearLigaConParticipantes();
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		PartidoCompetencia primero = jugar(liga, "Águilas FC", "Andes", 1, 0);
		gestor.registrarEstadistica(datos.admin, primero, a1, new EstadisticaFutbol(a1, 0, 0, 1, 0));
		assertNull(a1.getSuspensionPendiente());

		PartidoCompetencia segundo = jugar(liga, "Javeriana", "Águilas FC", 0, 0);
		gestor.registrarEstadistica(datos.admin, segundo, a1, new EstadisticaFutbol(a1, 0, 0, 1, 0));

		Suspension suspension = a1.getSuspensionPendiente();
		assertEquals(Suspension.MOTIVO_ACUMULACION, suspension.getMotivo());
		PartidoCompetencia tercero = gestor.programarPartido(datos.admin, liga, "Águilas FC", "Javeriana", FECHA,
				"Cancha 1", "Fecha");
		assertTrue(!a1.estaHabilitadoPara(tercero));
		// Un partido interno es otro contexto: ahí sí puede jugar
		GestorDeportes deportes = new GestorDeportes(datos.club);
		assertTrue(a1.estaHabilitadoPara(deportes.programarPartidoInterno(datos.e1, "Águilas FC", "Lobos FC",
				LocalDate.of(2026, 11, 20), "práctica")));
	}

	// RF2: consultas de competencias

	@Test
	public void rf2LaTablaOrdenaPorPuntosYDesempataPorEnfrentamientoDirecto() throws Exception {
		Competencia liga = crearLigaConParticipantes();
		jugar(liga, "Águilas FC", "Andes", 1, 0);
		jugar(liga, "Andes", "Javeriana", 5, 0);
		jugar(liga, "Javeriana", "Águilas FC", 0, 0);
		jugar(liga, "Andes", "Javeriana", 0, 0);

		List<FilaTabla> tabla = gestor.consultarTabla(datos.e1, liga, null);

		// Águilas y Andes suman 4; Andes tiene mejor diferencia, pero Águilas le ganó el directo
		assertEquals(3, tabla.size());
		assertEquals("Águilas FC", tabla.get(0).getParticipante().getNombre());
		assertEquals("Andes", tabla.get(1).getParticipante().getNombre());
		assertEquals(4, tabla.get(0).getPuntos());
		assertEquals(4, tabla.get(1).getPuntos());
		assertTrue(tabla.get(1).getDiferencia() > tabla.get(0).getDiferencia());
		assertThrows(DatoInvalidoException.class, () -> gestor.consultarTabla(datos.e1, liga, "A"));
	}

	@Test
	public void rf2EnGruposClasificanLosPrimerosDeCadaGrupo() throws Exception {
		Competencia copa = gestor.crearCompetencia(datos.admin, "Copa", "ASCUN", 2026, datos.futbol, datos.mayores, 7,
				3, new GruposEliminacion(2, 1));
		gestor.inscribirEquipo(datos.admin, copa, "Águilas FC", numeros(1, 7), "A");
		gestor.agregarRival(datos.admin, copa, "Andes", "Uniandes", "A");
		gestor.agregarRival(datos.admin, copa, "Javeriana", "PUJ", "B");
		gestor.agregarRival(datos.admin, copa, "Rosario", "UR", "B");
		jugar(copa, "Águilas FC", "Andes", 2, 1);
		jugar(copa, "Javeriana", "Rosario", 0, 1);

		List<ParticipanteCompetencia> clasificados = gestor.consultarClasificados(datos.e1, copa);

		assertEquals(2, clasificados.size());
		assertEquals("Águilas FC", clasificados.get(0).getNombre());
		assertEquals("Rosario", clasificados.get(1).getNombre());
		assertEquals(2, gestor.consultarTabla(datos.e1, copa, "B").size());
	}

	@Test
	public void rf2GoleadoresYDestacadosVanDeMayorAMenor() throws Exception {
		Competencia liga = crearLigaConParticipantes();
		PartidoCompetencia partido = jugar(liga, "Águilas FC", "Andes", 4, 0);
		JugadorEquipo a1 = datos.jugador(datos.aguilas, 1);
		JugadorEquipo a2 = datos.jugador(datos.aguilas, 2);
		gestor.registrarEstadistica(datos.admin, partido, a1, new EstadisticaFutbol(a1, 1, 3, 0, 0));
		gestor.registrarEstadistica(datos.admin, partido, a2, new EstadisticaFutbol(a2, 3, 0, 0, 0));

		List<FilaClasificacion> goleadores = gestor.consultarGoleadores(datos.e1, liga);
		List<FilaClasificacion> destacados = gestor.consultarDestacados(datos.e1, liga);

		assertSame(a2, goleadores.get(0).getJugador());
		assertEquals(3, goleadores.get(0).getValor());
		assertEquals(1, destacados.size());
		assertSame(a1, destacados.get(0).getJugador());
	}

	@Test
	public void rf2LosGoleadoresSoloExistenEnFutbolYElFiltroPuedeDarVacio() throws Exception {
		Competencia baloncesto = gestor.crearCompetencia(datos.admin, "Liga de Baloncesto", "Liga", 2026,
				datos.baloncesto, datos.mayores, 5, 3, new Liga(false));
		crearLigaConParticipantes();

		assertThrows(ReglaDeportivaException.class, () -> gestor.consultarGoleadores(datos.e1, baloncesto));
		assertEquals(1, gestor.consultarCompetencias(datos.e1, "fútbol", null).size());
		assertTrue(gestor.consultarCompetencias(datos.e1, "Voleibol", null).isEmpty());
		assertTrue(gestor.consultarCompetencias(datos.e1, null, "Sub-17").isEmpty());
	}
}
