package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.NoEncontradoException;
import modelo.competencias.Competencia;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.partidos.Partido;
import modelo.partidos.PartidoCompetencia;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Empleado;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;
import modelo.tienda.Existencia;
import modelo.tienda.Producto;
import modelo.tienda.Ubicacion;
import modelo.tienda.Venta;

/**
 * Guarda todas las listas del club y busca por id o por nombre.
 * No tiene reglas de negocio, eso lo hacen los gestores.
 */
public class Club {

	private Map<String, Usuario> usuarios;
	private List<DeporteEquipo> deportes;
	private List<Categoria> categorias;
	private List<Equipo> equipos;
	private List<PartidoInterno> partidosInternos;
	private List<Competencia> competencias;
	private LocalDate fechaActual;
	private List<Producto> productos;
	private List<Ubicacion> ubicaciones;
	private List<Existencia> existencias;
	private List<Venta> ventas;

	public Club() {
		this.usuarios = new HashMap<String, Usuario>();
		this.deportes = new ArrayList<DeporteEquipo>();
		this.categorias = new ArrayList<Categoria>();
		this.equipos = new ArrayList<Equipo>();
		this.partidosInternos = new ArrayList<PartidoInterno>();
		this.competencias = new ArrayList<Competencia>();
		this.fechaActual = LocalDate.now();
		this.productos = new ArrayList<Producto>();
		this.ubicaciones = new ArrayList<Ubicacion>();
		this.existencias = new ArrayList<Existencia>();
		this.ventas = new ArrayList<Venta>();
	}

	// ---------------- Fecha actual ----------------

	/**
	 * Fecha que se usa como "hoy". Se puede cambiar para las pruebas.
	 */
	public LocalDate getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(LocalDate fechaActual) {
		this.fechaActual = fechaActual;
	}

	// ---------------- Usuarios ----------------

	public void agregarUsuario(Usuario usuario) {
		usuarios.put(usuario.getLogin(), usuario);
	}

	public boolean existeUsuario(String login) {
		return usuarios.containsKey(login);
	}

	public List<Usuario> getUsuarios() {
		return new ArrayList<Usuario>(usuarios.values());
	}

	public Usuario buscarUsuario(String login) throws NoEncontradoException {
		Usuario usuario = usuarios.get(login);
		if (usuario == null) {
			throw new NoEncontradoException("No existe un usuario con login '" + login + "'");
		}
		return usuario;
	}

	public Socio buscarSocio(String socioID) throws NoEncontradoException {
		for (Usuario usuario : usuarios.values()) {
			if (usuario instanceof Socio) {
				Socio socio = (Socio) usuario;
				if (socio.getSocioID().equals(socioID)) {
					return socio;
				}
			}
		}
		throw new NoEncontradoException("No existe un socio con id '" + socioID + "'");
	}

	public Empleado buscarEmpleado(int idEmpleado) throws NoEncontradoException {
		for (Usuario usuario : usuarios.values()) {
			if (usuario instanceof Empleado) {
				Empleado empleado = (Empleado) usuario;
				if (empleado.getIdEmpleado() == idEmpleado) {
					return empleado;
				}
			}
		}
		throw new NoEncontradoException("No existe un empleado con id " + idEmpleado);
	}

	// ---------------- Deportes y categorías ----------------

	public void agregarDeporte(DeporteEquipo deporte) {
		deportes.add(deporte);
	}

	public List<DeporteEquipo> getDeportes() {
		return deportes;
	}

	public DeporteEquipo buscarDeporte(String nombre) throws NoEncontradoException {
		for (DeporteEquipo deporte : deportes) {
			if (deporte.getNombre().equalsIgnoreCase(nombre)) {
				return deporte;
			}
		}
		throw new NoEncontradoException("No existe el deporte '" + nombre + "'");
	}

	public void agregarCategoria(Categoria categoria) {
		categorias.add(categoria);
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public Categoria buscarCategoria(String nombre) throws NoEncontradoException {
		for (Categoria categoria : categorias) {
			if (categoria.getNombre().equalsIgnoreCase(nombre)) {
				return categoria;
			}
		}
		throw new NoEncontradoException("No existe la categoría '" + nombre + "'");
	}

	// ---------------- Equipos ----------------

	public void agregarEquipo(Equipo equipo) {
		equipos.add(equipo);
	}

	public List<Equipo> getEquipos() {
		return equipos;
	}

	public Equipo buscarEquipo(String nombre) throws NoEncontradoException {
		for (Equipo equipo : equipos) {
			if (equipo.getNombre().equalsIgnoreCase(nombre)) {
				return equipo;
			}
		}
		throw new NoEncontradoException("No existe el equipo '" + nombre + "'");
	}

	/**
	 * Los nombres de equipo no se repiten en el club.
	 */
	public boolean existeEquipo(String nombre) {
		for (Equipo equipo : equipos) {
			if (equipo.getNombre().equalsIgnoreCase(nombre)) {
				return true;
			}
		}
		return false;
	}

	// ---------------- Partidos ----------------

	/**
	 * El id es el total de partidos + 1. Funciona porque los partidos nunca se borran.
	 */
	public int siguienteIdPartido() {
		int total = partidosInternos.size();
		for (Competencia competencia : competencias) {
			total += competencia.getPartidos().size();
		}
		return total + 1;
	}

	public void agregarPartidoInterno(PartidoInterno partido) {
		partidosInternos.add(partido);
	}

	public List<PartidoInterno> getPartidosInternos() {
		return partidosInternos;
	}

	public PartidoInterno buscarPartidoInterno(int id) throws NoEncontradoException {
		for (PartidoInterno partido : partidosInternos) {
			if (partido.getId() == id) {
				return partido;
			}
		}
		throw new NoEncontradoException("No existe un partido interno con id " + id);
	}

	/**
	 * Partidos internos y de competencia del equipo.
	 */
	public List<Partido> getPartidosDe(Equipo equipo) {
		List<Partido> partidos = new ArrayList<Partido>();
		for (PartidoInterno partido : partidosInternos) {
			if (partido.participa(equipo)) {
				partidos.add(partido);
			}
		}
		for (Competencia competencia : competencias) {
			for (PartidoCompetencia partido : competencia.getPartidos()) {
				if (partido.participa(equipo)) {
					partidos.add(partido);
				}
			}
		}
		return partidos;
	}

	/**
	 * Busca en internos y en competencias. Lo usa la persistencia.
	 */
	public Partido buscarPartido(int id) throws NoEncontradoException {
		for (PartidoInterno partido : partidosInternos) {
			if (partido.getId() == id) {
				return partido;
			}
		}
		for (Competencia competencia : competencias) {
			for (PartidoCompetencia partido : competencia.getPartidos()) {
				if (partido.getId() == id) {
					return partido;
				}
			}
		}
		throw new NoEncontradoException("No existe un partido con id " + id);
	}

	// ---------------- Competencias ----------------

	public void agregarCompetencia(Competencia competencia) {
		competencias.add(competencia);
	}

	public List<Competencia> getCompetencias() {
		return competencias;
	}

	public boolean existeCompetencia(String nombre, int temporada) {
		for (Competencia competencia : competencias) {
			if (competencia.getNombre().equalsIgnoreCase(nombre) && competencia.getTemporada() == temporada) {
				return true;
			}
		}
		return false;
	}

	public Competencia buscarCompetencia(String nombre, int temporada) throws NoEncontradoException {
		for (Competencia competencia : competencias) {
			if (competencia.getNombre().equalsIgnoreCase(nombre) && competencia.getTemporada() == temporada) {
				return competencia;
			}
		}
		throw new NoEncontradoException("No existe la competencia '" + nombre + "' de la temporada " + temporada);
	}
	// ---------------- Tienda (Área C) ----------------

	public void agregarProducto(Producto producto) {
	    productos.add(producto);
	}

	public Producto buscarProducto(String nombre) throws NoEncontradoException {
	    for (Producto p : productos) {
	        if (p.getNombre().equalsIgnoreCase(nombre)) {
	            return p;
	        }
	    }
	    throw new NoEncontradoException("No existe el producto '" + nombre + "'");
	}

	public void agregarUbicacion(Ubicacion ubicacion) {
	    ubicaciones.add(ubicacion);
	}

	public Ubicacion buscarUbicacion(String nombre) throws NoEncontradoException {
	    for (Ubicacion u : ubicaciones) {
	        if (u.getNombre().equalsIgnoreCase(nombre)) {
	            return u;
	        }
	    }
	    throw new NoEncontradoException("No existe la ubicación '" + nombre + "'");
	}

	public void agregarExistencia(Existencia existencia) {
	    existencias.add(existencia);
	}

	public Existencia buscarExistencia(Producto producto, Ubicacion ubicacion) throws NoEncontradoException {
	    for (Existencia e : existencias) {
	        if (e.getProducto() == producto && e.getUbicacion() == ubicacion) {
	            return e;
	        }
	    }
	    throw new NoEncontradoException("No hay existencias de '" + producto.getNombre() + "' en " + ubicacion.getNombre());
	}

	public void agregarVenta(Venta venta) {
	    ventas.add(venta);
	}

	public List<Venta> getVentas() {
	    return ventas;
	}
}
