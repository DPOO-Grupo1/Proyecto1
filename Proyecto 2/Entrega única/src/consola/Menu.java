package consola;

import java.util.List;

import excepciones.ClubException;
import logica.Club;
import modelo.usuarios.Usuario;

/**
 * Un menú de un tema para un rol (por ejemplo, "Deportes y competencias" para el socio).
 * Cada menú lista sus opciones y ejecuta la elegida. Si un gestor rechaza la acción,
 * el menú muestra el motivo y vuelve a mostrar las opciones.
 * Para agregar un menú nuevo basta con heredar de esta clase y registrarlo en la consola del rol.
 */
public abstract class Menu {

	protected Consola consola;
	protected Club club;
	protected Usuario usuario;

	public Menu(Consola consola) {
		this.consola = consola;
		this.club = consola.getClub();
		this.usuario = consola.getUsuario();
	}

	/** Nombre que aparece en el menú principal. */
	public abstract String getTitulo();

	/** Opciones en el orden en que se numeran (desde 1). */
	public abstract List<String> getOpciones();

	/** Ejecuta la opción elegida (1 es la primera de getOpciones). */
	public abstract void ejecutar(int opcion) throws ClubException;

	public void mostrar() {
		while (true) {
			int opcion = consola.leerOpcion(getTitulo(), getOpciones(), "Volver");
			if (opcion == 0) {
				return;
			}
			try {
				ejecutar(opcion);
			} catch (ClubException e) {
				System.out.println("RECHAZADO: " + e.getMessage());
			}
		}
	}
}
