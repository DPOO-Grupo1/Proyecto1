package consola.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import excepciones.ClubException;
import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.torneos.BonoDescuento;
import modelo.torneos.Competitivo;
import modelo.torneos.EliminacionDirecta;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.FormatoTorneo;
import modelo.torneos.InscripcionEquipo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Recreativo;
import modelo.torneos.TipoTorneo;
import modelo.torneos.TodosContraTodos;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.usuarios.ParticipanteExterno;

/**
 * Torneos para el administrador: crearlos, manejar inscripciones y cupos, iniciarlos
 * y registrar los resultados.
 */
public class MenuTorneosAdministrador extends MenuTorneos {

	public MenuTorneosAdministrador(Consola consola) {
		super(consola);
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver torneos por estado");
		opciones.add("Ver el historial de un torneo");
		opciones.add("Crear un torneo de deporte de conjunto");
		opciones.add("Crear un torneo de disciplina individual");
		opciones.add("Registrar un estudiante o participante externo");
		opciones.add("Inscribir un equipo");
		opciones.add("Retirar una inscripción");
		opciones.add("Liberar los cupos preferenciales de un torneo");
		opciones.add("Iniciar un torneo");
		opciones.add("Registrar el resultado de un encuentro");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verTorneos();
		} else if (opcion == 2) {
			verHistorial();
		} else if (opcion == 3) {
			crearConjunto();
		} else if (opcion == 4) {
			crearIndividual();
		} else if (opcion == 5) {
			registrarExterno();
		} else if (opcion == 6) {
			inscribirEquipo();
		} else if (opcion == 7) {
			retirarInscripcion();
		} else if (opcion == 8) {
			liberarCupos();
		} else if (opcion == 9) {
			iniciar();
		} else if (opcion == 10) {
			registrarResultado();
		}
	}

	// HU_T1
	private void crearConjunto() throws ClubException {
		DeporteEquipo deporte = consola.elegir("Deporte", club.getDeportes());
		if (deporte == null) {
			return;
		}
		Categoria categoria = consola.elegir("Categoría", club.getCategorias());
		if (categoria == null) {
			return;
		}
		String nombre = consola.leerTexto("Nombre del torneo");
		LocalDate fecha = consola.leerFecha("Día del torneo");
		int cupos = consola.leerEntero("Número de equipos (cupos)", 2, 64);
		TipoTorneo tipo = leerTipo();
		if (tipo == null) {
			return;
		}
		FormatoTorneo formato = leerFormato();
		if (formato == null) {
			return;
		}
		Torneo torneo = gestor.crearTorneoConjunto(usuario, nombre, fecha, cupos, deporte, categoria, tipo, formato);
		System.out.println("ACEPTADO: " + torneo);
	}

	// HU_T1
	private void crearIndividual() throws ClubException {
		Disciplina disciplina = consola.elegir("Disciplina", club.getDisciplinas());
		if (disciplina == null) {
			return;
		}
		List<Nivel> niveles = new ArrayList<Nivel>();
		for (Nivel nivel : Nivel.values()) {
			niveles.add(nivel);
		}
		Nivel nivel = consola.elegir("Nivel", niveles);
		if (nivel == null) {
			return;
		}
		List<Modalidad> modalidades = new ArrayList<Modalidad>();
		for (Modalidad modalidad : Modalidad.values()) {
			modalidades.add(modalidad);
		}
		Modalidad modalidad = consola.elegir("Modalidad", modalidades);
		if (modalidad == null) {
			return;
		}
		String nombre = consola.leerTexto("Nombre del torneo");
		LocalDate fecha = consola.leerFecha("Día del torneo");
		int cupos = consola.leerEntero(modalidad == Modalidad.DOBLES ? "Número de parejas (cupos)"
				: "Número de jugadores (cupos)", 2, 64);
		TipoTorneo tipo = leerTipo();
		if (tipo == null) {
			return;
		}
		FormatoTorneo formato = leerFormato();
		if (formato == null) {
			return;
		}
		Torneo torneo = gestor.crearTorneoIndividual(usuario, nombre, fecha, cupos, disciplina, nivel, modalidad, tipo,
				formato);
		System.out.println("ACEPTADO: " + torneo);
	}

	private TipoTorneo leerTipo() {
		List<String> tipos = new ArrayList<String>();
		tipos.add("Recreativo: sin costo, el ganador recibe un bono de descuento");
		tipos.add("Competitivo: con tarifa, el ganador recibe un premio en efectivo");
		int opcion = consola.leerOpcion("Tipo de torneo", tipos, "Cancelar");
		if (opcion == 1) {
			return new Recreativo(consola.leerEntero("Porcentaje del bono", 1, 100));
		}
		if (opcion == 2) {
			return new Competitivo(consola.leerDecimal("Tarifa de inscripción por jugador o equipo", 1));
		}
		return null;
	}

	private FormatoTorneo leerFormato() {
		List<String> formatos = new ArrayList<String>();
		formatos.add("Todos contra todos");
		formatos.add("Eliminación directa");
		int opcion = consola.leerOpcion("Formato", formatos, "Cancelar");
		if (opcion == 1) {
			return new TodosContraTodos();
		}
		if (opcion == 2) {
			return new EliminacionDirecta();
		}
		return null;
	}

	// HU_T2
	private void registrarExterno() throws ClubException {
		String nombre = consola.leerTexto("Nombre");
		String apellidos = consola.leerTexto("Apellidos");
		String documento = consola.leerTexto("Documento");
		boolean estudiante = consola.confirmar("¿Es estudiante de la universidad?");
		String login = consola.leerTexto("Login para entrar al sistema");
		String password = consola.leerTexto("Contraseña");
		ParticipanteExterno externo = gestor.registrarParticipanteExterno(usuario, login, password, nombre, apellidos,
				documento, estudiante);
		System.out.println("ACEPTADO: " + externo + ". Puede entrar por la consola del socio.");
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
		TorneoConjunto conjunto = (TorneoConjunto) torneo;
		List<Equipo> candidatos = new ArrayList<Equipo>();
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.getDeporte() == conjunto.getDeporte() && equipo.getCategoria() == conjunto.getCategoria()) {
				candidatos.add(equipo);
			}
		}
		Equipo equipo = consola.elegir("Equipo", candidatos);
		if (equipo == null) {
			return;
		}
		InscripcionEquipo inscripcion = gestor.inscribirEquipo(usuario, torneo, equipo);
		System.out.println("ACEPTADO: " + inscripcion + ". Valor a pagar: $" + (long) inscripcion.getValorPagado());
	}

	// HU_T5
	private void retirarInscripcion() throws ClubException {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneos(usuario, EstadoTorneo.INSCRIPCIONES_ABIERTAS));
		if (torneo == null) {
			return;
		}
		InscripcionTorneo inscripcion = consola.elegir("Inscripción", torneo.getInscripciones());
		if (inscripcion == null || !consola.confirmar("¿Retirar a " + inscripcion.getNombre() + "?")) {
			return;
		}
		gestor.retirarInscripcion(usuario, torneo, inscripcion);
		System.out.println("ACEPTADO: el cupo quedó libre.");
	}

	// HU_T6
	private void liberarCupos() throws ClubException {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneos(usuario, EstadoTorneo.INSCRIPCIONES_ABIERTAS));
		if (torneo == null) {
			return;
		}
		gestor.liberarCuposPreferenciales(usuario, torneo);
		System.out.println("ACEPTADO: ahora hay " + torneo.getCuposRegularesLibres() + " cupos regulares libres.");
	}

	// HU_T7
	private void iniciar() throws ClubException {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneos(usuario, EstadoTorneo.INSCRIPCIONES_ABIERTAS));
		if (torneo == null) {
			return;
		}
		if (!consola.confirmar("Después de iniciar nadie se podrá inscribir ni salir. ¿Iniciar " + torneo.getNombre() + "?")) {
			return;
		}
		List<EncuentroTorneo> encuentros = gestor.iniciarTorneo(usuario, torneo);
		System.out.println("ACEPTADO: el torneo está en curso.");
		mostrarEncuentros("Encuentros:", encuentros);
	}

	// HU_T8
	private void registrarResultado() throws ClubException {
		Torneo torneo = consola.elegir("Torneo", gestor.consultarTorneos(usuario, EstadoTorneo.EN_CURSO));
		if (torneo == null) {
			return;
		}
		EncuentroTorneo encuentro = consola.elegir("Encuentro", torneo.getEncuentrosPendientes());
		if (encuentro == null) {
			return;
		}
		int local = consola.leerEntero("Marcador de " + encuentro.getLocal().getNombre(), 0, 999);
		int visitante = consola.leerEntero("Marcador de " + encuentro.getVisitante().getNombre(), 0, 999);
		int rondaAntes = torneo.getRondaActual();
		List<BonoDescuento> bonos = gestor.registrarResultado(usuario, torneo, encuentro, local, visitante);
		System.out.println("ACEPTADO: " + encuentro);
		if (torneo.getRondaActual() > rondaAntes) {
			mostrarEncuentros("Siguiente ronda:", torneo.getEncuentrosDeRonda(torneo.getRondaActual()));
		}
		if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
			System.out.println("El torneo terminó. " + torneo.getTipo().describirPremio(torneo));
			if (!bonos.isEmpty()) {
				System.out.println("Se entregaron " + bonos.size() + " bonos de descuento.");
			}
		}
	}
}
