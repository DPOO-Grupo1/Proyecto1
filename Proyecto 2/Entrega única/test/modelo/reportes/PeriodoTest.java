package modelo.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de Periodo y Mensualidad.
 */
public class PeriodoTest {

	private static final LocalDate PRIMERO_NOVIEMBRE = LocalDate.of(2026, 11, 1);

	@Test
	public void elPeriodoDiarioEmpiezaYTerminaElMismoDia() {
		assertEquals(PRIMERO_NOVIEMBRE, Periodo.DIARIO.inicio(PRIMERO_NOVIEMBRE));
		assertEquals(PRIMERO_NOVIEMBRE, Periodo.DIARIO.fin(PRIMERO_NOVIEMBRE));
	}

	@Test
	public void elPeriodoSemanalTieneSieteDias() {
		assertEquals(LocalDate.of(2026, 11, 7), Periodo.SEMANAL.fin(PRIMERO_NOVIEMBRE));
	}

	@Test
	public void elPeriodoMensualTerminaElDiaAnteriorDelMesSiguiente() {
		assertEquals(LocalDate.of(2026, 11, 30), Periodo.MENSUAL.fin(PRIMERO_NOVIEMBRE));
		assertEquals(LocalDate.of(2027, 2, 14), Periodo.MENSUAL.fin(LocalDate.of(2027, 1, 15)));
		assertEquals(LocalDate.of(2028, 2, 29), Periodo.MENSUAL.fin(LocalDate.of(2028, 2, 1)));
	}

	@Test
	public void unaMensualidadRegistradaQuedaPagadaConSuPeriodoYFecha() {
		Mensualidad mensualidad = new Mensualidad(YearMonth.of(2026, 11), 120000, PRIMERO_NOVIEMBRE);

		assertTrue(mensualidad.isPagada());
		assertEquals(YearMonth.of(2026, 11), mensualidad.getPeriodo());
		assertEquals(120000, mensualidad.getValor(), 0.01);
		assertEquals(PRIMERO_NOVIEMBRE, mensualidad.getFechaPago());
	}
}
