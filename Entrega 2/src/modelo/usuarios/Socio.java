package modelo.usuarios;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import modelo.deportes.DeporteEquipo;
import modelo.deportes.JugadorEquipo;

/**
 * Socio del club. Puede jugar en equipos, inscribirse en academias, reservar y comprar.
 */
public class Socio extends Cliente {

	private String socioID;
	private LocalDate fechaNacimiento;
	private Entrenador entrenador;
	private List<JugadorEquipo> participaciones;

	public Socio(String login, String password, String nombre, String apellidos, String socioID,
			LocalDate fechaNacimiento) {
		super(login, password, nombre, apellidos);
		this.socioID = socioID;
		this.fechaNacimiento = fechaNacimiento;
		this.entrenador = null;
		this.participaciones = new ArrayList<JugadorEquipo>();
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
}
