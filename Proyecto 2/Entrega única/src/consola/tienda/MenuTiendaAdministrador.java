package consola.tienda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.Bebida;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;

/**
 * Inventario para el administrador: consultar por ubicación, mover productos,
 * reabastecer y registrar productos nuevos (RF29, RF30 y RF31).
 */
public class MenuTiendaAdministrador extends Menu {

	private GestorTienda gestor;

	public MenuTiendaAdministrador(Consola consola) {
		super(consola);
		gestor = new GestorTienda(club);
	}

	@Override
	public String getTitulo() {
		return "Inventario de tienda y cafetería";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Consultar el inventario de una ubicación"); // 1
		opciones.add("Mover productos entre ubicaciones"); // 2
		opciones.add("Reabastecer un producto"); // 3
		opciones.add("Registrar un producto nuevo"); // 4
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			consultarInventario();
		} else if (opcion == 2) {
			moverProductos();
		} else if (opcion == 3) {
			reabastecer();
		} else if (opcion == 4) {
			registrarProducto();
		}
	}

	// RF29
	private void consultarInventario() throws ClubException {
		Ubicacion ubicacion = consola.elegir("Ubicación", club.getUbicaciones());
		if (ubicacion == null) {
			return;
		}
		Map<Producto, Integer> inventario = gestor.consultarInventario(usuario, ubicacion.getNombre());
		System.out.println("Inventario de " + ubicacion.getNombre() + ":");
		if (inventario.isEmpty()) {
			System.out.println("   (sin productos)");
		}
		for (Producto producto : inventario.keySet()) {
			System.out.println("   " + producto.getNombre() + ": " + inventario.get(producto) + " unidades");
		}
	}

	// RF30
	private void moverProductos() throws ClubException {
		Producto producto = consola.elegir("Producto", club.getProductos());
		if (producto == null) {
			return;
		}
		Ubicacion origen = consola.elegir("Ubicación de origen", club.getUbicaciones());
		if (origen == null) {
			return;
		}
		Ubicacion destino = consola.elegir("Ubicación de destino", club.getUbicaciones());
		if (destino == null) {
			return;
		}
		int cantidad = consola.leerEntero("Cantidad a mover", 1, 100000);
		gestor.moverProductos(usuario, producto.getNombre(), origen.getNombre(), destino.getNombre(), cantidad);
		System.out.println("ACEPTADO: se movieron " + cantidad + " de " + producto.getNombre() + " de "
				+ origen.getNombre() + " a " + destino.getNombre() + ".");
	}

	// RF31
	private void reabastecer() throws ClubException {
		Producto producto = consola.elegir("Producto", club.getProductos());
		if (producto == null) {
			return;
		}
		Ubicacion ubicacion = consola.elegir("Ubicación", club.getUbicaciones());
		if (ubicacion == null) {
			return;
		}
		int cantidad = consola.leerEntero("Unidades que llegan", 1, 100000);
		gestor.reabastecer(usuario, producto.getNombre(), ubicacion.getNombre(), cantidad);
		System.out.println("ACEPTADO: se sumaron " + cantidad + " unidades de " + producto.getNombre() + " en "
				+ ubicacion.getNombre() + ".");
	}

	// RF31
	private void registrarProducto() throws ClubException {
		List<String> tipos = new ArrayList<String>();
		tipos.add("Artículo (tienda)");
		tipos.add("Bebida (cafetería)");
		tipos.add("Snack (cafetería)");
		int tipo = consola.leerOpcion("Tipo de producto", tipos, "Cancelar");
		if (tipo == 0) {
			return;
		}
		String nombre = consola.leerTexto("Nombre");
		double precio = consola.leerDecimal("Precio", 0);

		Producto producto;
		if (tipo == 1) {
			producto = new Articulo(nombre, precio, consola.leerTexto("Categoría"));
		} else if (tipo == 2) {
			String temperatura = "FRIA";
			if (consola.confirmar("¿Es una bebida caliente?")) {
				temperatura = "CALIENTE";
			}
			int tamanio = consola.leerEntero("Tamaño en ml", 1, 5000);
			producto = new Bebida(temperatura, tamanio, nombre, precio);
		} else {
			producto = new Snack(leerAlergenos(), nombre, precio);
		}

		Ubicacion ubicacion = consola.elegir("Ubicación inicial", club.getUbicaciones());
		if (ubicacion == null) {
			return;
		}
		int cantidad = consola.leerEntero("Cantidad inicial", 0, 100000);
		gestor.registrarProducto(usuario, producto, ubicacion.getNombre(), cantidad);
		System.out.println("ACEPTADO: se registró " + producto + " con " + cantidad + " unidades en "
				+ ubicacion.getNombre() + ".");
	}

	// Lee los alérgenos separados por comas; si se deja vacío, el snack no tiene alérgenos
	private List<String> leerAlergenos() {
		List<String> alergenos = new ArrayList<String>();
		String texto = consola.leerTextoOpcional("Alérgenos separados por comas");
		if (texto.isEmpty()) {
			return alergenos;
		}
		for (String parte : texto.split(",")) {
			String alergeno = parte.trim();
			if (!alergeno.isEmpty()) {
				alergenos.add(alergeno);
			}
		}
		return alergenos;
	}
}