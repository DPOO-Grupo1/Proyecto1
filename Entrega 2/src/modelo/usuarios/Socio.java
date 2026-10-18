package modelo.usuarios;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Reserva;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.JugadorEquipo;
import modelo.reportes.Mensualidad;

/**
 * Socio del club. Puede jugar en equipos, inscribirse en academias, reservar y
 * comprar.
 */
public class Socio extends Cliente {

	private String socioID;
	private LocalDate fechaNacimiento;
	private Entrenador entrenador;
	private List<JugadorEquipo> participaciones;
	private List<Inscripcion> inscripciones;
	private List<Reserva> reservas;
	private boolean alDia;
	private List<Mensualidad> mensualidades;

	public Socio(String login, String password, String nombre, String apellidos, String socioID,
			LocalDate fechaNacimiento) {
		super(login, password, nombre, apellidos);
		this.socioID = socioID;
		this.fechaNacimiento = fechaNacimiento;
		this.entrenador = null;
		this.participaciones = new ArrayList<JugadorEquipo>();
		this.inscripciones = new ArrayList<Inscripcion>();
		this.reservas = new ArrayList<Reserva>();
		this.alDia = true;
		this.mensualidades = new ArrayList<Mensualidad>();
	}

	public String getSocioID() {
		return socioID;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	/**
	 * Edad en años cumplidos a una fecha. No se guarda porque se puede calcular.
	 */
	public int getEdad(LocalDate fecha) {
		return Period.between(fechaNacimiento, fecha).getYears();
	}

	/**
	 * Null si el socio no es entrenador.
	 */
	public Entrenador getEntrenador() {
		return entrenador;
	}

	/**
	 * Solo lo llama Entrenador.setSocio.
	 */
	void setEntrenador(Entrenador entrenador) {
		this.entrenador = entrenador;
	}

	public List<JugadorEquipo> getParticipaciones() {
		return participaciones;
	}

	public void agregarParticipacion(JugadorEquipo participacion) {
		participaciones.add(participacion);
	}

	/**
	 * True si el socio ya está activo en un equipo de ese deporte.
	 */
	public boolean juegaEnDeporte(DeporteEquipo deporte) {
		for (JugadorEquipo participacion : participaciones) {
			if (participacion.estaActivo() && participacion.getEquipo().getDeporte() == deporte) {
				return true;
			}
		}
		return false;
	}

	/**
	 * True si el socio también es entrenador del club. Sirve para la regla de
	 * niveles
	 * del partido de práctica: con un entrenador, no importa el nivel.
	 */
	public boolean esEntrenador() {
		return entrenador != null;
	}

	// Academias

	public List<Inscripcion> getInscripciones() {
		return inscripciones;
	}

	public void agregarInscripcion(Inscripcion inscripcion) {
		inscripciones.add(inscripcion);
	}

	/**
	 * La inscripción del socio en esa disciplina, o null si no está inscrito.
	 */
	public Inscripcion getInscripcion(Disciplina disciplina) {
		for (Inscripcion inscripcion : inscripciones) {
			if (inscripcion.getDisciplina() == disciplina) {
				return inscripcion;
			}
		}
		return null;
	}

	// Reservas e instalaciones

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void agregarReserva(Reserva reserva) {
		reservas.add(reserva);
	}

	/**
	 * Por defecto los socios están al día, salvo que se marquen en mora (como el
	 * socio S9 en las pruebas).
	 */
	public boolean estaAlDia(LocalDate fecha) {
		return alDia;
	}

	public void setAlDia(boolean alDia) {
		this.alDia = alDia;
	}
	
	//métodos de periodicidad
	public List<Mensualidad> getMensualidades() {
	    return mensualidades;
	}

	public void agregarMensualidad(Mensualidad mensualidad) {
	    mensualidades.add(mensualidad);
	}

	public boolean tieneMensualidadPagada(YearMonth periodo) {
	    for (Mensualidad m : mensualidades) {
	        if (m.getPeriodo().equals(periodo) && m.isPagada()) {
	            return true;
	        }
	    }
	    return false;
	}
}
