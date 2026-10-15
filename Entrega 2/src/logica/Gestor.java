package logica;

import excepciones.PermisoDenegadoException;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Clase padre de todos los gestores. Guarda el club y revisa los permisos.
 */
public abstract class Gestor {

	protected Club club;

	public Gestor(Club club) {
		this.club = club;
	}

	protected void verificarAdministrador(Usuario quien) throws PermisoDenegadoException {
		if (!(quien instanceof Administrador)) {
			throw new PermisoDenegadoException(nombreDe(quien) + " no tiene permiso: solo el administrador puede hacer esto");
		}
	}

	protected void verificarEntrenador(Usuario quien) throws PermisoDenegadoException {
		if (!(quien instanceof Entrenador)) {
			throw new PermisoDenegadoException(nombreDe(quien) + " no tiene permiso: solo un entrenador puede hacer esto");
		}
	}

	protected void verificarSocio(Usuario quien) throws PermisoDenegadoException {
		if (!(quien instanceof Socio)) {
			throw new PermisoDenegadoException(nombreDe(quien) + " no tiene permiso: solo un socio puede hacer esto");
		}
	}

	private String nombreDe(Usuario quien) {
		if (quien == null) {
			return "El usuario";
		}
		return "El usuario '" + quien.getLogin() + "'";
	}
}
