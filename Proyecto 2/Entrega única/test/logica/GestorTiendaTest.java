package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.VentaInvalidaException;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;

/**
 * Pruebas de integración de tienda, cafetería e inventario: RF3, RF4, RF5, RF17, RF20,
 * RF29, RF30, RF31 y el canje del bono de torneos (HU_T10).
 * Cada prueba arma su propio club con ClubDePruebaTienda.
 */
public class GestorTiendaTest {

	private ClubDePruebaTienda datos;
	private GestorTienda gestor;

	@BeforeEach
	public void setUp() {
		datos = new ClubDePruebaTienda();
		gestor = new GestorTienda(datos.club);
	}

	private Map<String, Integer> carrito(String producto, int cantidad) {
		Map<String, Integer> carrito = new HashMap<String, Integer>();
		carrito.put(producto, cantidad);
		return carrito;
	}

	// RF3: comprar en la tienda

	@Test
	public void rf3CompraEnLaTiendaConIvaYPuntos() throws Exception {
		VentaTienda venta = gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 2), null, 0);

		assertEquals(90000, venta.getSubtotal(), 0.01);
		assertEquals(17100, venta.calcularImpuesto(), 0.01);
		assertEquals(107100, venta.getMontoTotal(), 0.01);
		assertEquals(18, datos.existencias("Camiseta", "Tienda"));
		assertEquals(9000 + 2142, datos.s1.getPuntosFidelidad());
		assertTrue(datos.club.getVentas().contains(venta));
		assertTrue(datos.s1.getCompras().contains(venta));
	}

	@Test
	public void rf3LaVentaQuedaConLaFechaDelClub() throws Exception {
		VentaTienda venta = gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 0);

		assertEquals(ClubDePruebaTienda.HOY, venta.getFechaHora().toLocalDate());
	}

	@Test
	public void rf3RechazaSinExistenciasYNoCambiaNada() {
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Balón", 6), null, 0));

		assertEquals(5, datos.existencias("Balón", "Tienda"));
		assertEquals(9000, datos.s1.getPuntosFidelidad());
		assertTrue(datos.club.getVentas().isEmpty());
	}

	@Test
	public void rf3RechazaProductosDeCafeteriaCarritoVacioYAlAdministrador() {
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Café", 1), null, 0));
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, new HashMap<String, Integer>(), null, 0));
		assertThrows(PermisoDenegadoException.class,
				() -> gestor.comprarEnTienda(datos.admin, carrito("Camiseta", 1), null, 0));
	}

	// RF3 y RF17: códigos de descuento

	@Test
	public void rf3ElSocioUsaUnCodigoDeEmpleadoYRecibeOchoPorCiento() throws Exception {
		VentaTienda venta = gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 2), 1, 0);

		assertEquals(8568, venta.getDescuento(), 0.01);
		assertEquals(98532, venta.getMontoTotal(), 0.01);
	}

	@Test
	public void rf3RechazaCodigoInexistenteEInactivo() {
		assertThrows(NoEncontradoException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), 99, 0));
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), 3, 0));
		assertEquals(20, datos.existencias("Camiseta", "Tienda"));
	}

	@Test
	public void rf17ElEmpleadoConSuCodigoRecibeQuincePorCientoYConElAjenoOcho() throws Exception {
		VentaTienda propia = gestor.comprarEnTienda(datos.e1, carrito("Camiseta", 2), 1, 0);
		VentaTienda ajena = gestor.comprarEnTienda(datos.e2, carrito("Camiseta", 2), 1, 0);

		assertEquals(91035, propia.getMontoTotal(), 0.01);
		assertEquals(98532, ajena.getMontoTotal(), 0.01);
		assertEquals((int) (91035 * 0.02), datos.e1.getPuntosFidelidad());
	}

	// RF20: el fisioterapeuta también compra con descuento de empleado

	@Test
	public void rf20ElFisioterapeutaCompraEnLaCafeteriaConCodigoDeEmpleado() throws Exception {
		VentaCafeteria venta = gestor.comprarEnCafeteria(datos.f1, carrito("Limonada", 1), 4, 0, 0);

		assertEquals(5400 * 0.15, venta.getDescuento(), 0.01);
		assertThrows(NoEncontradoException.class,
				() -> gestor.comprarEnCafeteria(datos.f1, carrito("Limonada", 1), 50, 0, 0));
	}

	// RF4: cafetería

	@Test
	public void rf4CompraEnLaCafeteriaConImpuestoAlConsumoYPropina() throws Exception {
		VentaCafeteria venta = gestor.comprarEnCafeteria(datos.s1, carrito("Limonada", 1), null, 0, 10);

		assertEquals(400, venta.calcularImpuesto(), 0.01);
		assertEquals(500, venta.calcularPropina(), 0.01);
		assertEquals(5900, venta.getMontoTotal(), 0.01);
		assertEquals(29, datos.existencias("Limonada", "Cafetería"));
	}

	@Test
	public void rf4InformaLosAlergenosDeLosSnacksSinRepetir() throws Exception {
		Map<String, Integer> pedido = carrito("Mix de frutos secos", 1);
		pedido.put("Galleta", 2);
		pedido.put("Limonada", 1);

		List<String> alergenos = gestor.consultarAlergenos(datos.s1, pedido);

		assertEquals(3, alergenos.size());
		assertTrue(alergenos.contains("maní"));
		assertTrue(alergenos.contains("almendras"));
		assertTrue(alergenos.contains("gluten"));
	}

	@Test
	public void rf4NoVendeBebidaCalienteAQuienEstaEnPadel() throws Exception {
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnCafeteria(datos.s2, carrito("Café", 1), null, 0, 10));
		assertEquals(40, datos.existencias("Café", "Cafetería"));

		gestor.comprarEnCafeteria(datos.s1, carrito("Café", 1), null, 0, 10);
		assertEquals(39, datos.existencias("Café", "Cafetería"));
	}

	@Test
	public void rf4RechazaPropinaNegativaYArticulosDeTienda() {
		assertThrows(DatoInvalidoException.class,
				() -> gestor.comprarEnCafeteria(datos.s1, carrito("Limonada", 1), null, 0, -5));
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnCafeteria(datos.s1, carrito("Camiseta", 1), null, 0, 10));
	}

	// RF5: puntos de fidelidad

	@Test
	public void rf5RedimePuntosComoPesos() throws Exception {
		VentaTienda venta = gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 5000);

		assertEquals(48550, venta.getMontoTotal(), 0.01);
		assertEquals(9000 - 5000 + 971, datos.s1.getPuntosFidelidad());
	}

	@Test
	public void rf5RechazaMasPuntosQueElSaldo() {
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 20000));
		assertEquals(9000, datos.s1.getPuntosFidelidad());
	}

	@Test
	public void rf5RechazaMasPuntosQueElValorDeLaCompra() {
		datos.s1.setPuntosFidelidad(100000);

		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 60000));
		assertEquals(100000, datos.s1.getPuntosFidelidad());
	}

	// HU_T10: bono de torneos

	@Test
	public void hut10ElSocioUsaSuBonoYQuedaUsado() throws Exception {
		VentaTienda venta = gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 0, 1);

		assertEquals(42840, venta.getMontoTotal(), 0.01);
		assertEquals(1, venta.getIdBono());
		assertTrue(datos.bonoS1.isUsado());
	}

	@Test
	public void hut10ElBonoNoSeSumaConElCodigoNiLoUsaOtroNiSeUsaDosVeces() throws Exception {
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), 1, 0, 1));
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s2, carrito("Camiseta", 1), null, 0, 1));
		assertFalse(datos.bonoS1.isUsado());
		assertEquals(20, datos.existencias("Camiseta", "Tienda"));

		gestor.comprarEnCafeteria(datos.s1, carrito("Limonada", 1), null, 0, 0, 1);
		assertThrows(VentaInvalidaException.class,
				() -> gestor.comprarEnTienda(datos.s1, carrito("Camiseta", 1), null, 0, 1));
	}

	// RF29: inventario

	@Test
	public void rf29ConsultaElInventarioDeUnaUbicacion() throws Exception {
		Map<Producto, Integer> inventario = gestor.consultarInventario(datos.admin, "Tienda");

		assertEquals(2, inventario.size());
		assertEquals(Integer.valueOf(20), inventario.get(datos.buscar("Camiseta")));
		assertEquals(Integer.valueOf(5), inventario.get(datos.buscar("Balón")));
	}

	@Test
	public void rf29RechazaUbicacionInexistenteYUsuariosSinPermiso() {
		assertThrows(NoEncontradoException.class, () -> gestor.consultarInventario(datos.admin, "Sótano"));
		assertThrows(PermisoDenegadoException.class, () -> gestor.consultarInventario(datos.e1, "Tienda"));
	}

	// RF30: mover productos

	@Test
	public void rf30MueveProductosEntreUbicaciones() throws Exception {
		gestor.moverProductos(datos.admin, "Camiseta", "Bodega", "Tienda", 5);
		gestor.moverProductos(datos.admin, "Camiseta", "Bodega", "Cafetería", 2);

		assertEquals(3, datos.existencias("Camiseta", "Bodega"));
		assertEquals(25, datos.existencias("Camiseta", "Tienda"));
		assertEquals(2, datos.existencias("Camiseta", "Cafetería"));
	}

	@Test
	public void rf30RechazaMoverMasDeLoDisponible() {
		assertThrows(DatoInvalidoException.class,
				() -> gestor.moverProductos(datos.admin, "Camiseta", "Bodega", "Tienda", 30));
		assertEquals(10, datos.existencias("Camiseta", "Bodega"));
		assertEquals(20, datos.existencias("Camiseta", "Tienda"));
	}

	// RF31: reabastecer y registrar productos

	@Test
	public void rf31ReabasteceYRegistraUnProductoNuevo() throws Exception {
		gestor.reabastecer(datos.admin, "Balón", "Tienda", 15);
		Snack avena = new Snack(java.util.Arrays.asList("gluten"), "Galleta de avena", 4000);
		gestor.registrarProducto(datos.admin, avena, "Bodega", 30);

		assertEquals(20, datos.existencias("Balón", "Tienda"));
		assertEquals(30, datos.existencias("Galleta de avena", "Bodega"));
	}

	@Test
	public void rf31ReabasteceUnProductoEnUnaUbicacionDondeNoTeniaExistencias() throws Exception {
		gestor.reabastecer(datos.admin, "Balón", "Bodega", 4);

		assertEquals(4, datos.existencias("Balón", "Bodega"));
		assertEquals(5, datos.existencias("Balón", "Tienda"));
	}

	@Test
	public void rf31RechazaPrecioCeroCantidadNegativaYNombreRepetido() {
		assertThrows(DatoInvalidoException.class, () -> gestor.registrarProducto(datos.admin,
				new Snack(new java.util.ArrayList<String>(), "Fruta", 0), "Bodega", 10));
		assertThrows(DatoInvalidoException.class, () -> gestor.registrarProducto(datos.admin,
				new Snack(new java.util.ArrayList<String>(), "Fruta", 3000), "Bodega", -5));
		assertThrows(DuplicadoException.class, () -> gestor.registrarProducto(datos.admin,
				new Snack(new java.util.ArrayList<String>(), "Galleta", 3000), "Bodega", 5));
		assertThrows(DatoInvalidoException.class, () -> gestor.reabastecer(datos.admin, "Balón", "Tienda", 0));
		assertEquals(6, datos.club.getProductos().size());
	}
}
