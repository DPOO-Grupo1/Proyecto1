package modelo.usuarios;

/**
 * Administrador del club. No hereda de Cliente porque no compra ni reserva.
 */
public class Administrador extends Usuario {

	public Administrador(String login, String password) {
		super(login, password);
	}
}
