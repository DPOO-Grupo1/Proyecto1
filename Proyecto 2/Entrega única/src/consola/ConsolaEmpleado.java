package consola;

import java.util.ArrayList;
import java.util.List;

import consola.deportes.MenuDeportesEntrenador;
import consola.entrenamiento.MenuEntrenamientoEntrenador;
import consola.tienda.MenuTiendaEmpleado;
import consola.torneos.MenuTorneosJugador;
import consola.personal.MenuTurnosEmpleado;

import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Usuario;

/**
 * Consola de los empleados (entrenadores y fisioterapeutas).
 * Algunos menús solo aplican a entrenadores; cada menú revisa el tipo de empleado.
 */
public class ConsolaEmpleado extends Consola {

	public static void main(String[] args) {
		new ConsolaEmpleado().ejecutar();
	}

	@Override
	protected String getTitulo() {
		return "Consola del empleado";
	}

	@Override
	protected String getNombreRol() {
		return "empleado";
	}

	@Override
	protected boolean rolValido(Usuario usuario) {
		return usuario instanceof Empleado;
	}

	@Override
	protected List<Menu> crearMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		if (usuario instanceof Entrenador) {
			menus.add(new MenuDeportesEntrenador(this));
			menus.add(new MenuEntrenamientoEntrenador(this));
		}
		
		menus.add(new MenuTiendaEmpleado(this));
		menus.add(new MenuTurnosEmpleado(this));
		menus.add(new MenuTorneosJugador(this));
		return menus;
	}
}
