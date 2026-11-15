package consola.personal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorPersonal;
import modelo.personal.HorarioAtencion;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;
import modelo.usuarios.Empleado;


public class MenuTurnosEmpleado extends Menu {

	private GestorPersonal gestor;

	public MenuTurnosEmpleado(Consola consola) {
		super(consola);
		gestor = new GestorPersonal(club);
	}

	@Override
	public String getTitulo() {
		return "Turnos y sugerencias";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver mis turnos"); // 1
		opciones.add("Solicitar un cambio de turno"); // 2
		opciones.add("Ver mis solicitudes de cambio"); // 3
		opciones.add("Sugerir un producto"); // 4
		opciones.add("Ver mis sugerencias"); // 5
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			consola.mostrarLista("Mis turnos:", ((Empleado) usuario).getTurnos());
		} else if (opcion == 2) {
			solicitarCambio();
		} else if (opcion == 3) {
			consola.mostrarLista("Mis solicitudes:", gestor.consultarSolicitudes(usuario));
		} else if (opcion == 4) {
			sugerirProducto();
		} else if (opcion == 5) {
			consola.mostrarLista("Mis sugerencias:", ((Empleado) usuario).getSugerencias());
		}
	}

	// RF18, RF19
	private void solicitarCambio() throws ClubException {
		Turno turno = consola.elegir("Turno a cambiar", ((Empleado) usuario).getTurnos());
		if (turno == null) {
			return;
		}
		DayOfWeek dia = elegirDia();
		if (dia == null) {
			return;
		}
		LocalTime inicio = consola.leerHora("Nueva hora de inicio");
		LocalTime fin = consola.leerHora("Nueva hora de fin");
		SolicitudCambioTurno solicitud = gestor.solicitarCambio(usuario, turno, dia, inicio, fin);
		System.out.println("ACEPTADO: " + solicitud + ". Queda pendiente de aprobación del administrador.");
	}

	// RF21
	private void sugerirProducto() throws ClubException {
		String nombre = consola.leerTexto("Nombre del producto");
		List<String> tipos = new ArrayList<String>();
		tipos.add("Artículo");
		tipos.add("Bebida");
		tipos.add("Snack");
		int opcion = consola.leerOpcion("Tipo de producto", tipos, "Cancelar");
		if (opcion == 0) {
			return;
		}
		String tipo = tipos.get(opcion - 1);
		String descripcion = consola.leerTexto("Descripción");
		Sugerencia sugerencia = gestor.sugerirProducto(usuario, nombre, tipo, descripcion);
		System.out.println("ACEPTADO: " + sugerencia);
	}

	private DayOfWeek elegirDia() {
		List<String> nombres = new ArrayList<String>();
		for (DayOfWeek dia : DayOfWeek.values()) {
			nombres.add(HorarioAtencion.nombreDia(dia));
		}
		int opcion = consola.leerOpcion("Nuevo día", nombres, "Cancelar");
		if (opcion == 0) {
			return null;
		}
		return DayOfWeek.values()[opcion - 1];
	}
}