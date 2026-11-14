package consola.deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import excepciones.ClubException;
import modelo.competencias.Competencia;
import modelo.competencias.EquipoExterno;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.Formato;
import modelo.competencias.GruposEliminacion;
import modelo.competencias.Liga;
import modelo.competencias.ParticipanteCompetencia;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.partidos.Estadistica;
import modelo.partidos.PartidoCompetencia;

/**
 * Competencias externas para el administrador: crearlas, agregar rivales,
 * inscribir equipos del club, programar partidos y registrar resultados y
 * estadísticas (RF25, RF26 y RF27). También tiene las consultas de RF2.
 */
public class MenuCompetenciasAdministrador extends MenuConsultaCompetencias {

	private LectorEstadistica lector;

	public MenuCompetenciasAdministrador(Consola consola) {
		super(consola);
		lector = new LectorEstadistica(consola);
	}

	@Override
	public String getTitulo() {
		return "Competencias externas";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver los participantes y partidos de una competencia");
		opciones.add("Ver la tabla de posiciones de una competencia");
		opciones.add("Ver goleadores y jugadores destacados de una competencia");
		opciones.add("Crear una competencia");
		opciones.add("Agregar un rival externo");
		opciones.add("Inscribir un equipo del club");
		opciones.add("Programar un partido");
		opciones.add("Registrar el resultado de un partido");
		opciones.add("Registrar la estadística de un jugador");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verCompetencia();
		} else if (opcion == 2) {
			verTabla();
		} else if (opcion == 3) {
			verClasificaciones();
		} else if (opcion == 4) {
			crearCompetencia();
		} else if (opcion == 5) {
			agregarRival();
		} else if (opcion == 6) {
			inscribirEquipo();
		} else if (opcion == 7) {
			programarPartido();
		} else if (opcion == 8) {
			registrarResultado();
		} else if (opcion == 9) {
			registrarEstadistica();
		}
	}

	// RF2
	private void verCompetencia() {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		System.out.println(competencia + ", organiza " + competencia.getEntidadOrganizadora() + ". Nómina mínima: "
				+ competencia.getNominaMinima() + ". Desempate: " + competencia.getFormato().getCriterioDesempate());
		consola.mostrarLista("Participantes:", competencia.getParticipantes());
		consola.mostrarLista("Partidos:", competencia.getPartidos());
	}

	// RF25
	private void crearCompetencia() throws ClubException {
		DeporteEquipo deporte = consola.elegir("Deporte", club.getDeportes());
		if (deporte == null) {
			return;
		}
		Categoria categoria = consola.elegir("Categoría", club.getCategorias());
		if (categoria == null) {
			return;
		}
		String nombre = consola.leerTexto("Nombre de la competencia");
		String entidad = consola.leerTexto("Entidad organizadora");
		int temporada = consola.leerEntero("Temporada (año)", 2000, 2100);
		int nominaMinima = consola.leerEntero("Nómina mínima", 1, 50);
		int amarillas = consola.leerEntero("Amarillas acumuladas que suspenden", 1, 20);
		Formato formato = leerFormato();
		if (formato == null) {
			return;
		}
		Competencia competencia = gestorCompetencias.crearCompetencia(usuario, nombre, entidad, temporada, deporte,
				categoria, nominaMinima, amarillas, formato);
		System.out.println("ACEPTADO: se creó " + competencia);
	}

	private Formato leerFormato() {
		List<String> formatos = new ArrayList<String>();
		formatos.add("Liga");
		formatos.add("Grupos y eliminación");
		int opcion = consola.leerOpcion("Formato", formatos, "Cancelar");
		if (opcion == 1) {
			return new Liga(consola.confirmar("¿Ida y vuelta?"));
		}
		if (opcion == 2) {
			int grupos = consola.leerEntero("Número de grupos", 1, 8);
			int clasificados = consola.leerEntero("Clasificados por grupo", 1, 8);
			return new GruposEliminacion(grupos, clasificados);
		}
		return null;
	}

	// RF25
	private void agregarRival() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		String nombre = consola.leerTexto("Nombre del rival");
		String institucion = consola.leerTexto("Institución");
		String grupo = null;
		if (competencia.getFormato() instanceof GruposEliminacion) {
			grupo = consola.elegir("Grupo", ((GruposEliminacion) competencia.getFormato()).getGrupos());
			if (grupo == null) {
				return;
			}
		}
		EquipoExterno rival = gestorCompetencias.agregarRival(usuario, competencia, nombre, institucion, grupo);
		System.out.println("ACEPTADO: " + rival + " participa en " + competencia.getNombre());
	}

	// RF26
	private void inscribirEquipo() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		List<Equipo> candidatos = new ArrayList<Equipo>();
		for (Equipo equipo : club.getEquipos()) {
			if (equipo.estaActivo() && equipo.getDeporte() == competencia.getDeporte()
					&& equipo.getCategoria() == competencia.getCategoria()) {
				candidatos.add(equipo);
			}
		}
		Equipo equipo = consola.elegir("Equipo", candidatos);
		if (equipo == null) {
			return;
		}
		MenuEquiposAdministrador.mostrarEquipo(equipo);
		List<Integer> nomina = consola
				.leerEnteros("Números de la nómina (mínimo " + competencia.getNominaMinima() + ")");
		String grupo = null;
		if (competencia.getFormato() instanceof GruposEliminacion) {
			grupo = consola.elegir("Grupo", ((GruposEliminacion) competencia.getFormato()).getGrupos());
			if (grupo == null) {
				return;
			}
		}
		EquipoInscrito inscrito = gestorCompetencias.inscribirEquipo(usuario, competencia, equipo.getNombre(), nomina,
				grupo);
		System.out.println("ACEPTADO: " + inscrito + " con " + inscrito.getNomina().size() + " jugadores en la nómina");
	}

	// RF27
	private void programarPartido() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		ParticipanteCompetencia local = consola.elegir("Local", competencia.getParticipantes());
		if (local == null) {
			return;
		}
		ParticipanteCompetencia visitante = consola.elegir("Visitante", competencia.getParticipantes());
		if (visitante == null) {
			return;
		}
		LocalDate fecha = consola.leerFecha("Fecha del partido");
		String escenario = consola.leerTexto("Escenario");
		String fase = consola.leerTexto("Fase (por ejemplo Fecha 1 o Semifinal)");
		PartidoCompetencia partido = gestorCompetencias.programarPartido(usuario, competencia, local.getNombre(),
				visitante.getNombre(), fecha, escenario, fase);
		System.out.println("ACEPTADO: " + partido);
	}

	// RF27
	private void registrarResultado() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		List<PartidoCompetencia> pendientes = new ArrayList<PartidoCompetencia>();
		for (PartidoCompetencia partido : competencia.getPartidos()) {
			if (!partido.isJugado()) {
				pendientes.add(partido);
			}
		}
		PartidoCompetencia partido = consola.elegir("Partido", pendientes);
		if (partido == null) {
			return;
		}
		int local = consola.leerEntero("Marcador de " + partido.getLocal().getNombre(), 0, 999);
		int visitante = consola.leerEntero("Marcador de " + partido.getVisitante().getNombre(), 0, 999);
		gestorCompetencias.registrarResultado(usuario, partido, local, visitante);
		System.out.println("ACEPTADO: " + partido);
	}

	// RF27
	private void registrarEstadistica() throws ClubException {
		Competencia competencia = elegirCompetencia();
		if (competencia == null) {
			return;
		}
		PartidoCompetencia partido = consola.elegir("Partido", competencia.getPartidos());
		if (partido == null) {
			return;
		}
		List<JugadorEquipo> nomina = new ArrayList<JugadorEquipo>();
		nomina.addAll(partido.getLocal().getNomina());
		nomina.addAll(partido.getVisitante().getNomina());
		JugadorEquipo jugador = consola.elegir("Jugador habilitado de la nómina",
				LectorEstadistica.habilitados(nomina, partido));
		if (jugador == null) {
			return;
		}
		Estadistica estadistica = lector.leer(jugador);
		if (estadistica == null) {
			return;
		}
		gestorCompetencias.registrarEstadistica(usuario, partido, jugador, estadistica);
		System.out.println("ACEPTADO: " + estadistica);
		if (jugador.getSuspensionPendiente() != null) {
			System.out.println("Queda suspendido: " + jugador.getSuspensionPendiente());
		}
	}
}
