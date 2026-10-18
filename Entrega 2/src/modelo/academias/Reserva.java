package modelo.academias;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import modelo.usuarios.Socio;

/**
 * Reserva de una instalación deportiva hecha por un socio titular.
 */
public class Reserva {

	private String id;
	private Socio titular;
	private Instalacion instalacion;
	private LocalDate fecha;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private int numJugadores;
	private Modalidad modalidad;
	private EstadoReserva estado;
	private PartidoPractica partido;

	public Reserva(String id, Socio titular, Instalacion instalacion, LocalDate fecha,
			LocalTime horaInicio, LocalTime horaFin, int numJugadores, Modalidad modalidad) {
		this.id = id;
		this.titular = titular;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.numJugadores = numJugadores;
		this.modalidad = modalidad;
		this.estado = EstadoReserva.ACTIVA;
		this.partido = null;
	}

	public String getId() {
		return id;
	}

	public Socio getTitular() {
		return titular;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public int getNumJugadores() {
		return numJugadores;
	}

	public Modalidad getModalidad() {
		return modalidad;
	}

	public EstadoReserva getEstado() {
		return estado;
	}

	public void setEstado(EstadoReserva estado) {
		this.estado = estado;
	}

	public PartidoPractica getPartido() {
		return partido;
	}

	public void setPartido(PartidoPractica partido) {
		this.partido = partido;
	}

	public boolean estaActiva() {
		return estado == EstadoReserva.ACTIVA;
	}

	/**
	 * Cancela la reserva y automáticamente cancela el partido de práctica asociado
	 */
	public void cancelar() {
		this.estado = EstadoReserva.CANCELADA;
		if (partido != null) {
			partido.cancelar();
		}
	}

	/**
	 * Horas que faltan desde el momento actual hasta el inicio de la reserva.
	 * Sirve para validar la regla de cancelación con al menos 2 horas de
	 * anticipación
	 */
	public double horasHastaInicio(LocalDate fechaActual, LocalTime horaActual) {
		LocalDateTime ahora = LocalDateTime.of(fechaActual, horaActual);
		LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
		return Duration.between(ahora, inicio).toMinutes() / 60.0;
	}

	/**
	 * Revisa si la reserva tiene conflicto horario con otro rango en la misma
	 * fecha.
	 * Bloques contiguos no tienen conflicto: [14:00, 15:30) y [15:30, 17:00) no
	 * chocan.
	 */
	public boolean tieneConflictoCon(LocalDate fecha, LocalTime inicio, LocalTime fin) {
		if (!estaActiva() || !this.fecha.equals(fecha)) {
			return false;
		}
		return inicio.isBefore(this.horaFin) && fin.isAfter(this.horaInicio);
	}

	@Override
	public String toString() {
		return "Reserva " + id + " en " + instalacion.getNombre() + " (" + fecha + " " + horaInicio
				+ "-" + horaFin + ", " + modalidad + ", " + estado + ")";
	}
}
