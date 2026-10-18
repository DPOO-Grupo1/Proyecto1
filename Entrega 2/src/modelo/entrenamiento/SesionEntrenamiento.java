package modelo.entrenamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import modelo.academias.Disciplina;
import modelo.academias.Instalacion;
import modelo.deportes.Equipo;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Sesión de entrenamiento dirigida por un entrenador en una instalación.
 * Puede ser individual o grupal de una disciplina de academia, o de un equipo de conjunto.
 * Administra las convocatorias, asistencias y evaluaciones de los socios.
 */
public class SesionEntrenamiento {

	private String id;
	private Entrenador entrenador;
	private Instalacion instalacion;
	private LocalDate fecha;
	private LocalTime hora;
	private int duracionMin;
	private Disciplina disciplina;
	private Equipo equipo;
	private List<Convocatoria> convocatorias;
	private List<Socio> convocados;
	private boolean finalizada;

	public SesionEntrenamiento(String id, Entrenador entrenador, Instalacion instalacion,
			LocalDate fecha, LocalTime hora, int duracionMin) {
		this(id, entrenador, instalacion, fecha, hora, duracionMin, (Disciplina) null);
	}

	public SesionEntrenamiento(String id, Entrenador entrenador, Instalacion instalacion,
			LocalDate fecha, LocalTime hora, int duracionMin, Disciplina disciplina) {
		this.id = id;
		this.entrenador = entrenador;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.hora = hora;
		this.duracionMin = duracionMin;
		this.disciplina = disciplina;
		this.equipo = null;
		this.convocatorias = new ArrayList<Convocatoria>();
		this.convocados = new ArrayList<Socio>();
		this.finalizada = false;
	}

	public SesionEntrenamiento(String id, Entrenador entrenador, Instalacion instalacion,
			LocalDate fecha, LocalTime hora, int duracionMin, Equipo equipo) {
		this.id = id;
		this.entrenador = entrenador;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.hora = hora;
		this.duracionMin = duracionMin;
		this.disciplina = null;
		this.equipo = equipo;
		this.convocatorias = new ArrayList<Convocatoria>();
		this.convocados = new ArrayList<Socio>();
		this.finalizada = false;
	}

	public String getId() {
		return id;
	}

	public Entrenador getEntrenador() {
		return entrenador;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public int getDuracionMin() {
		return duracionMin;
	}

	public LocalTime getHoraFin() {
		return hora.plusMinutes(duracionMin);
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public boolean esDeEquipo() {
		return equipo != null;
	}

	public boolean estaFinalizada() {
		return finalizada;
	}

	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public void finalizar() {
		this.finalizada = true;
	}

	public List<Convocatoria> getConvocatorias() {
		return convocatorias;
	}

	public List<Socio> getConvocados() {
		return convocados;
	}

	/**
	 * Agrega una convocatoria a la sesión y actualiza la lista de socios convocados.
	 */
	public void agregarConvocatoria(Convocatoria convocatoria) {
		if (convocatoria != null && !convocatorias.contains(convocatoria)) {
			convocatorias.add(convocatoria);
			if (!convocados.contains(convocatoria.getSocio())) {
				convocados.add(convocatoria.getSocio());
			}
		}
	}

	/**
	 * Convoca a un socio creando su convocatoria respectiva.
	 */
	public Convocatoria convocar(Socio socio) {
		Convocatoria existente = buscarConvocatoria(socio);
		if (existente != null) {
			return existente;
		}
		Convocatoria nueva = new Convocatoria(socio, this);
		agregarConvocatoria(nueva);
		return nueva;
	}

	public boolean tieneConvocado(Socio socio) {
		return convocados.contains(socio);
	}

	public Convocatoria buscarConvocatoria(Socio socio) {
		for (Convocatoria c : convocatorias) {
			if (c.getSocio().equals(socio)) {
				return c;
			}
		}
		return null;
	}

	public Convocatoria buscarConvocatoria(String socioID) {
		for (Convocatoria c : convocatorias) {
			if (c.getSocio().getSocioID().equalsIgnoreCase(socioID)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Revisa si la sesión tiene conflicto horario en fecha y rango de horas.
	 * Bloques contiguos no tienen conflicto.
	 */
	public boolean tieneConflictoCon(LocalDate fecha, LocalTime inicio, LocalTime fin) {
		if (!this.fecha.equals(fecha)) {
			return false;
		}
		LocalTime horaFin = getHoraFin();
		return inicio.isBefore(horaFin) && fin.isAfter(this.hora);
	}

	@Override
	public String toString() {
		String tipo = esDeEquipo() ? "Equipo " + equipo.getNombre() : (disciplina != null ? disciplina.getNombre() : "General");
		return "Sesión " + id + " [" + tipo + "] en " + instalacion.getNombre() + " (" + fecha + " " + hora + "-" + getHoraFin() + ")";
	}
}
