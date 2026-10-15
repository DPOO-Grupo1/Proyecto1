package modelo.usuarios;

/**
 * Socios y empleados: compran en el club y acumulan puntos de fidelidad.
 */
public abstract class Cliente extends Usuario {

	protected String nombre;
	protected String apellidos;
	protected int puntosFidelidad;

	public Cliente(String login, String password, String nombre, String apellidos) {
		super(login, password);
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntosFidelidad = 0;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}
}
