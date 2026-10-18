package logica;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.academias.Disciplina;
import modelo.academias.EscenarioConjunto;
import modelo.academias.Instalacion;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.Evaluacion;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Gestor del área de entrenamientos.
 * Cubre RF15 (planear sesiones individuales, grupales y de equipo)
 * y RF16 (asistencia y evaluaciones de desempeño).
 */
public class GestorEntrenamiento extends Gestor {

	public GestorEntrenamiento(Club club) {
		super(club);
	}

	// ---------------- RF15: Planear sesión de entrenamiento ----------------

	/**
	 * RF15. Planea una sesión de entrenamiento individual o grupal de una disciplina de academia.
	 * Solo un entrenador o el administrador pueden planearla.
	 * Valida duración > 0, compatibilidad de la instalación, disponibilidad horaria
	 * y que los convocados estén inscritos en la disciplina y libres de otros compromisos.
	 */
	public SesionEntrenamiento planearSesion(Usuario quien, String nombreDisciplina, String idInstalacion,
			LocalDate fecha, LocalTime hora, int duracionMin, List<String> sociosIDs)
			throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException,
			ReglaDeportivaException, ConflictoHorarioException {

		Entrenador entrenador = determinarEntrenador(quien);

		if (duracionMin <= 0) {
			throw new DatoInvalidoException("La duración de la sesión debe ser mayor a 0 minutos");
		}
		if (hora == null) {
			throw new DatoInvalidoException("La hora de inicio no puede ser nula");
		}
		if (fecha == null || fecha.isBefore(club.getFechaActual())) {
			throw new DatoInvalidoException("No se pueden planear entrenamientos en fechas pasadas");
		}
		if (sociosIDs == null || sociosIDs.isEmpty()) {
			throw new DatoInvalidoException("Debe convocar al menos a un socio a la sesión");
		}

		Disciplina disciplina = club.buscarDisciplina(nombreDisciplina);
		Instalacion instalacion = club.buscarInstalacion(idInstalacion);

		// Validar compatibilidad de la instalación con la disciplina
		if (instalacion instanceof EscenarioConjunto || instalacion.getDisciplina() != disciplina) {
			throw new ReglaDeportivaException("Instalación incompatible: '" + instalacion.getId()
					+ "' no corresponde a la disciplina " + disciplina.getNombre());
		}

		LocalTime horaFin = hora.plusMinutes(duracionMin);

		// Validar disponibilidad de la instalación
		if (!instalacion.estaLibre(fecha, hora, horaFin)) {
			throw new ConflictoHorarioException("La instalación '" + instalacion.getId()
					+ "' ya está ocupada en ese horario");
		}

		// Validar disponibilidad del entrenador
		for (SesionEntrenamiento s : club.getSesiones()) {
			if (s.getEntrenador().equals(entrenador) && s.tieneConflictoCon(fecha, hora, horaFin)) {
				throw new ConflictoHorarioException("El entrenador " + entrenador.getNombreCompleto()
						+ " ya tiene otra sesión programada en ese horario");
			}
		}

		// Validar convocados: inscritos en la disciplina y sin choques de horario
		List<Socio> convocados = new ArrayList<Socio>();
		for (String socioID : sociosIDs) {
			Socio socio = club.buscarSocio(socioID);
			if (socio.getInscripcion(disciplina) == null) {
				throw new ReglaDeportivaException("El socio " + socio.getNombreCompleto()
						+ " no está inscrito en la disciplina " + disciplina.getNombre());
			}
			if (club.tieneCompromiso(socio, fecha, hora, horaFin)) {
				throw new ConflictoHorarioException("El socio " + socio.getNombreCompleto()
						+ " ya tiene otro compromiso en esa fecha y horario");
			}
			convocados.add(socio);
		}

		String id = generarIdSesion();
		SesionEntrenamiento sesion = new SesionEntrenamiento(id, entrenador, instalacion, fecha, hora, duracionMin, disciplina);

		for (Socio socio : convocados) {
			Convocatoria conv = new Convocatoria(socio, sesion);
			sesion.agregarConvocatoria(conv);
		}

		instalacion.agregarSesion(sesion);
		club.agregarSesion(sesion);
		return sesion;
	}

	/**
	 * RF15. Planea una sesión de entrenamiento para un equipo de conjunto en un escenario adecuado.
	 * Convoca automáticamente a todos los jugadores activos del equipo.
	 */
	public SesionEntrenamiento planearSesionEquipo(Usuario quien, String nombreEquipo, String idInstalacion,
			LocalDate fecha, LocalTime hora, int duracionMin)
			throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException,
			ReglaDeportivaException, ConflictoHorarioException {

		determinarEntrenador(quien);

		if (duracionMin <= 0) {
			throw new DatoInvalidoException("La duración de la sesión debe ser mayor a 0 minutos");
		}
		if (hora == null) {
			throw new DatoInvalidoException("La hora de inicio no puede ser nula");
		}
		if (fecha == null || fecha.isBefore(club.getFechaActual())) {
			throw new DatoInvalidoException("No se pueden planear entrenamientos en fechas pasadas");
		}

		Equipo equipo = club.buscarEquipo(nombreEquipo);
		Instalacion instalacion = club.buscarInstalacion(idInstalacion);

		// Validar que la instalación sea un escenario de conjunto adecuado para el deporte
		if (!(instalacion instanceof EscenarioConjunto)) {
			throw new ReglaDeportivaException("Instalación incompatible: la sesión de " + equipo.getNombre()
					+ " requiere un escenario de conjunto");
		}
		EscenarioConjunto ec = (EscenarioConjunto) instalacion;
		if (equipo.getDeporte() != null && !ec.getTipo().equalsIgnoreCase(equipo.getDeporte().getNombre())) {
			throw new ReglaDeportivaException("Instalación incompatible: el escenario '" + ec.getNombre()
					+ "' es para " + ec.getTipo() + ", no para " + equipo.getDeporte().getNombre());
		}

		LocalTime horaFin = hora.plusMinutes(duracionMin);

		if (!instalacion.estaLibre(fecha, hora, horaFin)) {
			throw new ConflictoHorarioException("El escenario '" + instalacion.getId()
					+ "' ya está ocupado en ese horario");
		}

		Entrenador entrenador = equipo.getEntrenador();
		if (entrenador == null) {
			if (quien instanceof Entrenador) {
				entrenador = (Entrenador) quien;
			} else {
				throw new ReglaDeportivaException("El equipo " + equipo.getNombre() + " no tiene entrenador asignado");
			}
		}

		for (SesionEntrenamiento s : club.getSesiones()) {
			if (s.getEntrenador().equals(entrenador) && s.tieneConflictoCon(fecha, hora, horaFin)) {
				throw new ConflictoHorarioException("El entrenador " + entrenador.getNombreCompleto()
						+ " ya tiene otra sesión en ese horario");
			}
		}

		String id = generarIdSesion();
		SesionEntrenamiento sesion = new SesionEntrenamiento(id, entrenador, instalacion, fecha, hora, duracionMin, equipo);

		// Convocar a los jugadores activos del equipo
		for (JugadorEquipo jugador : equipo.getJugadores()) {
			if (jugador.estaActivo()) {
				Socio socio = jugador.getSocio();
				if (socio != null) {
					Convocatoria conv = new Convocatoria(socio, sesion);
					sesion.agregarConvocatoria(conv);
				}
			}
		}

		instalacion.agregarSesion(sesion);
		club.agregarSesion(sesion);
		return sesion;
	}

	// ---------------- RF16: Asistencia y evaluación ----------------

	/**
	 * RF16. Registra la asistencia de un socio convocado en una sesión finalizada.
	 * Solo el entrenador responsable de la sesión o el administrador pueden registrarla.
	 * Los estados válidos son asistió, faltó o llegó tarde (no se permite 'pendiente').
	 */
	public void registrarAsistencia(Usuario quien, String idSesion, String socioID, String estadoTexto)
			throws PermisoDenegadoException, NoEncontradoException, EstadoInvalidoException, DatoInvalidoException {

		SesionEntrenamiento sesion = club.buscarSesion(idSesion);
		verificarResponsableSesion(quien, sesion);

		if (!sesion.estaFinalizada()) {
			throw new EstadoInvalidoException("Solo se puede registrar asistencia sobre una sesión finalizada");
		}

		EstadoAsistencia estado = EstadoAsistencia.desdeTexto(estadoTexto);
		if (estado == EstadoAsistencia.PENDIENTE) {
			throw new DatoInvalidoException("El estado de asistencia debe ser asistió, faltó o llegó tarde");
		}

		Convocatoria conv = sesion.buscarConvocatoria(socioID);
		if (conv == null) {
			throw new NoEncontradoException("El socio con id '" + socioID + "' no está convocado a la sesión " + idSesion);
		}

		conv.registrarAsistencia(estado);
	}

	/**
	 * RF16. Registra la evaluación técnica y formativa de un socio en una sesión finalizada.
	 * La calificación debe estar entre 1 y 5. La evaluación es opcional por convocado.
	 */
	public void registrarEvaluacion(Usuario quien, String idSesion, String socioID, int nota, String comentario)
			throws PermisoDenegadoException, NoEncontradoException, EstadoInvalidoException, DatoInvalidoException {

		SesionEntrenamiento sesion = club.buscarSesion(idSesion);
		verificarResponsableSesion(quien, sesion);

		if (!sesion.estaFinalizada()) {
			throw new EstadoInvalidoException("Solo se puede registrar evaluación sobre una sesión finalizada");
		}

		Convocatoria conv = sesion.buscarConvocatoria(socioID);
		if (conv == null) {
			throw new NoEncontradoException("El socio con id '" + socioID + "' no está convocado a la sesión " + idSesion);
		}

		Evaluacion eval = new Evaluacion(nota, comentario);
		conv.asignarEvaluacion(eval);
	}

	/**
	 * Finaliza una sesión de entrenamiento para permitir el registro de asistencia y evaluación.
	 */
	public void finalizarSesion(Usuario quien, String idSesion)
			throws PermisoDenegadoException, NoEncontradoException {
		SesionEntrenamiento sesion = club.buscarSesion(idSesion);
		verificarResponsableSesion(quien, sesion);
		sesion.finalizar();
	}

	// ---------------- Consultas ----------------

	/**
	 * Consulta las sesiones de entrenamiento del club, opcionalmente filtradas por fecha.
	 */
	public List<SesionEntrenamiento> consultarSesiones(Usuario quien, LocalDate fecha) {
		List<SesionEntrenamiento> resultado = new ArrayList<SesionEntrenamiento>();
		for (SesionEntrenamiento s : club.getSesiones()) {
			if (fecha == null || s.getFecha().equals(fecha)) {
				resultado.add(s);
			}
		}
		return resultado;
	}

	/**
	 * Consulta las convocatorias y evaluaciones de un socio.
	 * Solo el propio socio, su entrenador o el administrador pueden consultarlas.
	 */
	public List<Convocatoria> consultarConvocatoriasSocio(Usuario quien, String socioID)
			throws PermisoDenegadoException, NoEncontradoException {
		Socio socio = club.buscarSocio(socioID);
		if (quien instanceof Socio && !((Socio) quien).getSocioID().equalsIgnoreCase(socioID)) {
			throw new PermisoDenegadoException("Solo el propio socio o el personal autorizado pueden consultar su historial de entrenamientos");
		}

		List<Convocatoria> resultado = new ArrayList<Convocatoria>();
		for (SesionEntrenamiento s : club.getSesiones()) {
			Convocatoria conv = s.buscarConvocatoria(socio);
			if (conv != null) {
				resultado.add(conv);
			}
		}
		return resultado;
	}

	// ---------------- Métodos auxiliares de permisos ----------------

	private Entrenador determinarEntrenador(Usuario quien) throws PermisoDenegadoException {
		if (quien instanceof Entrenador) {
			return (Entrenador) quien;
		}
		if (quien instanceof Administrador) {
			// El administrador puede actuar en nombre de la dirección técnica,
			// pero si no se especifica entrenador toma el primer entrenador del club
			for (Usuario u : club.getUsuarios()) {
				if (u instanceof Entrenador) {
					return (Entrenador) u;
				}
			}
			throw new PermisoDenegadoException("No hay ningún entrenador registrado en el club para asignar a la sesión");
		}
		throw new PermisoDenegadoException("Solo un entrenador o el administrador pueden planear sesiones de entrenamiento");
	}

	private void verificarResponsableSesion(Usuario quien, SesionEntrenamiento sesion)
			throws PermisoDenegadoException {
		if (quien instanceof Administrador) {
			return; // El administrador tiene acceso global
		}
		if (quien instanceof Entrenador) {
			if (sesion.getEntrenador().equals(quien)) {
				return;
			}
			throw new PermisoDenegadoException("El entrenador " + quien.getLogin()
					+ " no es el responsable de la sesión " + sesion.getId()
					+ ": solo el responsable puede registrar asistencia o evaluaciones");
		}
		throw new PermisoDenegadoException("Solo el entrenador responsable o el administrador pueden registrar asistencia o evaluaciones");
	}

	private String generarIdSesion() {
		int i = club.getSesiones().size() + 1;
		while (true) {
			String candidato = "SE" + i;
			boolean ocupado = false;
			for (SesionEntrenamiento s : club.getSesiones()) {
				if (s.getId().equalsIgnoreCase(candidato)) {
					ocupado = true;
					break;
				}
			}
			if (!ocupado) {
				return candidato;
			}
			i++;
		}
	}
}
