package logica;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import excepciones.ConflictoHorarioException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.academias.Disciplina;
import modelo.academias.EstadisticaPractica;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.usuarios.Administrador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Gestor del área de academias deportivas y reservas.
 */
public class GestorAcademias extends Gestor {

	public GestorAcademias(Club club) {
		super(club);
	}

	// RF6: Inscribir en academia

	/**
	 * RF6. Inscribe a un socio en una academia (tenis, pádel o tenis de mesa) con
	 * su nivel inicial.
	 * Solo el propio socio o el administrador pueden realizar la inscripción.
	 * Se rechaza si ya está inscrito en esa disciplina, si la disciplina no existe
	 * o si el nivel es inválido.
	 */
	public Inscripcion inscribirEnAcademia(Usuario quien, String socioID, String nombreDisciplina, String nivelTexto)
			throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException, DuplicadoException {
		Socio socio = club.buscarSocio(socioID);

		// Permiso: socio propio o administrador
		if (quien instanceof Socio && !((Socio) quien).getSocioID().equals(socioID)) {
			throw new PermisoDenegadoException("Un socio solo puede inscribirse a sí mismo en una academia");
		}
		if (!(quien instanceof Socio) && !(quien instanceof Administrador)) {
			throw new PermisoDenegadoException("Solo un socio o el administrador pueden inscribir en academias");
		}

		Disciplina disciplina = club.buscarDisciplina(nombreDisciplina);
		Nivel nivel = Nivel.desdeTexto(nivelTexto);

		if (socio.getInscripcion(disciplina) != null) {
			throw new DuplicadoException("El socio " + socio.getNombreCompleto()
					+ " ya tiene una inscripción activa en " + disciplina.getNombre());
		}

		Inscripcion inscripcion = new Inscripcion(socio, disciplina, nivel, Inscripcion.ELO_INICIAL);
		socio.agregarInscripcion(inscripcion);
		disciplina.agregarInscripcion(inscripcion);
		return inscripcion;
	}

	// RF11: Consultar perfil

	/**
	 * RF11. Consulta las inscripciones y nivel de un socio en las academias.
	 * Solo el propio socio o el administrador pueden consultarlo.
	 */
	public List<Inscripcion> consultarPerfil(Usuario quien, String socioID)
			throws PermisoDenegadoException, NoEncontradoException {
		Socio socio = club.buscarSocio(socioID);
		if (quien instanceof Socio && !((Socio) quien).getSocioID().equals(socioID)) {
			throw new PermisoDenegadoException("Solo el propio socio o el administrador pueden consultar el perfil");
		}
		return socio.getInscripciones();
	}

	// RF7: Reservar instalación

	/**
	 * RF7. Reserva una instalación deportiva para una fecha y franja horaria. Solo
	 * socios.
	 * Valida rol (el administrador no reserva, RF36), que la instalación sea
	 * reservable,
	 * que el socio esté inscrito en la disciplina y al día con su mensualidad,
	 * capacidad y modalidad,
	 * disponibilidad horaria y que no se exceda el cupo simultáneo de la
	 * disciplina.
	 */
	public Reserva reservar(Usuario quien, String idInstalacion, LocalDate fecha, LocalTime inicio, LocalTime fin,
			int numJugadores, Modalidad modalidad) throws PermisoDenegadoException, NoEncontradoException,
			DatoInvalidoException, ReglaDeportivaException, EstadoInvalidoException, ConflictoHorarioException,
			CupoExcedidoException {
		verificarSocio(quien);
		Socio titular = (Socio) quien;

		if (inicio == null || fin == null || !fin.isAfter(inicio)) {
			throw new DatoInvalidoException("Horario inválido: la hora de inicio debe ser anterior a la hora de fin");
		}
		if (fecha.isBefore(club.getFechaActual())) {
			throw new DatoInvalidoException("No se pueden realizar reservas para fechas pasadas");
		}

		Instalacion instalacion = club.buscarInstalacion(idInstalacion);

		if (!instalacion.esReservable()) {
			throw new ReglaDeportivaException("La instalación '" + instalacion.getId()
					+ "' es un escenario de conjunto y no es reservable para prácticas de academia");
		}

		Disciplina disciplina = instalacion.getDisciplina();
		if (disciplina != null && titular.getInscripcion(disciplina) == null) {
			throw new ReglaDeportivaException("El socio debe estar inscrito en la disciplina "
					+ disciplina.getNombre() + " para poder reservar la instalación");
		}

		if (!titular.estaAlDia(fecha)) {
			throw new EstadoInvalidoException("El socio " + titular.getNombreCompleto()
					+ " tiene la mensualidad vencida y no puede realizar reservas");
		}

		if (!instalacion.admite(numJugadores, modalidad)) {
			throw new ReglaDeportivaException("La instalación no admite " + numJugadores
					+ " jugadores en modalidad " + modalidad);
		}

		if (!instalacion.estaLibre(fecha, inicio, fin)) {
			throw new ConflictoHorarioException("La instalación '" + instalacion.getId()
					+ "' ya está ocupada en ese horario");
		}

		if (disciplina != null) {
			int simultaneas = club.contarReservasSimultaneas(disciplina, fecha, inicio, fin);
			if (simultaneas >= disciplina.getCupoInstalaciones()) {
				throw new CupoExcedidoException("Cupo simultáneo de la disciplina " + disciplina.getNombre()
						+ " completo: máximo " + disciplina.getCupoInstalaciones() + " canchas a la vez");
			}
		}

		if (club.tieneCompromiso(titular, fecha, inicio, fin)) {
			throw new ConflictoHorarioException("El socio " + titular.getNombreCompleto()
					+ " ya tiene otro compromiso en esa fecha y horario");
		}

		String idReserva = "R" + (club.getReservas().size() + 1);
		Reserva reserva = new Reserva(idReserva, titular, instalacion, fecha, inicio, fin, numJugadores, modalidad);
		club.agregarReserva(reserva);
		instalacion.agregarReserva(reserva);
		titular.agregarReserva(reserva);
		return reserva;
	}

	// RF7 y RF4: Registrar ingreso a la instalación
	//

	/**
	 * RF7 y RF4. Registra el ingreso a la instalación para hacer uso de la reserva.
	 * Valida que no se ingrese con bebidas calientes a canchas de pádel o mesas
	 * techadas.
	 */
	public boolean registrarIngreso(Usuario quien, String idReserva, boolean llevaBebidaCaliente)
			throws NoEncontradoException, EstadoInvalidoException {
		Reserva reserva = club.buscarReserva(idReserva);

		if (!reserva.estaActiva()) {
			throw new EstadoInvalidoException("La reserva no está activa");
		}

		if (llevaBebidaCaliente && reserva.getInstalacion().restringeBebidaCaliente()) {
			throw new EstadoInvalidoException("No se permite el ingreso con bebidas calientes a la instalación "
					+ reserva.getInstalacion().getNombre());
		}

		return true;
	}

	// RF8: Cancelar reserva

	/**
	 * RF8. Cancela una reserva con un mínimo de 2 horas de anticipación.
	 * Solo el titular puede cancelar la reserva. Cancela automáticamente el partido
	 * de práctica asociado.
	 */
	public void cancelarReserva(Usuario quien, String idReserva, LocalTime horaActual)
			throws PermisoDenegadoException, NoEncontradoException, EstadoInvalidoException {
		Reserva reserva = club.buscarReserva(idReserva);

		if (quien instanceof Socio && reserva.getTitular() != quien) {
			throw new PermisoDenegadoException("Solo el socio titular puede cancelar su reserva");
		}

		if (!reserva.estaActiva()) {
			throw new EstadoInvalidoException("La reserva " + idReserva + " ya no se encuentra activa");
		}

		double horasFaltantes = reserva.horasHastaInicio(club.getFechaActual(), horaActual);
		if (horasFaltantes < 2.0) {
			throw new EstadoInvalidoException(
					"La cancelación debe realizarse con un mínimo de 2 horas de anticipación");
		}

		reserva.cancelar();
	}

	public void cancelarReserva(Usuario quien, String idReserva)
			throws PermisoDenegadoException, NoEncontradoException, EstadoInvalidoException {
		cancelarReserva(quien, idReserva, LocalTime.now());
	}

	// RF9: Programar partido de práctica

	/**
	 * RF9. Programa un partido de práctica sobre una reserva activa.
	 * Valida que los participantes pertenezcan a la misma disciplina, que sus
	 * niveles sean
	 * adyacentes (salvo si participa un entrenador, que exime la regla), que la
	 * cantidad
	 * coincida con la modalidad (pádel siempre 4 en dobles) y que no tengan
	 * compromisos en ese horario.
	 */
	public PartidoPractica programarPartidoPractica(Usuario quien, String idReserva, List<String> sociosIDs)
			throws NoEncontradoException, EstadoInvalidoException, ReglaDeportivaException,
			ConflictoHorarioException, DatoInvalidoException {
		Reserva reserva = club.buscarReserva(idReserva);

		if (!reserva.estaActiva()) {
			throw new EstadoInvalidoException("Todo partido de práctica debe programarse sobre una reserva activa");
		}
		if (reserva.getPartido() != null) {
			throw new EstadoInvalidoException("La reserva " + idReserva + " ya tiene un partido de práctica asociado");
		}

		if (sociosIDs == null || sociosIDs.size() != reserva.getNumJugadores()) {
			throw new ReglaDeportivaException("El partido debe tener exactamente " + reserva.getNumJugadores()
					+ " participantes para la modalidad " + reserva.getModalidad());
		}

		Disciplina disciplina = reserva.getInstalacion().getDisciplina();
		List<Socio> participantes = new ArrayList<Socio>();
		boolean hayEntrenador = false;

		for (String id : sociosIDs) {
			Socio socio = club.buscarSocio(id);
			if (participantes.contains(socio)) {
				throw new ReglaDeportivaException("Los participantes del partido deben ser socios distintos");
			}
			if (disciplina != null && socio.getInscripcion(disciplina) == null) {
				throw new ReglaDeportivaException("El socio " + socio.getNombreCompleto()
						+ " no está inscrito en la disciplina " + disciplina.getNombre());
			}
			if (socio.esEntrenador()) {
				hayEntrenador = true;
			}
			participantes.add(socio);
		}

		// Niveles iguales o adyacentes, salvo que juegue un entrenador (RF9, EP9.1c)
		if (!hayEntrenador && disciplina != null && participantes.size() >= 2) {
			Nivel base = participantes.get(0).getInscripcion(disciplina).getNivel();
			for (int i = 1; i < participantes.size(); i++) {
				Nivel rival = participantes.get(i).getInscripcion(disciplina).getNivel();
				if (!base.esAdyacenteA(rival)) {
					throw new ReglaDeportivaException("Niveles no compatibles: " + base + " y " + rival
							+ " no son adyacentes y ningún participante es entrenador");
				}
			}
		}

		// Validar que ningún participante tenga compromisos previos en ese bloque
		// horario (EP9.3b)
		for (Socio participante : participantes) {
			// Si es el titular de esta reserva, ya sabemos que tiene este espacio asignado
			if (participante == reserva.getTitular()) {
				continue;
			}
			if (club.tieneCompromiso(participante, reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin())) {
				throw new ConflictoHorarioException("El participante " + participante.getNombreCompleto()
						+ " ya tiene otro compromiso en esa fecha y horario");
			}
		}

		String idPartido = "PP" + (club.getPartidosPractica().size() + 1);
		PartidoPractica partido = new PartidoPractica(idPartido, reserva, participantes);
		club.agregarPartidoPractica(partido);
		reserva.setPartido(partido);
		return partido;
	}

	// RF10: Registrar resultado de práctica

	/**
	 * RF10. Registra los sets, estadísticas y ganador de un partido de práctica y
	 * actualiza el ELO.
	 * Solo un socio participante puede registrar el resultado.
	 */
	public void registrarResultadoPractica(Usuario quien, String idPartido, Map<Socio, EstadisticaPractica> stats)
			throws NoEncontradoException, EstadoInvalidoException, PermisoDenegadoException,
			ReglaDeportivaException, DatoInvalidoException {
		PartidoPractica partido = club.buscarPartidoPractica(idPartido);
		partido.registrarResultado(quien, stats);
	}

	// RF11: Consultar historial de partidos

	/**
	 * RF11. Consulta el historial de partidos de un socio en una disciplina en un
	 * rango de fechas.
	 * Solo el propio socio o el administrador pueden consultar el historial.
	 */
	public List<PartidoPractica> consultarHistorial(Usuario quien, String socioID, String nombreDisciplina,
			LocalDate desde, LocalDate hasta) throws PermisoDenegadoException, NoEncontradoException,
			ReglaDeportivaException {
		Socio socio = club.buscarSocio(socioID);

		if (quien instanceof Socio && !((Socio) quien).getSocioID().equals(socioID)) {
			throw new PermisoDenegadoException("Solo el propio socio o el administrador pueden consultar el historial");
		}

		Disciplina disciplina = club.buscarDisciplina(nombreDisciplina);
		Inscripcion inscripcion = socio.getInscripcion(disciplina);
		if (inscripcion == null) {
			throw new ReglaDeportivaException("El socio " + socio.getNombreCompleto()
					+ " no está inscrito en " + disciplina.getNombre());
		}

		List<PartidoPractica> resultado = new ArrayList<PartidoPractica>();
		for (PartidoPractica partido : inscripcion.getPartidos()) {
			LocalDate fechaPartido = partido.getReserva().getFecha();
			boolean cumpleDesde = (desde == null || !fechaPartido.isBefore(desde));
			boolean cumpleHasta = (hasta == null || !fechaPartido.isAfter(hasta));
			if (cumpleDesde && cumpleHasta) {
				resultado.add(partido);
			}
		}
		return resultado;
	}

	// RF11: Consultar ranking ELO

	/**
	 * RF11. Consulta la tabla de posiciones (ranking de mayor a menor ELO) de una
	 * disciplina.
	 * Cualquier usuario puede consultarla.
	 */
	public List<Inscripcion> consultarRanking(Usuario quien, String nombreDisciplina) throws NoEncontradoException {
		Disciplina disciplina = club.buscarDisciplina(nombreDisciplina);
		return disciplina.getRanking();
	}

	// RF36: Consultar reservas e instalaciones

	/**
	 * RF36. Consulta las reservas realizadas para una fecha.
	 * Abierto para socios y administrador.
	 */
	public List<Reserva> consultarReservas(Usuario quien, LocalDate fecha) {
		List<Reserva> resultado = new ArrayList<Reserva>();
		for (Reserva reserva : club.getReservas()) {
			if (fecha == null || reserva.getFecha().equals(fecha)) {
				resultado.add(reserva);
			}
		}
		return resultado;
	}

	/**
	 * Consulta las instalaciones del club, opcionalmente filtradas por disciplina.
	 */
	public List<Instalacion> consultarInstalaciones(Usuario quien, String nombreDisciplina) {
		if (nombreDisciplina == null || nombreDisciplina.trim().isEmpty()) {
			return club.getInstalaciones();
		}
		List<Instalacion> resultado = new ArrayList<Instalacion>();
		for (Instalacion instalacion : club.getInstalaciones()) {
			if (instalacion.getDisciplina() != null
					&& instalacion.getDisciplina().getNombre().equalsIgnoreCase(nombreDisciplina)) {
				resultado.add(instalacion);
			}
		}
		return resultado;
	}
}
