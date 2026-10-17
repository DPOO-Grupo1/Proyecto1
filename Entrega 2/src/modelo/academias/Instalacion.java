package modelo.academias;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import modelo.entrenamiento.SesionEntrenamiento;

/**
 * Instalación deportiva del club (canchas de tenis, pádel, mesas de tenis de mesa, escenarios de conjunto).
 */
public abstract class Instalacion {

	protected String id;
	protected String nombre;
	protected Disciplina disciplina;
	protected List<Reserva> reservas;
	protected List<SesionEntrenamiento> sesiones;

	public Instalacion(String id, String nombre, Disciplina disciplina) {
		this.id = id;
		this.nombre = nombre;
		this.disciplina = disciplina;
		this.reservas = new ArrayList<Reserva>();
		this.sesiones = new ArrayList<SesionEntrenamiento>();
	}

	public String getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void agregarReserva(Reserva reserva) {
		reservas.add(reserva);
	}

	public List<SesionEntrenamiento> getSesiones() {
		return sesiones;
	}

	public void agregarSesion(SesionEntrenamiento sesion) {
		sesiones.add(sesion);
	}

	/**
	 * indica si los socios pueden reservar esta instalación
	 */
	public abstract boolean esReservable();

	/**
	 * valida si la instalación admite la cantidad de jugadores y la modalidad solicitadas
	 */
	public abstract boolean admite(int numJugadores, Modalidad modalidad);

	public abstract boolean restringeBebidaCaliente();

	/**
	 * Revisa si la instalación está desocupada en esa fecha y horario.
	 * Revisa tanto reservas activas como sesiones de entrenamiento.
	*/
	public boolean estaLibre(LocalDate fecha, LocalTime inicio, LocalTime fin) {
		for (Reserva reserva : reservas) {
			if (reserva.tieneConflictoCon(fecha, inicio, fin)) {
				return false;
			}
		}
		for (SesionEntrenamiento sesion : sesiones) {
			if (sesion.tieneConflictoCon(fecha, inicio, fin)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return id + ": " + nombre + (disciplina != null ? " (" + disciplina.getNombre() + ")" : "");
	}
}
