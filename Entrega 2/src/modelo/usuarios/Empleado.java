package modelo.usuarios;

/**
 * Empleado del club. Entrenador y Fisioterapeuta heredan de esta clase.
 */
public class Empleado extends Cliente {

	private int idEmpleado;

	public Empleado(String login, String password, String nombre, String apellidos, int idEmpleado) {
		super(login, password, nombre, apellidos);
		this.idEmpleado = idEmpleado;
	}

	public int getIdEmpleado() {
		return idEmpleado;
	}
}
