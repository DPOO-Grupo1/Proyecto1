package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.reportes.FiltroHistorial;
import modelo.reportes.Historial;
import modelo.reportes.Mensualidad;
import modelo.reportes.Periodo;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteFinanciero;

/**
 * Pruebas de integración de reportes y mensualidades: RF28, RF34 y RF35.
 */
public class GestorReportesTest {

	private static final YearMonth NOVIEMBRE = YearMonth.of(2026, 11);
	private static final LocalDate PRIMERO_NOVIEMBRE = LocalDate.of(2026, 11, 1);

	private ClubDePruebaTienda datos;
	private GestorReportes gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaTienda();
		gestor = new GestorReportes(datos.club);
	}

	// RF35: registrar mensualidades

	@Test
	public void rf35RegistraElPagoYElSocioQuedaAlDia() throws Exception {
		datos.s1.setAlDia(false);

		Mensualidad mensualidad = gestor.registrarPago(datos.admin, "S1", NOVIEMBRE, 120000);

		assertTrue(datos.s1.tieneMensualidadPagada(NOVIEMBRE));
		assertTrue(datos.s1.estaAlDia(ClubDePruebaTienda.HOY));
		assertEquals(ClubDePruebaTienda.HOY, mensualidad.getFechaPago());
	}

	@Test
	public void rf35RechazaPagoRepetidoValorCeroSocioInexistenteYSinPermiso() throws Exception {
		gestor.registrarPago(datos.admin, "S1", NOVIEMBRE, 120000);

		assertThrows(DuplicadoException.class, () -> gestor.registrarPago(datos.admin, "S1", NOVIEMBRE, 120000));
		assertThrows(DatoInvalidoException.class, () -> gestor.registrarPago(datos.admin, "S2", NOVIEMBRE, 0));
		assertThrows(NoEncontradoException.class, () -> gestor.registrarPago(datos.admin, "S99", NOVIEMBRE, 120000));
		assertThrows(PermisoDenegadoException.class, () -> gestor.registrarPago(datos.s1, "S1", NOVIEMBRE, 120000));
		assertEquals(1, datos.s1.getMensualidades().size());
		assertFalse(datos.s2.tieneMensualidadPagada(NOVIEMBRE));
	}

	// RF34: reporte financiero

	@Test
	public void rf34ReporteFinancieroSumaVentasYMensualidadesDelPeriodo() throws Exception {
		GestorTienda tienda = new GestorTienda(datos.club);
		Map<String, Integer> camiseta = new HashMap<String, Integer>();
		camiseta.put("Camiseta", 1);
		tienda.comprarEnTienda(datos.s1, camiseta, null, 0);
		gestor.registrarPago(datos.admin, "S1", NOVIEMBRE, 120000);

		ReporteFinanciero reporte = gestor.generarReporteFinanciero(datos.admin, Periodo.MENSUAL, PRIMERO_NOVIEMBRE);

		assertEquals(LocalDate.of(2026, 11, 30), reporte.getHasta());
		assertEquals(45000, reporte.getSubtotalTienda(), 0.01);
		assertEquals(8550, reporte.getImpuestoTienda(), 0.01);
		assertEquals(53550, reporte.getTotalTienda(), 0.01);
		assertEquals(120000, reporte.getTotalMensualidades(), 0.01);
	}

	@Test
	public void rf34PeriodoSinDatosDaReporteEnCeroYSoloLoGeneraElAdministrador() throws Exception {
		ReporteFinanciero vacio = gestor.generarReporteFinanciero(datos.admin, Periodo.DIARIO, LocalDate.of(2026, 1, 1));

		assertEquals(0, vacio.getTotalTienda(), 0.01);
		assertEquals(0, vacio.getTotalCafeteria(), 0.01);
		assertEquals(0, vacio.getTotalMensualidades(), 0.01);
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.generarReporteFinanciero(datos.e1, Periodo.SEMANAL, PRIMERO_NOVIEMBRE));
	}

	// RF34: reporte deportivo

	@Test
	public void rf34ReporteDeportivoIncluyeLaAsistenciaYSoloLoGeneraElAdministrador() throws Exception {
		ReporteDeportivo reporte = gestor.generarReporteDeportivo(datos.admin, Periodo.MENSUAL, PRIMERO_NOVIEMBRE);

		assertEquals(PRIMERO_NOVIEMBRE, reporte.getDesde());
		assertTrue(reporte.getResultados().get(0).startsWith("Asistencia a entrenamientos"));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.generarReporteDeportivo(datos.s1, Periodo.MENSUAL, PRIMERO_NOVIEMBRE));
	}

	@Test
	public void rf34ReporteDeportivoMuestraElRankingConLosPartidosDelPeriodo() throws Exception {
		Disciplina padel = datos.club.buscarDisciplina("Pádel");
		datos.s1.agregarInscripcion(new Inscripcion(datos.s1, padel, Nivel.INTERMEDIO, 1016));
		datos.s2.agregarInscripcion(new Inscripcion(datos.s2, padel, Nivel.INTERMEDIO, 984));
		List<modelo.usuarios.Socio> jugadores = new ArrayList<modelo.usuarios.Socio>();
		jugadores.add(datos.s1);
		jugadores.add(datos.s2);
		PartidoPractica partido = new PartidoPractica("PP1", datos.club.buscarReserva("R1"), jugadores);
		partido.setJugado(true);
		partido.setGanador(datos.s1);
		datos.club.agregarPartidoPractica(partido);

		List<String> noviembre = gestor.generarReporteDeportivo(datos.admin, Periodo.MENSUAL, PRIMERO_NOVIEMBRE)
				.getResultados();
		List<String> diciembre = gestor.generarReporteDeportivo(datos.admin, Periodo.MENSUAL, LocalDate.of(2026, 12, 1))
				.getResultados();

		int primero = noviembre.indexOf("ELO Pádel - s1: 1016 (en el periodo: 1 partido(s), 1 ganado(s))");
		int segundo = noviembre.indexOf("ELO Pádel - s2: 984 (en el periodo: 1 partido(s), 0 ganado(s))");
		assertTrue(primero >= 0);
		assertTrue(segundo > primero);
		assertTrue(diciembre.contains("ELO Pádel - s1: 1016 (en el periodo: 0 partido(s), 0 ganado(s))"));
	}

	// RF28: historial global

	@Test
	public void rf28ElHistorialFiltraPorSocioYPorFechas() throws Exception {
		Historial deS2 = gestor.consultarHistorial(datos.admin,
				new FiltroHistorial(null, null, null, datos.s2, null, null, null));
		Historial deS1 = gestor.consultarHistorial(datos.admin,
				new FiltroHistorial(null, null, null, datos.s1, null, null, null));
		Historial otroMes = gestor.consultarHistorial(datos.admin,
				new FiltroHistorial(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31), null));

		assertEquals(1, deS2.getReservas().size());
		assertTrue(deS1.getReservas().isEmpty());
		assertTrue(otroMes.getReservas().isEmpty());
	}

	@Test
	public void rf28SoloElAdministradorConsultaElHistorial() {
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarHistorial(datos.s1,
				new FiltroHistorial(null, null, null)));
	}
}
