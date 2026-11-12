package modelo.usuarios;

import java.util.ArrayList;
import java.util.List;

import modelo.personal.Sugerencia;
import modelo.personal.Turno;

/**
 * Empleado del club. Entrenador y Fisioterapeuta heredan de esta clase.
 */
public class Empleado extends Cliente {

	private int idEmpleado;
	private List<Turno> turnos;
	private List<Sugerencia> sugerencias;

	public Empleado(String login, String password, String nombre, String apellidos, int idEmpleado) {
		super(login, password, nombre, apellidos);
		this.idEmpleado = idEmpleado;
		this.turnos = new ArrayList<Turno>();
		this.sugerencias = new ArrayList<Sugerencia>();
	}

	public int getIdEmpleado() {
		return idEmpleado;
	}
	
	//métodos:
	public List<Turno> getTurnos() {
	    return turnos;
	}

	public void agregarTurno(Turno turno) {
	    turnos.add(turno);
	}

	public List<Sugerencia> getSugerencias() {
	    return sugerencias;
	}

	public void agregarSugerencia(Sugerencia sugerencia) {
	    sugerencias.add(sugerencia);
	}
}
