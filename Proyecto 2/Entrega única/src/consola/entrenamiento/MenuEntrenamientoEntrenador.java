package consola.entrenamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorEntrenamiento;
import modelo.academias.Disciplina;
import modelo.academias.Instalacion;
import modelo.deportes.Equipo;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Menú de entrenamiento para el entrenador (RF15 y RF16).
 */
public class MenuEntrenamientoEntrenador extends Menu {

	private GestorEntrenamiento gestor;

	public MenuEntrenamientoEntrenador(Consola consola) {
		super(consola);
		this.gestor = new GestorEntrenamiento(club);
	}

	@Override
	public String getTitulo() {
		return "Entrenamientos";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Planear sesión individual o grupal");
		opciones.add("Planear sesión de equipo");
		opciones.add("Finalizar una sesión");
		opciones.add("Registrar asistencia de un convocado");
		opciones.add("Registrar evaluación de un convocado");
		opciones.add("Ver sesiones de una fecha");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			planearSesionAcademia();
		} else if (opcion == 2) {
			planearSesionEquipo();
		} else if (opcion == 3) {
			finalizarSesion();
		} else if (opcion == 4) {
			registrarAsistencia();
		} else if (opcion == 5) {
			registrarEvaluacion();
		} else if (opcion == 6) {
			verSesiones();
		}
	}

	private void planearSesionAcademia() throws ClubException {
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		Instalacion instalacion = consola.elegir("Instalación", club.getInstalaciones());
		if (instalacion == null) {
			return;
		}
		LocalDate fecha = consola.leerFecha("Fecha");
		LocalTime hora = consola.leerHora("Hora de inicio");
		int duracion = consola.leerEntero("Duración en minutos", 1, 300);

		List<Socio> socios = obtenerSocios();
		List<String> ids = new ArrayList<String>();
		do {
			Socio socio = consola.elegir("Socio a convocar", socios);
			if (socio == null) {
				break;
			}
			ids.add(socio.getSocioID());
		} while (consola.confirmar("¿Convocar a otro socio?"));

		if (ids.isEmpty()) {
			return;
		}
		SesionEntrenamiento sesion = gestor.planearSesion(usuario, disciplina.getNombre(),
				instalacion.getId(), fecha, hora, duracion, ids);
		System.out.println("ACEPTADO: sesión " + sesion.getId() + " planeada.");
	}

	private void planearSesionEquipo() throws ClubException {
		Equipo equipo = consola.elegir("Equipo", club.getEquipos());
		if (equipo == null) {
			return;
		}
		Instalacion instalacion = consola.elegir("Escenario de conjunto", club.getInstalaciones());
		if (instalacion == null) {
			return;
		}
		LocalDate fecha = consola.leerFecha("Fecha");
		LocalTime hora = consola.leerHora("Hora de inicio");
		int duracion = consola.leerEntero("Duración en minutos", 1, 300);

		SesionEntrenamiento sesion = gestor.planearSesionEquipo(usuario, equipo.getNombre(),
				instalacion.getId(), fecha, hora, duracion);
		System.out.println("ACEPTADO: sesión de equipo " + sesion.getId() + " planeada con "
				+ sesion.getConvocatorias().size() + " convocados.");
	}

	private void finalizarSesion() throws ClubException {
		SesionEntrenamiento sesion = consola.elegir("Sesión a finalizar", club.getSesiones());
		if (sesion == null) {
			return;
		}
		gestor.finalizarSesion(usuario, sesion.getId());
		System.out.println("ACEPTADO: sesión " + sesion.getId() + " finalizada.");
	}

	private void registrarAsistencia() throws ClubException {
		SesionEntrenamiento sesion = consola.elegir("Sesión", club.getSesiones());
		if (sesion == null) {
			return;
		}
		Convocatoria conv = consola.elegir("Convocado", sesion.getConvocatorias());
		if (conv == null) {
			return;
		}
		EstadoAsistencia estado = consola.elegir("Estado de asistencia", Arrays.asList(EstadoAsistencia.values()));
		if (estado == null) {
			return;
		}
		gestor.registrarAsistencia(usuario, sesion.getId(), conv.getSocio().getSocioID(), estado.toString());
		System.out.println("ACEPTADO: asistencia registrada para " + conv.getSocio().getNombreCompleto());
	}

	private void registrarEvaluacion() throws ClubException {
		SesionEntrenamiento sesion = consola.elegir("Sesión", club.getSesiones());
		if (sesion == null) {
			return;
		}
		Convocatoria conv = consola.elegir("Convocado", sesion.getConvocatorias());
		if (conv == null) {
			return;
		}
		int nota = consola.leerEntero("Nota", 1, 5);
		String comentario = consola.leerTextoOpcional("Comentario");
		gestor.registrarEvaluacion(usuario, sesion.getId(), conv.getSocio().getSocioID(), nota, comentario);
		System.out.println("ACEPTADO: evaluación registrada para " + conv.getSocio().getNombreCompleto());
	}

	private void verSesiones() {
		LocalDate fecha = consola.confirmar("¿Filtrar por fecha?") ? consola.leerFecha("Fecha") : null;
		List<SesionEntrenamiento> sesiones = gestor.consultarSesiones(usuario, fecha);
		consola.mostrarLista("Sesiones de entrenamiento:", sesiones);
	}

	private List<Socio> obtenerSocios() {
		List<Socio> socios = new ArrayList<Socio>();
		for (Usuario u : club.getUsuarios()) {
			if (u instanceof Socio) {
				socios.add((Socio) u);
			}
		}
		return socios;
	}
}

