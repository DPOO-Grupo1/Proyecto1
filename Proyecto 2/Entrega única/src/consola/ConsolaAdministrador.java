package consola;

import java.util.ArrayList;
import java.util.List;

import consola.academias.MenuAcademiasAdministrador;
import consola.deportes.MenuCompetenciasAdministrador;
import consola.deportes.MenuEquiposAdministrador;
import consola.torneos.MenuTorneosAdministrador;
import consola.tienda.MenuTiendaAdministrador;
import consola.personal.MenuPersonalAdministrador;
import consola.reportes.MenuReportesAdministrador;

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
		menus.add(new MenuEquiposAdministrador(this));
		menus.add(new MenuCompetenciasAdministrador(this));
		menus.add(new MenuAcademiasAdministrador(this));
		
		menus.add(new MenuTiendaAdministrador(this));
		menus.add(new MenuPersonalAdministrador(this));
		menus.add(new MenuReportesAdministrador(this));
		menus.add(new MenuTorneosAdministrador(this));
		return menus;
	}
}
