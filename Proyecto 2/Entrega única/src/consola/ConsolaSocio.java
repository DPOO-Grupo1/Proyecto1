package consola;

import java.util.ArrayList;
import java.util.List;

import consola.academias.MenuAcademiasSocio;
import consola.deportes.MenuDeportesSocio;
import consola.tienda.MenuTiendaSocio;
import consola.torneos.MenuTorneosJugador;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Consola del socio. Carga los datos, pide el login y muestra los menús del socio.
 * También la usan los estudiantes y externos registrados para los torneos, que solo ven ese menú.
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
		return "socio ni participante de torneos";
	}

	@Override
	protected boolean rolValido(Usuario usuario) {
		return usuario instanceof Socio || usuario instanceof ParticipanteExterno;
	}

	@Override
	protected List<Menu> crearMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		if (usuario instanceof Socio) {
			menus.add(new MenuDeportesSocio(this));
			menus.add(new MenuAcademiasSocio(this));
			menus.add(new MenuTiendaSocio(this));
		}
		menus.add(new MenuTorneosJugador(this));
		return menus;
	}
}
