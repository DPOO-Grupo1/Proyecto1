package modelo.tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias del cálculo de una venta: impuestos, descuentos, bono, puntos y propina.
 */
public class VentaTest {

	private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 2, 10, 0);

	private Socio socio;
	private Entrenador entrenador;
	private Articulo camiseta;
	private Bebida limonada;

	@BeforeEach
	public void setUp() {
		socio = new Socio("s1", "pass", "Ana", "Pérez", "S1", java.time.LocalDate.of(1995, 5, 5));
		entrenador = new Entrenador("e1", "pass", "Marta", "López", 1);
		camiseta = new Articulo("Camiseta", 45000, "ropa");
		limonada = new Bebida("FRIA", 400, "Limonada", 5000);
	}

	@Test
	public void laTiendaCobraIvaDelDiecinuevePorCiento() {
		VentaTienda venta = new VentaTienda(socio, AHORA);
		venta.agregarDetalle(camiseta, 2);

		assertEquals(90000, venta.getSubtotal(), 0.01);
		assertEquals(17100, venta.calcularImpuesto(), 0.01);
		assertEquals(107100, venta.calcularTotal(), 0.01);
		assertEquals(2142, venta.calcularPuntosGenerados());
	}

	@Test
	public void laCafeteriaCobraImpuestoAlConsumoYPropinaSobreElSubtotal() {
		VentaCafeteria venta = new VentaCafeteria(socio, AHORA, 10);
		venta.agregarDetalle(limonada, 2);

		assertEquals(800, venta.calcularImpuesto(), 0.01);
		assertEquals(1000, venta.calcularPropina(), 0.01);
		assertEquals(11800, venta.calcularTotal(), 0.01);
	}

	@Test
	public void elCodigoDaQuincePorCientoASuDuenoYOchoAlResto() {
		CodigoDescuento codigo = new CodigoDescuento(1, entrenador);
		VentaTienda propia = new VentaTienda(entrenador, AHORA);
		propia.agregarDetalle(camiseta, 1);
		propia.aplicarCodigo(codigo);
		VentaTienda ajena = new VentaTienda(socio, AHORA);
		ajena.agregarDetalle(camiseta, 1);
		ajena.aplicarCodigo(codigo);

		assertEquals(53550 * 0.15, propia.getDescuento(), 0.01);
		assertEquals(53550 * 0.08, ajena.getDescuento(), 0.01);
	}

	@Test
	public void elBonoReemplazaAlCodigoYNoSeSuman() {
		VentaTienda venta = new VentaTienda(socio, AHORA);
		venta.agregarDetalle(camiseta, 1);
		assertFalse(venta.tieneBono());

		venta.aplicarCodigo(new CodigoDescuento(1, entrenador));
		venta.aplicarBono(7, 20);

		assertTrue(venta.tieneBono());
		assertEquals(53550 * 0.20, venta.getDescuento(), 0.01);
		assertEquals(42840, venta.calcularTotal(), 0.01);
	}

	@Test
	public void losPuntosRedimidosSeRestanComoPesos() {
		VentaTienda venta = new VentaTienda(socio, AHORA);
		venta.agregarDetalle(camiseta, 1);
		venta.redimirPuntos(5000);

		assertEquals(48550, venta.calcularTotal(), 0.01);
		assertEquals(971, venta.calcularPuntosGenerados());
	}
}
