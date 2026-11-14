package consola.deportes;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorDeportes;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Usuario;

/**
 * Equipos y jugadores para el administrador: crear equipos, asignar entrenador,
 * inscribir y retirar jugadores y dar de baja equipos (RF22, RF23 y RF24).
 */
public class MenuEquiposAdministrador extends Menu {

	private GestorDeportes gestor;

	public MenuEquiposAdministrador(Consola consola) {
		super(consola);
		gestor = new GestorDeportes(club);
	}

	@Override
	public String getTitulo() {
		return "Equipos y jugadores";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver los equipos y sus jugadores");
		opciones.add("Crear un equipo");
		opciones.add("Asignar el entrenador de un equipo");
		opciones.add("Inscribir a un socio en un equipo");
		opciones.add("Retirar a un jugador de un equipo");
		opciones.add("Dar de baja un equipo");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verEquipos();
		} else if (opcion == 2) {
			crearEquipo();
		} else if (opcion == 3) {
			asignarEntrenador();
		} else if (opcion == 4) {
			inscribirJugador();
		} else if (opcion == 5) {
			retirarJugador();
		} else if (opcion == 6) {
			darDeBaja();
		}
	}

	private void verEquipos() {
		Equipo equipo = consola.elegir("Equipo", club.getEquipos());
		if (equipo == null) {
			return;
		}
		mostrarEquipo(equipo);
	}

	/**
	 * Lo usa también el menú del entrenador.
	 */
	static void mostrarEquipo(Equipo equipo) {
		Entrenador entrenador = equipo.getEntrenador();
		System.out.println(equipo + (equipo.estaActivo() ? "" : ", dado de baja") + ". Entrenador: "
				+ (entrenador == null ? "sin asignar" : entrenador.getNombreCompleto()) + ". Jugadores activos: "
				+ equipo.getJugadoresActivos().size() + " de " + equipo.getCupoMaximo());
		if (equipo.getJugadoresActivos().isEmpty()) {
			System.out.println("   (sin información disponible)");
		}
		for (JugadorEquipo jugador : equipo.getJugadoresActivos()) {
			Suspension suspension = jugador.getSuspensionPendiente();
			System.out.println("   " + jugador + (suspension == null ? "" : ", suspendido: " + suspension));
		}
	}

	// RF22
	private void crearEquipo() throws ClubException {
		DeporteEquipo deporte = consola.elegir("Deporte", club.getDeportes());
		if (deporte == null) {
			return;
		}
		Categoria categoria = consola.elegir("Categoría", club.getCategorias());
		if (categoria == null) {
			return;
		}
		String nombre = consola.leerTexto("Nombre del equipo");
		int cupo = consola.leerEntero("Cupo máximo de jugadores", 1, 50);
		Equipo equipo = gestor.crearEquipo(usuario, nombre, deporte, categoria, cupo);
		System.out.println("ACEPTADO: se creó " + equipo);
	}

	// RF22
	private void asignarEntrenador() throws ClubException {
		Equipo equipo = consola.elegir("Equipo", equiposActivos());
		if (equipo == null) {
			return;
		}
		List<Entrenador> entrenadores = new ArrayList<Entrenador>();
		for (Usuario candidato : club.getUsuarios()) {
			if (candidato instanceof Entrenador) {
				entrenadores.add((Entrenador) candidato);
			}
		}
		List<String> nombres = new ArrayList<String>();
		for (Entrenador entrenador : entrenadores) {
			nombres.add(entrenador.getNombreCompleto() + " (id " + entrenador.getIdEmpleado() + ")");
		}
		int opcion = consola.leerOpcion("Entrenador", nombres, "Cancelar");
		if (opcion == 0) {
			return;
		}
		Entrenador entrenador = entrenadores.get(opcion - 1);
		gestor.asignarEntrenador(usuario, equipo.getNombre(), entrenador.getIdEmpleado());
		System.out.println("ACEPTADO: " + entrenador.getNombreCompleto() + " entrena a " + equipo.getNombre());
	}

	// RF23
	private void inscribirJugador() throws ClubException {
		Equipo equipo = consola.elegir("Equipo", equiposActivos());
		if (equipo == null) {
			return;
		}
		String socioID = consola.leerTexto("Id del socio (por ejemplo S1)");
		int numero = consola.leerEntero("Número de camiseta", 1, 99);
		String posicion = consola.leerTexto("Posición");
		JugadorEquipo jugador = gestor.inscribirJugador(usuario, socioID, equipo.getNombre(), numero, posicion);
		System.out.println("ACEPTADO: " + jugador);
	}

	// RF24
	private void retirarJugador() throws ClubException {
		Equipo equipo = consola.elegir("Equipo", equiposActivos());
		if (equipo == null) {
			return;
		}
		JugadorEquipo jugador = consola.elegir("Jugador", equipo.getJugadoresActivos());
		if (jugador == null || !consola.confirmar("¿Retirar a " + jugador + "?")) {
			return;
		}
		gestor.retirarJugador(usuario, equipo.getNombre(), jugador.getNumeroCamiseta());
		System.out.println("ACEPTADO: el número " + jugador.getNumeroCamiseta() + " quedó libre.");
	}

	// RF24
	private void darDeBaja() throws ClubException {
		Equipo equipo = consola.elegir("Equipo", equiposActivos());
		if (equipo == null || !consola.confirmar("Sus jugadores quedarán inactivos. ¿Dar de baja " + equipo.getNombre() + "?")) {
			return;
		}
		gestor.darDeBajaEquipo(usuario, equipo.getNombre());
		System.out.println("ACEPTADO: " + equipo.getNombre() + " quedó dado de baja.");
	}

	private List<Equipo> equiposActivos() {
		List<Equipo> activos = new ArrayList<Equipo>();
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.estaActivo()) {
				activos.add(equipo);
			}
		}
		return activos;
	}
}
