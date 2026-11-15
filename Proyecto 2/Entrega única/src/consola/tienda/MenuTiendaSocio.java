package consola.tienda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.Producto;
import modelo.tienda.ProductoCafeteria;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;
import modelo.torneos.BonoDescuento;
import modelo.usuarios.Cliente;

/**
 * Compras del socio en la tienda y la cafetería (RF3, RF4, RF5) y uso de los
 * bonos ganados en torneos recreativos (HU_T10).
 */
public class MenuTiendaSocio extends Menu {

	private GestorTienda gestor;

	public MenuTiendaSocio(Consola consola) {
		super(consola);
		gestor = new GestorTienda(club);
	}

	@Override
	public String getTitulo() {
		return "Tienda y cafetería";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver mis puntos"); // 1
		opciones.add("Ver mis compras"); // 2
		opciones.add("Comprar en tienda"); // 3
		opciones.add("Comprar en cafetería"); // 4
		opciones.add("Ver mis bonos de torneos"); // 5
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verPuntos();
		}
		else if (opcion == 2) {
			verCompras();
		}
		else if (opcion == 3) {
			comprarEnTienda();
		}
		else if (opcion == 4) {
			comprarEnCafeteria();
		}
		else if (opcion == 5) {
			consola.mostrarLista("Mis bonos:", club.getBonosDe(usuario));
		}
	}

	// RF3, RF5
	private void comprarEnTienda() throws ClubException {
		List<Producto> productosTienda = new ArrayList<Producto>();
		for (Producto p : club.getProductos()) {
			if (p instanceof Articulo) {
				productosTienda.add(p);
			}
		}

		// carrito
		Map<String, Integer> carrito = new HashMap<String, Integer>();
		do {
			Producto producto = consola.elegir("Producto", productosTienda);
			if (producto == null) {
				return;
			}
			int cantidad = consola.leerEntero("Cantidad", 1, 100);
			String nombre = producto.getNombre();
			if (carrito.containsKey(nombre)) {
				carrito.put(nombre, carrito.get(nombre) + cantidad);
			} else {
				carrito.put(nombre, cantidad);
			}
		} while (consola.confirmar("¿Agregar otro producto?"));

		// código descuento (null-> sin codigo)
		// bono de torneo; si usa bono no se pregunta el código porque no son acumulables
		Integer idBono = elegirBono();
		Integer codigo = null;
		if (idBono == null && consola.confirmar("¿Tiene código de descuento?")) {
			codigo = consola.leerEntero("Número del código", 1, 9999);
		}

		//  puntos
		int saldo = ((Cliente) usuario).getPuntosFidelidad();
		int puntos = 0;
		if (saldo > 0) {
			System.out.println("Tiene " + saldo + " puntos (1 punto = $1).");
			puntos = consola.leerEntero("Puntos a redimir", 0, saldo);
		}

		// compra
		VentaTienda venta = gestor.comprarEnTienda(usuario, carrito, codigo, puntos, idBono);
		System.out.println("ACEPTADO: compra registrada.");
		System.out.println("   Subtotal:  $" + (long) venta.getSubtotal());
		System.out.println("   IVA (19%): $" + (long) venta.calcularImpuesto());
		System.out.println("   Descuento: $" + (long) venta.getDescuento());
		System.out.println("   Puntos:    $" + venta.getPuntosRedimidos());
		System.out.println("   Total:     $" + (long) venta.getMontoTotal());
		System.out.println("   Ganó " + venta.calcularPuntosGenerados() + " puntos.");
	}

	// RF4, RF5
	private void comprarEnCafeteria() throws ClubException {
		List<Producto> productosCafeteria = new ArrayList<Producto>();
		for (Producto p : club.getProductos()) {
			if (p instanceof ProductoCafeteria) {
				productosCafeteria.add(p);
			}
		}
		Map<String, Integer> carrito = new HashMap<String, Integer>();
		do {
			Producto producto = consola.elegir("Producto", productosCafeteria);
			if (producto == null) {
				return;
			}
			int cantidad = consola.leerEntero("Cantidad", 1, 100);
			String nombre = producto.getNombre();
			if (carrito.containsKey(nombre)) {
				carrito.put(nombre, carrito.get(nombre) + cantidad);
			} else {
				carrito.put(nombre, cantidad);
			}
		} while (consola.confirmar("¿Agregar otro producto?"));
		
		List<String> alergenos = gestor.consultarAlergenos(usuario, carrito);
		if (!alergenos.isEmpty()) {
			System.out.println("Atención, su pedido contiene: " + String.join(", ", alergenos));
			if (!consola.confirmar("¿Desea continuar con la compra?")) {
				return;
			}
		}
		
		// bono de torneo; si usa bono no se pregunta el código porque no son acumulables
		Integer idBono = elegirBono();
		Integer codigo = null;
		if (idBono == null && consola.confirmar("¿Tiene código de descuento?")) {
			codigo = consola.leerEntero("Número del código", 1, 9999);
		}
		
		int saldo = ((Cliente) usuario).getPuntosFidelidad();
		int puntos = 0;
		if (saldo > 0) {
			System.out.println("Tiene " + saldo + " puntos (1 punto = $1).");
			puntos = consola.leerEntero("Puntos a redimir", 0, saldo);
		}
		
		// propina
		int propina = 10;
		if (!consola.confirmar("¿Dejar la propina sugerida del 10%?")) {
			propina = consola.leerEntero("Porcentaje de propina", 0, 100);
		}

		// compra
		VentaCafeteria venta = gestor.comprarEnCafeteria(usuario, carrito, codigo, puntos, propina, idBono);
		System.out.println("ACEPTADO: compra registrada.");
		System.out.println("   Subtotal:  $" + (long) venta.getSubtotal());
		System.out.println("   Impuesto al consumo (8%): $" + (long) venta.calcularImpuesto());
		System.out.println("   Descuento: $" + (long) venta.getDescuento());
		System.out.println("   Puntos:    $" + venta.getPuntosRedimidos());
		System.out.println("   Propina:   $" + (long) venta.calcularPropina());
		System.out.println("   Total:     $" + (long) venta.getMontoTotal());
		System.out.println("   Ganó " + venta.calcularPuntosGenerados() + " puntos.");
		
	}
	
	// Si el usuario tiene bonos sin usar, pregunta si quiere usar uno. Devuelve su id o null.
	private Integer elegirBono() {
		List<BonoDescuento> disponibles = new ArrayList<BonoDescuento>();
		for (BonoDescuento bono : club.getBonosDe(usuario)) {
			if (bono.puedeUsar(usuario)) {
				disponibles.add(bono);
			}
		}
		if (disponibles.isEmpty() || !consola.confirmar("Tiene " + disponibles.size() + " bono(s) de torneos. ¿Usar uno en esta compra?")) {
			return null;
		}
		BonoDescuento bono = consola.elegir("Bono", disponibles);
		if (bono == null) {
			return null;
		}
		return bono.getId();
	}
	
	private void verPuntos() {
		Cliente cliente = (Cliente) usuario;
		System.out.println("Tiene " + cliente.getPuntosFidelidad() + " puntos (1 punto = $1).");
	}
	
	private void verCompras() {
		Cliente cliente = (Cliente) usuario;
		consola.mostrarLista("Mis compras" ,cliente.getCompras());
	}
}