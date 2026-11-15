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
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Usuario;


public class MenuPersonalAdministrador extends Menu {

	private GestorPersonal gestor;

	public MenuPersonalAdministrador(Consola consola) {
		super(consola);
		gestor = new GestorPersonal(club);
	}

	@Override
	public String getTitulo() {
		return "Turnos y sugerencias del personal";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver los turnos publicados"); // 1
		opciones.add("Publicar los turnos de la semana (reemplaza los actuales)"); // 2
		opciones.add("Agregar un turno a la semana publicada"); // 3
		opciones.add("Resolver solicitudes de cambio de turno"); // 4
		opciones.add("Ver todas las sugerencias"); // 5
		opciones.add("Resolver sugerencias pendientes"); // 6
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			consola.mostrarLista("Turnos publicados:", club.getTurnos());
		} else if (opcion == 2) {
			publicarSemana();
		} else if (opcion == 3) {
			agregarTurno();
		} else if (opcion == 4) {
			resolverSolicitud();
		} else if (opcion == 5) {
			consola.mostrarLista("Sugerencias:", club.getSugerencias());
		} else if (opcion == 6) {
			resolverSugerencia();
		}
	}

	// RF33: se arma la semana completa y se publica de una vez, porque la cobertura se revisa sobre todos los turnos
	private void publicarSemana() throws ClubException {
		List<Turno> semana = new ArrayList<Turno>();
		do {
			Turno turno = leerTurno(semana.size() + 1);
			if (turno == null) {
				return;
			}
			semana.add(turno);
			System.out.println("Agregado: " + turno + " (" + semana.size() + " turno(s) en la semana)");
		} while (consola.confirmar("¿Agregar otro turno?"));
		gestor.publicarTurnos(usuario, semana);
		System.out.println("ACEPTADO: se publicaron " + semana.size() + " turnos.");
	}

	// RF33: copia de los turnos publicados más el nuevo; el gestor revisa que no se solape
	private void agregarTurno() throws ClubException {
		List<Turno> semana = new ArrayList<Turno>(club.getTurnos());
		Turno turno = leerTurno(siguienteIdTurno());
		if (turno == null) {
			return;
		}
		semana.add(turno);
		gestor.publicarTurnos(usuario, semana);
		System.out.println("ACEPTADO: se agregó " + turno);
	}

	// RF33
	private void resolverSolicitud() throws ClubException {
		List<SolicitudCambioTurno> pendientes = new ArrayList<SolicitudCambioTurno>();
		for (Turno turno : club.getTurnos()) {
			for (SolicitudCambioTurno solicitud : turno.getSolicitudes()) {
				if (solicitud.estaPendiente()) {
					pendientes.add(solicitud);
				}
			}
		}
		SolicitudCambioTurno solicitud = consola.elegir("Solicitud pendiente", pendientes);
		if (solicitud == null) {
			return;
		}
		boolean aprobar = consola.confirmar("¿Aprobar el cambio? (n para rechazar)");
		gestor.resolverSolicitud(usuario, solicitud, aprobar);
		System.out.println("ACEPTADO: " + solicitud);
	}

	// RF32
	private void resolverSugerencia() throws ClubException {
		List<Sugerencia> pendientes = new ArrayList<Sugerencia>();
		for (Sugerencia sugerencia : club.getSugerencias()) {
			if (sugerencia.estaPendiente()) {
				pendientes.add(sugerencia);
			}
		}
		Sugerencia sugerencia = consola.elegir("Sugerencia pendiente", pendientes);
		if (sugerencia == null) {
			return;
		}
		boolean aprobar = consola.confirmar("¿Aprobar la sugerencia? (n para rechazar)");
		String motivo = null;
		if (!aprobar) {
			motivo = consola.leerTexto("Motivo del rechazo");
		}
		gestor.resolverSugerencia(usuario, sugerencia, aprobar, motivo);
		System.out.println("ACEPTADO: " + sugerencia);
		if (aprobar) {
			System.out.println("Ya puede registrar el producto en el menú de inventario.");
		}
	}

	// Pide empleado, día y horario. Devuelve null si cancela.
	private Turno leerTurno(int id) {
		List<Empleado> empleados = new ArrayList<Empleado>();
		List<String> nombres = new ArrayList<String>();
		for (Usuario candidato : club.getUsuarios()) {
			if (candidato instanceof Empleado) {
				Empleado empleado = (Empleado) candidato;
				empleados.add(empleado);
				String cargo = (empleado instanceof Fisioterapeuta) ? "fisioterapeuta" : "entrenador";
				nombres.add(empleado.getNombreCompleto() + " (" + cargo + ")");
			}
		}
		int opcion = consola.leerOpcion("Empleado", nombres, "Cancelar");
		if (opcion == 0) {
			return null;
		}
		Empleado empleado = empleados.get(opcion - 1);

		List<String> dias = new ArrayList<String>();
		for (DayOfWeek dia : DayOfWeek.values()) {
			dias.add(HorarioAtencion.nombreDia(dia));
		}
		int dia = consola.leerOpcion("Día", dias, "Cancelar");
		if (dia == 0) {
			return null;
		}
		LocalTime inicio = consola.leerHora("Hora de inicio");
		LocalTime fin = consola.leerHora("Hora de fin");
		return new Turno(id, empleado, DayOfWeek.values()[dia - 1], inicio, fin);
	}

	// Los turnos no se borran uno a uno, así que el siguiente id es el mayor + 1
	private int siguienteIdTurno() {
		int mayor = 0;
		for (Turno turno : club.getTurnos()) {
			if (turno.getId() > mayor) {
				mayor = turno.getId();
			}
		}
		return mayor + 1;
	}
}