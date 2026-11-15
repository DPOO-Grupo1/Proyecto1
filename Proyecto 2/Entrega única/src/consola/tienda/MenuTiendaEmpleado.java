package consola.tienda;

import consola.Consola;

/**
 * Compras del empleado. Son iguales a las del socio; el gestor aplica el 15 %
 * si usa su propio código y el 8 % si usa el de otro empleado (RF17, RF20).
 */
public class MenuTiendaEmpleado extends MenuTiendaSocio {

	public MenuTiendaEmpleado(Consola consola) {
		super(consola);
	}
}
