package consola;

import java.util.ArrayList;
import java.util.List;

import consola.torneos.MenuTorneosAdministrador;

import modelo.usuarios.Administrador;
import modelo.usuarios.Usuario;

/**
 * Consola del administrador.
 */
public class ConsolaAdministrador extends Consola {

	public static void main(String[] args) {
		new ConsolaAdministrador().ejecutar();
	}

	@Override
	protected String getTitulo() {
		return "Consola del administrador";
	}

	@Override
	protected String getNombreRol() {
		return "administrador";
	}

	@Override
	protected boolean rolValido(Usuario usuario) {
		return usuario instanceof Administrador;
	}

	@Override
	protected List<Menu> crearMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		// Área A: menus.add(new MenuDeportesAdministrador(this)); menus.add(new MenuCompetenciasAdministrador(this));
		// Área B: menus.add(new MenuAcademiasAdministrador(this));
		// Área C: menus.add(new MenuTiendaAdministrador(this)); menus.add(new MenuPersonalAdministrador(this));
		menus.add(new MenuTorneosAdministrador(this));
		return menus;
	}
}
