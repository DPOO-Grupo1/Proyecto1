package consola.academias;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorAcademias;
import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.Nivel;
import modelo.academias.Reserva;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Menú de academias para el administrador: inscribir socios (RF6) y consultar
 * reservas e instalaciones (RF36).
 */
public class MenuAcademiasAdministrador extends Menu {

	private GestorAcademias gestor;

	public MenuAcademiasAdministrador(Consola consola) {
		super(consola);
		this.gestor = new GestorAcademias(club);
	}

	@Override
	public String getTitulo() {
		return "Academias y reservas";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Inscribir a un socio en una academia");
		opciones.add("Consultar reservas de una fecha");
		opciones.add("Consultar instalaciones");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			inscribirSocio();
		} else if (opcion == 2) {
			consultarReservas();
		} else if (opcion == 3) {
			consultarInstalaciones();
		}
	}

	private void inscribirSocio() throws ClubException {
		List<Socio> socios = new ArrayList<Socio>();
		for (Usuario u : club.getUsuarios()) {
			if (u instanceof Socio) {
				socios.add((Socio) u);
			}
		}
		Socio socio = consola.elegir("Socio", socios);
		if (socio == null) {
			return;
		}
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		Nivel nivel = consola.elegir("Nivel", Arrays.asList(Nivel.values()));
		if (nivel == null) {
			return;
		}
		Inscripcion ins = gestor.inscribirEnAcademia(usuario, socio.getSocioID(),
				disciplina.getNombre(), nivel.name());
		System.out.println("ACEPTADO: " + socio.getNombreCompleto() + " inscrito en " + ins);
	}

	private void consultarReservas() throws ClubException {
		LocalDate fecha = consola.confirmar("¿Filtrar por fecha?") ? consola.leerFecha("Fecha") : null;
		List<Reserva> reservas = gestor.consultarReservas(usuario, fecha);
		consola.mostrarLista("Reservas:", reservas);
	}

	private void consultarInstalaciones() {
		String filtro = null;
		if (consola.confirmar("¿Filtrar por disciplina?")) {
			Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
			if (disciplina != null) {
				filtro = disciplina.getNombre();
			}
		}
		List<Instalacion> instalaciones = gestor.consultarInstalaciones(usuario, filtro);
		consola.mostrarLista("Instalaciones:", instalaciones);
	}
}

