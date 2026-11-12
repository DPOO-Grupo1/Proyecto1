package consola;

import java.util.ArrayList;
import java.util.List;

import consola.deportes.MenuDeportesSocio;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Consola del socio. Carga los datos, pide el login y muestra los menús de cada área.
 */
public class ConsolaSocio extends Consola {

	public static void main(String[] args) {
		new ConsolaSocio().ejecutar();
	}

	@Override
	protected String getTitulo() {
		return "Consola del socio";
	}

	@Override
	protected String getNombreRol() {
		return "socio";
	}

	@Override
	protected boolean rolValido(Usuario usuario) {
		return usuario instanceof Socio;
	}

	@Override
	protected List<Menu> crearMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(new MenuDeportesSocio(this));
		// Área B: menus.add(new MenuAcademiasSocio(this));
		// Área C: menus.add(new MenuTiendaSocio(this));
		return menus;
	}
}
