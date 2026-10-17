package modelo.entrenamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import modelo.academias.Instalacion;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Sesión de entrenamiento dirigida por un entrenador en una instalación.
 * Se completa en la parte 5 con convocatorias y evaluaciones.
 */
public class SesionEntrenamiento {

	private String id;
	private Entrenador entrenador;
	private Instalacion instalacion;
	private LocalDate fecha;
	private LocalTime hora;
	private int duracionMin;
	private List<Socio> convocados;
	private boolean finalizada;

	public SesionEntrenamiento(String id, Entrenador entrenador, Instalacion instalacion,
			LocalDate fecha, LocalTime hora, int duracionMin) {
		this.id = id;
		this.entrenador = entrenador;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.hora = hora;
		this.duracionMin = duracionMin;
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

	public boolean estaFinalizada() {
		return finalizada;
	}

	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public List<Socio> getConvocados() {
		return convocados;
	}

	public void convocar(Socio socio) {
		if (!convocados.contains(socio)) {
			convocados.add(socio);
		}
	}

	public boolean tieneConvocado(Socio socio) {
		return convocados.contains(socio);
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
		return "Sesión " + id + " en " + instalacion.getNombre() + " (" + fecha + " " + hora + "-" + getHoraFin() + ")";
	}
}
