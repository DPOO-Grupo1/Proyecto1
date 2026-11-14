package modelo.usuarios;

/**
 * Persona que no es socia ni empleada y entra solo para jugar torneos internos:
 * un estudiante de la universidad o alguien externo. No compra ni reserva, por eso
 * no hereda de Cliente.
 */
public class ParticipanteExterno extends Usuario {

	private String nombre;
	private String apellidos;
	private String documento;
	private boolean estudiante;

	public ParticipanteExterno(String login, String password, String nombre, String apellidos, String documento,
			boolean estudiante) {
		super(login, password);
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.documento = documento;
		this.estudiante = estudiante;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	@Override
	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public String getDocumento() {
		return documento;
	}

	public boolean isEstudiante() {
		return estudiante;
	}

	@Override
	public String toString() {
		return getNombreCompleto() + " (" + (estudiante ? "estudiante" : "externo") + ", documento " + documento + ")";
	}
}
