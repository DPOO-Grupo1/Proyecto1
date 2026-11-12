package consola;

import java.util.ArrayList;
import java.util.List;

import modelo.usuarios.Empleado;
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
		// Área A: menus.add(new MenuDeportesEntrenador(this));
		// Área B: menus.add(new MenuEntrenamientoEntrenador(this));
		// Área C: menus.add(new MenuTiendaEmpleado(this)); menus.add(new MenuTurnosEmpleado(this));
		return menus;
	}
}
