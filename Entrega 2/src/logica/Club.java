package logica;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.NoEncontradoException;
import modelo.academias.Disciplina;
import modelo.academias.Instalacion;
import modelo.academias.Reserva;
import modelo.entrenamiento.SesionEntrenamiento;
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
	private List<Disciplina> disciplinas;
	private List<Instalacion> instalaciones;
	private List<Reserva> reservas;
	private List<SesionEntrenamiento> sesiones;
	private LocalDate fechaActual;

	public Club() {
		this.usuarios = new HashMap<String, Usuario>();
		this.deportes = new ArrayList<DeporteEquipo>();
		this.categorias = new ArrayList<Categoria>();
		this.equipos = new ArrayList<Equipo>();
		this.partidosInternos = new ArrayList<PartidoInterno>();
		this.competencias = new ArrayList<Competencia>();
		this.disciplinas = new ArrayList<Disciplina>();
		this.instalaciones = new ArrayList<Instalacion>();
		this.reservas = new ArrayList<Reserva>();
		this.sesiones = new ArrayList<SesionEntrenamiento>();
		this.fechaActual = LocalDate.now();
	}

	// Fecha actual

	/**
	 * Fecha que se usa como "hoy"
	 */
	public LocalDate getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(LocalDate fechaActual) {
		this.fechaActual = fechaActual;
	}

	// Usuarios

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

	// Deportes y categorías

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

	// Equipos

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
	 * El id es el total de partidos + 1. Funciona porque los partidos nunca se
	 * borran.
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

	// Academias

	public void agregarDisciplina(Disciplina disciplina) {
		disciplinas.add(disciplina);
	}

	public List<Disciplina> getDisciplinas() {
		return disciplinas;
	}

	public Disciplina buscarDisciplina(String nombre) throws NoEncontradoException {
		for (Disciplina disciplina : disciplinas) {
			if (disciplina.getNombre().equalsIgnoreCase(nombre)) {
				return disciplina;
			}
		}
		throw new NoEncontradoException(
				"No existe la disciplina '" + nombre + "': las academias son tenis, pádel y tenis de mesa");
	}

	// Instalaciones y Reservas

	public void agregarInstalacion(Instalacion instalacion) {
		instalaciones.add(instalacion);
	}

	public List<Instalacion> getInstalaciones() {
		return instalaciones;
	}

	public Instalacion buscarInstalacion(String id) throws NoEncontradoException {
		for (Instalacion instalacion : instalaciones) {
			if (instalacion.getId().equalsIgnoreCase(id)) {
				return instalacion;
			}
		}
		throw new NoEncontradoException("No existe una instalación con id '" + id + "'");
	}

	public void agregarReserva(Reserva reserva) {
		reservas.add(reserva);
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public Reserva buscarReserva(String id) throws NoEncontradoException {
		for (Reserva reserva : reservas) {
			if (reserva.getId().equalsIgnoreCase(id)) {
				return reserva;
			}
		}
		throw new NoEncontradoException("No existe una reserva con id '" + id + "'");
	}

	public void agregarSesion(SesionEntrenamiento sesion) {
		sesiones.add(sesion);
	}

	public List<SesionEntrenamiento> getSesiones() {
		return sesiones;
	}

	/**
	 * Cuenta cuántas reservas activas de instalaciones de esa disciplina se cruzan
	 * Sirve para validar que no se exceda el cupo simultáneo de la disciplina.
	 */
	public int contarReservasSimultaneas(Disciplina disciplina, LocalDate fecha, LocalTime inicio, LocalTime fin) {
		int total = 0;
		for (Reserva reserva : reservas) {
			if (reserva.estaActiva()
					&& reserva.getInstalacion().getDisciplina() == disciplina
					&& reserva.tieneConflictoCon(fecha, inicio, fin)) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Revisa si el socio ya tiene un compromiso en esa fecha y horario:
	 * ya sea como titular de una reserva activa o convocado a una sesión de
	 */
	public boolean tieneCompromiso(Socio socio, LocalDate fecha, LocalTime inicio, LocalTime fin) {
		for (Reserva reserva : reservas) {
			if (reserva.estaActiva() && reserva.getTitular() == socio
					&& reserva.tieneConflictoCon(fecha, inicio, fin)) {
				return true;
			}
		}
		for (SesionEntrenamiento sesion : sesiones) {
			if (sesion.tieneConvocado(socio) && sesion.tieneConflictoCon(fecha, inicio, fin)) {
				return true;
			}
		}
		return false;
	}
}
