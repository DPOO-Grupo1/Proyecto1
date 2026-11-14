package consola.torneos;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import excepciones.ClubException;
import modelo.deportes.Equipo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.InscripcionEquipo;
import modelo.torneos.InscripcionIndividual;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Usuario;

/**
 * Torneos para quien juega: socios, empleados y participantes externos.
 * Los entrenadores además ven las opciones para inscribir y retirar sus equipos.
 */
public class MenuTorneosJugador extends MenuTorneos {

	public MenuTorneosJugador(Consola consola) {
		super(consola);
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver torneos por estado");
		opciones.add("Ver el historial de un torneo");
		opciones.add("Inscribirme en un torneo individual");
		opciones.add("Salirme de un torneo");
		opciones.add("Ver mis torneos");
		opciones.add("Ver mis bonos de descuento");
		if (usuario instanceof Entrenador) {
			opciones.add("Inscribir uno de mis equipos");
			opciones.add("Retirar uno de mis equipos");
		}
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verTorneos();
		} else if (opcion == 2) {
			verHistorial();
		} else if (opcion == 3) {
			inscribirme();
		} else if (opcion == 4) {
			salirme();
		} else if (opcion == 5) {
			consola.mostrarLista("Mis torneos:", gestor.consultarMisTorneos(usuario));
		} else if (opcion == 6) {
			consola.mostrarLista("Mis bonos:", gestor.consultarBonos(usuario));
		} else if (opcion == 7) {
			inscribirEquipo();
		} else if (opcion == 8) {
			retirarEquipo();
		}
	}

	// HU_T3
	private void inscribirme() throws ClubException {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneosAbiertosIndividuales(usuario));
		if (torneo == null) {
			return;
		}
		Usuario pareja = null;
		if (((TorneoIndividual) torneo).esDobles()) {
			pareja = club.buscarUsuario(consola.leerTexto("Login de su pareja"));
		}
		InscripcionIndividual inscripcion = gestor.inscribirIndividual(usuario, torneo, pareja);
		System.out.println("ACEPTADO: quedó inscrito en " + torneo.getNombre() + " con un cupo "
				+ (inscripcion.isPreferencial() ? "preferencial" : "regular") + ". Valor a pagar: $"
				+ (long) inscripcion.getValorPagado());
	}

	// HU_T5
	private void salirme() throws ClubException {
		List<Torneo> abiertos = new ArrayList<Torneo>();
		for (Torneo torneo : gestor.consultarMisTorneos(usuario)) {
			if (torneo.estaAbierto() && torneo.buscarInscripcionDe(usuario) != null) {
				abiertos.add(torneo);
			}
		}
		Torneo torneo = consola.elegir("Torneo del que se quiere salir", abiertos);
		if (torneo == null) {
			return;
		}
		InscripcionTorneo inscripcion = torneo.buscarInscripcionDe(usuario);
		if (!consola.confirmar("Se va a retirar la inscripción de " + inscripcion.getNombre() + ". ¿Continuar?")) {
			return;
		}
		gestor.retirarInscripcion(usuario, torneo, inscripcion);
		System.out.println("ACEPTADO: el cupo quedó libre.");
	}

	// HU_T4
	private void inscribirEquipo() throws ClubException {
		List<Torneo> deConjunto = new ArrayList<Torneo>();
		for (Torneo torneo : gestor.consultarTorneos(usuario, EstadoTorneo.INSCRIPCIONES_ABIERTAS)) {
			if (torneo instanceof TorneoConjunto) {
				deConjunto.add(torneo);
			}
		}
		Torneo torneo = consola.elegir("Torneo", deConjunto);
		if (torneo == null) {
			return;
		}
		Equipo equipo = consola.elegir("Equipo", misEquipos());
		if (equipo == null) {
			return;
		}
		InscripcionEquipo inscripcion = gestor.inscribirEquipo(usuario, torneo, equipo);
		System.out.println("ACEPTADO: " + equipo.getNombre() + " quedó inscrito con un cupo "
				+ (inscripcion.isPreferencial() ? "preferencial" : "regular") + ". Valor a pagar: $"
				+ (long) inscripcion.getValorPagado());
	}

	// HU_T5
	private void retirarEquipo() throws ClubException {
		List<InscripcionTorneo> inscripciones = new ArrayList<InscripcionTorneo>();
		List<Torneo> torneos = new ArrayList<Torneo>();
		for (Torneo torneo : gestor.consultarMisTorneos(usuario)) {
			for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
				if (torneo.estaAbierto() && inscripcion instanceof InscripcionEquipo
						&& ((InscripcionEquipo) inscripcion).getEquipo().getEntrenador() == usuario) {
					inscripciones.add(inscripcion);
					torneos.add(torneo);
				}
			}
		}
		List<String> textos = new ArrayList<String>();
		for (int i = 0; i < inscripciones.size(); i++) {
			textos.add(inscripciones.get(i).getNombre() + " en " + torneos.get(i).getNombre());
		}
		if (textos.isEmpty()) {
			System.out.println("Ninguno de sus equipos está inscrito en un torneo con inscripciones abiertas.");
			return;
		}
		int opcion = consola.leerOpcion("Inscripción a retirar", textos, "Cancelar");
		if (opcion == 0) {
			return;
		}
		gestor.retirarInscripcion(usuario, torneos.get(opcion - 1), inscripciones.get(opcion - 1));
		System.out.println("ACEPTADO: el cupo quedó libre.");
	}

	private List<Equipo> misEquipos() {
		List<Equipo> equipos = new ArrayList<Equipo>();
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.getEntrenador() == usuario && equipo.estaActivo()) {
				equipos.add(equipo);
			}
		}
		return equipos;
	}
}
