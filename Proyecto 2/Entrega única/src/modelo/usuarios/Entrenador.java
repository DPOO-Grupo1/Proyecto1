package modelo.usuarios;

/**
 * Entrenador del club. Dirige equipos y también puede ser socio.
 */
public class Entrenador extends Empleado {

	private Socio socio;

	public Entrenador(String login, String password, String nombre, String apellidos, int idEmpleado) {
		super(login, password, nombre, apellidos, idEmpleado);
		this.socio = null;
	}

	/**
	 * Null si el entrenador no es socio.
	 */
	public Socio getSocio() {
		return socio;
	}

	/**
	 * Conecta la relación en los dos sentidos.
	 */
	public void setSocio(Socio socio) {
		this.socio = socio;
		if (socio != null) {
			socio.setEntrenador(this);
		}
	}
}
