package consola.torneos;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import logica.GestorTorneos;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.FilaTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Torneo;

/**
 * Lo que comparten los menús de torneos de los jugadores y del administrador:
 * consultar los torneos por estado y ver el historial detallado de uno (HU_T9).
 */
public abstract class MenuTorneos extends Menu {

	protected GestorTorneos gestor;

	public MenuTorneos(Consola consola) {
		super(consola);
		gestor = new GestorTorneos(club);
	}

	@Override
	public String getTitulo() {
		return "Torneos internos";
	}

	// HU_T9
	protected void verTorneos() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Todos");
		for (EstadoTorneo estado : EstadoTorneo.values()) {
			opciones.add(estado.toString());
		}
		int opcion = consola.leerOpcion("Estado", opciones, "Cancelar");
		if (opcion == 0) {
			return;
		}
		EstadoTorneo estado = opcion == 1 ? null : EstadoTorneo.values()[opcion - 2];
		consola.mostrarLista("Torneos:", gestor.consultarTorneos(usuario, estado));
	}

	// HU_T9
	protected void verHistorial() {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneos(usuario, null));
		if (torneo == null) {
			return;
		}
		System.out.println(torneo);
		System.out.println("Cupos: " + torneo.getCupos() + " (" + torneo.getCuposReservados()
				+ " preferenciales). Libres: " + torneo.getCuposRegularesLibres() + " regulares y "
				+ torneo.getCuposPreferencialesLibres() + " preferenciales"
				+ (torneo.isPreferencialesLiberados() ? " (ya se liberaron)" : ""));
		System.out.println("Premio: " + torneo.getTipo().describirPremio(torneo));

		System.out.println("Participantes:");
		if (torneo.getInscripciones().isEmpty()) {
			System.out.println("   (sin información disponible)");
		}
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			System.out.println("   " + inscripcion + ", inscrito el " + inscripcion.getFecha() + ", pagó $"
					+ (long) inscripcion.getValorPagado());
		}

		consola.mostrarLista("Encuentros:", torneo.getEncuentros());

		System.out.println("Posiciones:");
		int posicion = 1;
		for (FilaTorneo fila : gestor.consultarPosiciones(usuario, torneo)) {
			System.out.println("   " + posicion + ". " + fila);
			posicion++;
		}
		if (torneo.getInscripciones().isEmpty()) {
			System.out.println("   (sin información disponible)");
		}
	}

	/**
	 * Para los encuentros nuevos que salen al registrar un resultado o al iniciar.
	 */
	protected void mostrarEncuentros(String titulo, List<EncuentroTorneo> encuentros) {
		if (!encuentros.isEmpty()) {
			consola.mostrarLista(titulo, encuentros);
		}
	}
}
