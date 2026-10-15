package modelo.usuarios;

/**
 * Cualquier persona que entra al sistema con login y contraseña.
 */
public abstract class Usuario {

	protected String login;
	protected String password;

	public Usuario(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public boolean verificarPassword(String password) {
		return this.password.equals(password);
	}
}
