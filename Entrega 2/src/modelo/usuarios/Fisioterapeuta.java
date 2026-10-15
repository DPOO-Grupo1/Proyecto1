package modelo.usuarios;

/**
 * Fisioterapeuta del club. Es una clase aparte por la regla de turnos.
 */
public class Fisioterapeuta extends Empleado {

	public Fisioterapeuta(String login, String password, String nombre, String apellidos, int idEmpleado) {
		super(login, password, nombre, apellidos, idEmpleado);
	}
}
