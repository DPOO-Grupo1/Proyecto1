package logica;

import excepciones.AutenticacionException;
import excepciones.NoEncontradoException;
import modelo.usuarios.Usuario;

/**
 * Maneja el ingreso de los usuarios al sistema. Todos (socios, empleados y
 * administrador) entran con login y contraseña.
 */
public class GestorUsuarios extends Gestor {

	public GestorUsuarios(Club club) {
		super(club);
	}

	/**
	 * Devuelve el usuario si el login existe y la contraseña es correcta.
	 * El mensaje de error no dice cuál de los dos falló.
	 */
	public Usuario autenticar(String login, String password) throws AutenticacionException {
		if (login == null || login.trim().isEmpty() || password == null) {
			throw new AutenticacionException("Debe escribir el login y la contraseña");
		}
		Usuario usuario;
		try {
			usuario = club.buscarUsuario(login.trim());
		} catch (NoEncontradoException e) {
			throw new AutenticacionException("Login o contraseña incorrectos");
		}
		if (!usuario.verificarPassword(password)) {
			throw new AutenticacionException("Login o contraseña incorrectos");
		}
		return usuario;
	}
}
