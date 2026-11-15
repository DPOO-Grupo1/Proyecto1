package consola.academias;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorAcademias;
import logica.GestorEntrenamiento;
import modelo.academias.Disciplina;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisMesa;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.entrenamiento.Convocatoria;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Menú de academias y prácticas para el socio (RF6 a RF11 y consulta de convocatorias RF16).
 */
public class MenuAcademiasSocio extends Menu {

	private GestorAcademias gestor;
	private GestorEntrenamiento gestorEntrenamiento;

	public MenuAcademiasSocio(Consola consola) {
		super(consola);
		this.gestor = new GestorAcademias(club);
		this.gestorEntrenamiento = new GestorEntrenamiento(club);
	}

	@Override
	public String getTitulo() {
		return "Academias y prácticas";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Inscribirse en una academia");
		opciones.add("Reservar una instalación");
		opciones.add("Registrar ingreso a una reserva");
		opciones.add("Cancelar una reserva");
		opciones.add("Programar un partido de práctica");
		opciones.add("Registrar resultado de un partido de práctica");
		opciones.add("Ver mi perfil e inscripciones");
		opciones.add("Ver mi historial de partidos");
		opciones.add("Ver el ranking de una disciplina");
		opciones.add("Ver mis convocatorias de entrenamiento");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			inscribirse();
		} else if (opcion == 2) {
			reservar();
		} else if (opcion == 3) {
			registrarIngreso();
		} else if (opcion == 4) {
			cancelarReserva();
		} else if (opcion == 5) {
			programarPractica();
		} else if (opcion == 6) {
			registrarResultado();
		} else if (opcion == 7) {
			verPerfil();
		} else if (opcion == 8) {
			verHistorial();
		} else if (opcion == 9) {
			verRanking();
		} else if (opcion == 10) {
			verConvocatorias();
		}
	}

	private Socio socioActual() {
		return (Socio) usuario;
	}

	private void inscribirse() throws ClubException {
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		Nivel nivel = consola.elegir("Nivel", Arrays.asList(Nivel.values()));
		if (nivel == null) {
			return;
		}
		Inscripcion ins = gestor.inscribirEnAcademia(usuario, socioActual().getSocioID(),
				disciplina.getNombre(), nivel.name());
		System.out.println("ACEPTADO: " + ins);
	}

	private void reservar() throws ClubException {
		Instalacion instalacion = consola.elegir("Instalación", club.getInstalaciones());
		if (instalacion == null) {
			return;
		}
		LocalDate fecha = consola.leerFecha("Fecha");
		LocalTime inicio = consola.leerHora("Hora de inicio");
		LocalTime fin = consola.leerHora("Hora de fin");
		Modalidad modalidad = consola.elegir("Modalidad", Arrays.asList(Modalidad.values()));
		if (modalidad == null) {
			return;
		}
		int numJugadores = consola.leerEntero("Número de jugadores", 1, 10);
		Reserva reserva = gestor.reservar(usuario, instalacion.getId(), fecha, inicio, fin, numJugadores, modalidad);
		System.out.println("ACEPTADO: reserva " + reserva.getId() + " creada.");
	}

	private void registrarIngreso() throws ClubException {
		Reserva reserva = consola.elegir("Reserva", socioActual().getReservas());
		if (reserva == null) {
			return;
		}
		boolean bebida = consola.confirmar("¿Lleva bebida caliente?");
		gestor.registrarIngreso(usuario, reserva.getId(), bebida);
		System.out.println("ACEPTADO: ingreso registrado en " + reserva.getInstalacion().getNombre());
	}

	private void cancelarReserva() throws ClubException {
		Reserva reserva = consola.elegir("Reserva a cancelar", socioActual().getReservas());
		if (reserva == null) {
			return;
		}
		LocalTime horaActual = consola.leerHora("Hora actual");
		gestor.cancelarReserva(usuario, reserva.getId(), horaActual);
		System.out.println("ACEPTADO: reserva " + reserva.getId() + " cancelada.");
	}

	private void programarPractica() throws ClubException {
		Reserva reserva = consola.elegir("Reserva para el partido", socioActual().getReservas());
		if (reserva == null) {
			return;
		}
		List<Socio> socios = obtenerSocios();
		List<String> ids = new ArrayList<String>();
		for (int i = 1; i <= reserva.getNumJugadores(); i++) {
			Socio elegido = consola.elegir("Participante " + i, socios);
			if (elegido == null) {
				return;
			}
			ids.add(elegido.getSocioID());
		}
		PartidoPractica partido = gestor.programarPartidoPractica(usuario, reserva.getId(), ids);
		System.out.println("ACEPTADO: " + partido);
	}

	private void registrarResultado() throws ClubException {
		List<PartidoPractica> misPartidos = new ArrayList<PartidoPractica>();
		for (PartidoPractica p : club.getPartidosPractica()) {
			if (p.tieneParticipante(socioActual())) {
				misPartidos.add(p);
			}
		}
		PartidoPractica partido = consola.elegir("Partido de práctica", misPartidos);
		if (partido == null) {
			return;
		}
		Disciplina disc = partido.getReserva().getInstalacion().getDisciplina();
		boolean esMesa = disc != null && disc.getNombre().equalsIgnoreCase("Tenis de mesa");
		Map<Socio, EstadisticaPractica> stats = new HashMap<Socio, EstadisticaPractica>();
		for (Socio p : partido.getParticipantes()) {
			System.out.println("Estadísticas de " + p.getNombreCompleto() + ":");
			int sets = consola.leerEntero("  Sets ganados", 0, 5);
			if (esMesa) {
				int puntos = consola.leerEntero("  Puntos totales", 0, 500);
				stats.put(p, new EstadisticaTenisMesa(sets, puntos));
			} else {
				int games = consola.leerEntero("  Games ganados", 0, 100);
				int aces = consola.leerEntero("  Aces", 0, 100);
				int errores = consola.leerEntero("  Errores no forzados", 0, 200);
				stats.put(p, new EstadisticaTenisPadel(sets, games, aces, errores));
			}
		}
		gestor.registrarResultadoPractica(usuario, partido.getId(), stats);
		System.out.println("ACEPTADO: resultado registrado. Ganador: " + partido.getGanador().getNombreCompleto());
	}

	private void verPerfil() throws ClubException {
		List<Inscripcion> perfil = gestor.consultarPerfil(usuario, socioActual().getSocioID());
		consola.mostrarLista("Mis inscripciones:", perfil);
	}

	private void verHistorial() throws ClubException {
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		LocalDate desde = consola.confirmar("¿Filtrar desde fecha?") ? consola.leerFecha("Desde") : null;
		LocalDate hasta = consola.confirmar("¿Filtrar hasta fecha?") ? consola.leerFecha("Hasta") : null;
		List<PartidoPractica> historial = gestor.consultarHistorial(usuario, socioActual().getSocioID(),
				disciplina.getNombre(), desde, hasta);
		consola.mostrarLista("Historial en " + disciplina.getNombre() + ":", historial);
	}

	private void verRanking() throws ClubException {
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		List<Inscripcion> ranking = gestor.consultarRanking(usuario, disciplina.getNombre());
		List<String> lineas = new ArrayList<String>();
		for (int i = 0; i < ranking.size(); i++) {
			Inscripcion ins = ranking.get(i);
			lineas.add((i + 1) + ". " + ins.getSocio().getNombreCompleto() + " - ELO " + ins.getPuntajeELO()
					+ " (" + ins.getNivel() + ")");
		}
		consola.mostrarLista("Ranking de " + disciplina.getNombre() + ":", lineas);
	}

	private void verConvocatorias() throws ClubException {
		List<Convocatoria> convocatorias = gestorEntrenamiento.consultarConvocatoriasSocio(usuario,
				socioActual().getSocioID());
		List<String> lineas = new ArrayList<String>();
		for (Convocatoria c : convocatorias) {
			lineas.add(c.getSesion().getId() + " (" + c.getSesion().getFecha() + " " + c.getSesion().getHora()
					+ ") - " + c);
		}
		consola.mostrarLista("Mis convocatorias de entrenamiento:", lineas);
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

