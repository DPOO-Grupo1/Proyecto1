package persistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.reportes.Mensualidad;
import modelo.tienda.Articulo;
import modelo.tienda.Bebida;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.DetalleVenta;
import modelo.tienda.Existencia;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.tienda.Venta;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;
import modelo.usuarios.Cliente;
import modelo.usuarios.Empleado;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Guarda en tienda.json las ubicaciones, productos, existencias, códigos de descuento,
 * ventas con sus detalles y las mensualidades de los socios.
 * Todo se relaciona por nombre, login o id y se busca al cargar.
 */
public class PersistenciaTienda implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray ubicaciones = json.getJSONArray("ubicaciones");
			for (int i = 0; i < ubicaciones.length(); i++) {
				club.agregarUbicacion(new Ubicacion(ubicaciones.getString(i)));
			}

			JSONArray productos = json.getJSONArray("productos");
			for (int i = 0; i < productos.length(); i++) {
				club.agregarProducto(cargarProducto(productos.getJSONObject(i), archivo));
			}

			JSONArray existencias = json.getJSONArray("existencias");
			for (int i = 0; i < existencias.length(); i++) {
				JSONObject je = existencias.getJSONObject(i);
				Producto producto = club.buscarProducto(je.getString("producto"));
				Ubicacion ubicacion = club.buscarUbicacion(je.getString("ubicacion"));
				club.agregarExistencia(new Existencia(producto, ubicacion, je.getInt("cantidad")));
			}

			JSONArray codigos = json.getJSONArray("codigos");
			for (int i = 0; i < codigos.length(); i++) {
				JSONObject jc = codigos.getJSONObject(i);
				Empleado dueno = club.buscarEmpleado(jc.getInt("empleado"));
				CodigoDescuento codigo = new CodigoDescuento(jc.getInt("id"), dueno);
				if (!jc.getBoolean("activo")) {
					codigo.desactivar();
				}
				club.agregarCodigo(codigo);
			}

			JSONArray ventas = json.getJSONArray("ventas");
			for (int i = 0; i < ventas.length(); i++) {
				cargarVenta(ventas.getJSONObject(i), club, archivo);
			}

			JSONArray mensualidades = json.getJSONArray("mensualidades");
			for (int i = 0; i < mensualidades.length(); i++) {
				JSONObject jm = mensualidades.getJSONObject(i);
				Socio socio = club.buscarSocio(jm.getString("socioID"));
				socio.agregarMensualidad(new Mensualidad(YearMonth.parse(jm.getString("periodo")),
						jm.getDouble("valor"), LocalDate.parse(jm.getString("fechaPago"))));
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha mal escrita en " + archivo + ": " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private Producto cargarProducto(JSONObject jp, String archivo) throws PersistenciaException {
		String tipo = jp.getString("tipo");
		String nombre = jp.getString("nombre");
		double precio = jp.getDouble("precio");
		if (tipo.equals("ARTICULO")) {
			return new Articulo(nombre, precio, jp.getString("categoria"));
		} else if (tipo.equals("BEBIDA")) {
			return new Bebida(jp.getString("temperatura"), jp.getInt("tamanio"), nombre, precio);
		} else if (tipo.equals("SNACK")) {
			List<String> alergenos = new ArrayList<String>();
			JSONArray ja = jp.getJSONArray("alergenos");
			for (int i = 0; i < ja.length(); i++) {
				alergenos.add(ja.getString(i));
			}
			return new Snack(alergenos, nombre, precio);
		}
		throw new PersistenciaException("Tipo de producto desconocido en " + archivo + ": " + tipo);
	}

	private void cargarVenta(JSONObject jv, Club club, String archivo)
			throws NoEncontradoException, PersistenciaException {
		Usuario usuario = club.buscarUsuario(jv.getString("cliente"));
		if (!(usuario instanceof Cliente)) {
			throw new PersistenciaException("El usuario " + usuario.getLogin() + " no puede tener compras (" + archivo + ")");
		}
		Cliente cliente = (Cliente) usuario;
		LocalDateTime fechaHora = LocalDateTime.parse(jv.getString("fechaHora"));

		Venta venta;
		String tipo = jv.getString("tipo");
		if (tipo.equals("TIENDA")) {
			venta = new VentaTienda(cliente, fechaHora);
		} else if (tipo.equals("CAFETERIA")) {
			venta = new VentaCafeteria(cliente, fechaHora, jv.getInt("propina"));
		} else {
			throw new PersistenciaException("Tipo de venta desconocido en " + archivo + ": " + tipo);
		}

		JSONArray detalles = jv.getJSONArray("detalles");
		for (int i = 0; i < detalles.length(); i++) {
			JSONObject jd = detalles.getJSONObject(i);
			venta.agregarDetalle(club.buscarProducto(jd.getString("producto")), jd.getInt("cantidad"));
		}
		if (!jv.isNull("codigo")) {
			venta.aplicarCodigo(club.buscarCodigo(jv.getInt("codigo")));
		}
		venta.redimirPuntos(jv.getInt("puntosRedimidos"));
		venta.calcularTotal();

		club.agregarVenta(venta);
		cliente.agregarCompra(venta);
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray ubicaciones = new JSONArray();
		for (Ubicacion u : club.getUbicaciones()) {
			ubicaciones.put(u.getNombre());
		}

		JSONArray productos = new JSONArray();
		for (Producto p : club.getProductos()) {
			JSONObject jp = new JSONObject();
			jp.put("nombre", p.getNombre());
			jp.put("precio", p.getPrecio());
			if (p instanceof Articulo) {
				jp.put("tipo", "ARTICULO");
				jp.put("categoria", ((Articulo) p).getCategoria());
			} else if (p instanceof Bebida) {
				jp.put("tipo", "BEBIDA");
				jp.put("temperatura", ((Bebida) p).getTemperatura());
				jp.put("tamanio", ((Bebida) p).getTamanio());
			} else if (p instanceof Snack) {
				jp.put("tipo", "SNACK");
				JSONArray alergenos = new JSONArray();
				for (String a : ((Snack) p).getAlergenos()) {
					alergenos.put(a);
				}
				jp.put("alergenos", alergenos);
			}
			productos.put(jp);
		}

		JSONArray existencias = new JSONArray();
		for (Existencia e : club.getExistencias()) {
			JSONObject je = new JSONObject();
			je.put("producto", e.getProducto().getNombre());
			je.put("ubicacion", e.getUbicacion().getNombre());
			je.put("cantidad", e.getCantidad());
			existencias.put(je);
		}

		JSONArray codigos = new JSONArray();
		for (CodigoDescuento c : club.getCodigos()) {
			JSONObject jc = new JSONObject();
			jc.put("id", c.getIdDescuento());
			jc.put("empleado", c.getDueno().getIdEmpleado());
			jc.put("activo", c.isActivo());
			codigos.put(jc);
		}

		JSONArray ventas = new JSONArray();
		for (Venta v : club.getVentas()) {
			JSONObject jv = new JSONObject();
			jv.put("cliente", v.getCliente().getLogin());
			jv.put("fechaHora", v.getFechaHora().toString());
			jv.put("puntosRedimidos", v.getPuntosRedimidos());
			if (v.getCodigo() == null) {
				jv.put("codigo", JSONObject.NULL);
			} else {
				jv.put("codigo", v.getCodigo().getIdDescuento());
			}
			if (v instanceof VentaCafeteria) {
				jv.put("tipo", "CAFETERIA");
				jv.put("propina", ((VentaCafeteria) v).getPorcentajePropina());
			} else {
				jv.put("tipo", "TIENDA");
			}
			JSONArray detalles = new JSONArray();
			for (DetalleVenta d : v.getDetalles()) {
				JSONObject jd = new JSONObject();
				jd.put("producto", d.getProducto().getNombre());
				jd.put("cantidad", d.getCantidad());
				detalles.put(jd);
			}
			jv.put("detalles", detalles);
			ventas.put(jv);
		}

		JSONArray mensualidades = new JSONArray();
		for (Usuario u : club.getUsuarios()) {
			if (u instanceof Socio) {
				Socio socio = (Socio) u;
				for (Mensualidad m : socio.getMensualidades()) {
					JSONObject jm = new JSONObject();
					jm.put("socioID", socio.getSocioID());
					jm.put("periodo", m.getPeriodo().toString());
					jm.put("valor", m.getValor());
					jm.put("fechaPago", m.getFechaPago().toString());
					mensualidades.put(jm);
				}
			}
		}

		JSONObject json = new JSONObject();
		json.put("ubicaciones", ubicaciones);
		json.put("productos", productos);
		json.put("existencias", existencias);
		json.put("codigos", codigos);
		json.put("ventas", ventas);
		json.put("mensualidades", mensualidades);
		archivoJSON.escribir(archivo, json);
	}
}
