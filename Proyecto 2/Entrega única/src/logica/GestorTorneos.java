package logica;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.ConflictoHorarioException;
import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.competencias.Competencia;
import modelo.competencias.FilaTabla;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.personal.Turno;
import modelo.torneos.BonoDescuento;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.FilaTorneo;
import modelo.torneos.FormatoTorneo;
import modelo.torneos.InscripcionEquipo;
import modelo.torneos.InscripcionIndividual;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.TipoTorneo;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Administrador;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Maneja los torneos internos: creación, inscripciones con cupos preferenciales,
 * encuentros, premios y consultas. Cubre las historias HU_T1 a HU_T10.
 */
public class GestorTorneos extends Gestor {

	public GestorTorneos(Club club) {
		super(club);
	}

	// HU_T1: crear torneos

	/**
	 * HU_T1. Crea un torneo de un deporte de conjunto en una categoría. Solo el administrador.
	 */
	public TorneoConjunto crearTorneoConjunto(Usuario quien, String nombre, LocalDate fecha, int cupos,
			DeporteEquipo deporte, Categoria categoria, TipoTorneo tipo, FormatoTorneo formato)
			throws PermisoDenegadoException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);
		validarDatosTorneo(nombre, fecha, cupos, tipo);

		TorneoConjunto torneo = new TorneoConjunto(nombre.trim(), fecha, cupos, tipo, formato, deporte, categoria);
		club.agregarTorneo(torneo);
		return torneo;
	}

	/**
	 * HU_T1. Crea un torneo de una disciplina individual en un nivel. Solo el administrador.
	 * En dobles los cupos son de parejas.
	 */
	public TorneoIndividual crearTorneoIndividual(Usuario quien, String nombre, LocalDate fecha, int cupos,
			Disciplina disciplina, Nivel nivel, Modalidad modalidad, TipoTorneo tipo, FormatoTorneo formato)
			throws PermisoDenegadoException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);
		validarDatosTorneo(nombre, fecha, cupos, tipo);

		TorneoIndividual torneo = new TorneoIndividual(nombre.trim(), fecha, cupos, tipo, formato, disciplina, nivel,
				modalidad);
		club.agregarTorneo(torneo);
		return torneo;
	}

	private void validarDatosTorneo(String nombre, LocalDate fecha, int cupos, TipoTorneo tipo)
			throws DuplicadoException, DatoInvalidoException {
		if (nombre == null || nombre.trim().isEmpty()) {
			throw new DatoInvalidoException("El torneo debe tener nombre");
		}
		if (club.existeTorneo(nombre.trim())) {
			throw new DuplicadoException("Ya existe un torneo llamado '" + nombre.trim() + "'");
		}
		if (!fecha.isAfter(club.getFechaActual())) {
			throw new DatoInvalidoException("La fecha del torneo debe ser posterior a hoy (" + club.getFechaActual() + ")");
		}
		if (cupos < 2) {
			throw new DatoInvalidoException("Un torneo necesita al menos 2 cupos");
		}
		tipo.validar();
	}

	// HU_T2: registrar participantes externos

	/**
	 * HU_T2. Crea la cuenta de un estudiante de la universidad o de una persona externa
	 * para que pueda inscribirse en torneos. Solo el administrador.
	 */
	public ParticipanteExterno registrarParticipanteExterno(Usuario quien, String login, String password,
			String nombre, String apellidos, String documento, boolean estudiante)
			throws PermisoDenegadoException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);

		if (vacio(login) || vacio(password) || vacio(nombre) || vacio(apellidos) || vacio(documento)) {
			throw new DatoInvalidoException("Login, contraseña, nombre, apellidos y documento son obligatorios");
		}
		if (club.existeUsuario(login.trim())) {
			throw new DuplicadoException("Ya existe un usuario con login '" + login.trim() + "'");
		}
		for (Usuario usuario : club.getUsuarios()) {
			if (usuario instanceof ParticipanteExterno
					&& ((ParticipanteExterno) usuario).getDocumento().equals(documento.trim())) {
				throw new DuplicadoException("Ya hay un participante externo con el documento " + documento.trim());
			}
		}

		ParticipanteExterno externo = new ParticipanteExterno(login.trim(), password, nombre.trim(), apellidos.trim(),
				documento.trim(), estudiante);
		club.agregarUsuario(externo);
		return externo;
	}

	private boolean vacio(String texto) {
		return texto == null || texto.trim().isEmpty();
	}

	// HU_T3: inscribirse en un torneo individual

	/**
	 * HU_T3. El jugador se inscribe solo (sencillos) o con su pareja (dobles).
	 * Pueden hacerlo socios, empleados y participantes externos. Si el jugador está entre
	 * los mejores del ranking ocupa un cupo preferencial; si no, uno regular.
	 * En un torneo competitivo, cada jugador que no sea empleado paga la tarifa.
	 */
	public InscripcionIndividual inscribirIndividual(Usuario quien, Torneo torneo, Usuario pareja)
			throws PermisoDenegadoException, ReglaDeportivaException, EstadoInvalidoException, DuplicadoException,
			ConflictoHorarioException, CupoExcedidoException {
		verificarJugador(quien);

		if (!(torneo instanceof TorneoIndividual)) {
			throw new ReglaDeportivaException("En " + torneo.getNombre() + " se inscriben equipos completos, no jugadores");
		}
		TorneoIndividual individual = (TorneoIndividual) torneo;
		verificarAbierto(torneo);
		if (individual.esDobles() && pareja == null) {
			throw new ReglaDeportivaException(torneo.getNombre() + " es de dobles: debe inscribirse con su pareja");
		}
		if (!individual.esDobles() && pareja != null) {
			throw new ReglaDeportivaException(torneo.getNombre() + " es de sencillos: no lleva pareja");
		}
		if (pareja == quien) {
			throw new ReglaDeportivaException("La pareja tiene que ser otra persona");
		}
		verificarPuedeJugar(quien, individual);
		if (pareja != null) {
			verificarJugador(pareja);
			verificarPuedeJugar(pareja, individual);
		}

		boolean preferente = esPreferente(individual, quien) || (pareja != null && esPreferente(individual, pareja));
		boolean preferencial = asignarCupo(torneo, preferente);
		InscripcionIndividual inscripcion = new InscripcionIndividual(quien, pareja, club.getFechaActual(), preferencial);
		inscripcion.setValorPagado(torneo.getTipo().tarifaPara(inscripcion));
		torneo.agregarInscripcion(inscripcion);
		return inscripcion;
	}

	// Socios, empleados y externos pueden jugar; el administrador no.
	private void verificarJugador(Usuario usuario) throws PermisoDenegadoException {
		if (!(usuario instanceof Socio || usuario instanceof Empleado || usuario instanceof ParticipanteExterno)) {
			throw new PermisoDenegadoException("El usuario '" + (usuario == null ? "" : usuario.getLogin())
					+ "' no puede jugar torneos: solo socios, empleados, estudiantes y externos");
		}
	}

	private void verificarAbierto(Torneo torneo) throws EstadoInvalidoException {
		if (!torneo.estaAbierto()) {
			throw new EstadoInvalidoException("El torneo " + torneo.getNombre() + " ya no recibe cambios en las inscripciones (está "
					+ torneo.getEstado() + ")");
		}
	}

	/**
	 * Reglas de cada jugador: no estar inscrito dos veces, que un empleado no esté de turno ese día,
	 * y que un socio inscrito en la academia de la disciplina tenga el nivel del torneo.
	 */
	private void verificarPuedeJugar(Usuario jugador, TorneoIndividual torneo)
			throws DuplicadoException, ConflictoHorarioException, ReglaDeportivaException {
		if (torneo.buscarInscripcionDe(jugador) != null) {
			throw new DuplicadoException(jugador.getNombreCompleto() + " ya está inscrito en " + torneo.getNombre());
		}
		if (jugador instanceof Empleado && estaDeTurno((Empleado) jugador, torneo.getFecha())) {
			throw new ConflictoHorarioException(jugador.getNombreCompleto() + " está de turno el "
					+ torneo.getFecha() + " y no puede jugar ese día");
		}
		if (jugador instanceof Socio) {
			Inscripcion academia = ((Socio) jugador).getInscripcion(torneo.getDisciplina());
			if (academia != null && academia.getNivel() != torneo.getNivel()) {
				throw new ReglaDeportivaException(jugador.getNombreCompleto() + " es nivel "
						+ academia.getNivel().name().toLowerCase() + " en " + torneo.getDisciplina().getNombre()
						+ " y el torneo es nivel " + torneo.getNivel().name().toLowerCase());
			}
		}
	}

	/**
	 * Los turnos se repiten cada semana, así que basta con mirar el día de la semana.
	 */
	private boolean estaDeTurno(Empleado empleado, LocalDate fecha) {
		DayOfWeek dia = fecha.getDayOfWeek();
		for (Turno turno : club.getTurnos()) {
			if (turno.getEmpleado() == empleado && turno.getDia() == dia) {
				return true;
			}
		}
		return false;
	}

	// HU_T4: inscribir un equipo en un torneo de conjunto

	/**
	 * HU_T4. Inscribe un equipo completo. Lo puede hacer el administrador o el entrenador del equipo.
	 * El equipo debe estar activo, ser del deporte y la categoría del torneo y tener al menos
	 * el mínimo de jugadores activos que pide el deporte.
	 */
	public InscripcionEquipo inscribirEquipo(Usuario quien, Torneo torneo, Equipo equipo)
			throws PermisoDenegadoException, ReglaDeportivaException, EstadoInvalidoException, DuplicadoException,
			CupoExcedidoException {
		verificarAdministradorOEntrenadorDe(quien, equipo);

		if (!(torneo instanceof TorneoConjunto)) {
			throw new ReglaDeportivaException("En " + torneo.getNombre() + " se inscriben jugadores, no equipos");
		}
		TorneoConjunto conjunto = (TorneoConjunto) torneo;
		verificarAbierto(torneo);
		if (!equipo.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + equipo.getNombre() + " está inactivo");
		}
		if (equipo.getDeporte() != conjunto.getDeporte() || equipo.getCategoria() != conjunto.getCategoria()) {
			throw new ReglaDeportivaException("El equipo " + equipo + " no es de " + conjunto.getDescripcion());
		}
		int minimo = conjunto.getDeporte().getMinimoHabilitados();
		if (equipo.getJugadoresActivos().size() < minimo) {
			throw new ReglaDeportivaException("El equipo " + equipo.getNombre() + " no está completo: tiene "
					+ equipo.getJugadoresActivos().size() + " jugadores activos y " + conjunto.getDeporte().getNombre()
					+ " pide " + minimo);
		}
		if (buscarInscripcionDeEquipo(torneo, equipo) != null) {
			throw new DuplicadoException("El equipo " + equipo.getNombre() + " ya está inscrito en " + torneo.getNombre());
		}

		boolean preferencial = asignarCupo(torneo, esPreferente(conjunto, equipo));
		InscripcionEquipo inscripcion = new InscripcionEquipo(equipo, club.getFechaActual(), preferencial);
		inscripcion.setValorPagado(torneo.getTipo().tarifaPara(inscripcion));
		torneo.agregarInscripcion(inscripcion);
		return inscripcion;
	}

	private void verificarAdministradorOEntrenadorDe(Usuario quien, Equipo equipo) throws PermisoDenegadoException {
		if (quien instanceof Administrador) {
			return;
		}
		if (quien instanceof Entrenador && equipo.getEntrenador() == quien) {
			return;
		}
		throw new PermisoDenegadoException("Solo el administrador o el entrenador de " + equipo.getNombre()
				+ " pueden manejar sus inscripciones");
	}

	private InscripcionEquipo buscarInscripcionDeEquipo(Torneo torneo, Equipo equipo) {
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			if (inscripcion instanceof InscripcionEquipo && ((InscripcionEquipo) inscripcion).getEquipo() == equipo) {
				return (InscripcionEquipo) inscripcion;
			}
		}
		return null;
	}

	// HU_T3 y HU_T4: cupos preferenciales

	/**
	 * Devuelve true si la inscripción toma un cupo preferencial. Un preferente usa primero
	 * los cupos reservados y, si ya están llenos, uno regular. Los demás solo usan regulares.
	 */
	private boolean asignarCupo(Torneo torneo, boolean preferente) throws CupoExcedidoException {
		if (preferente && torneo.getCuposPreferencialesLibres() > 0) {
			return true;
		}
		if (torneo.getCuposRegularesLibres() > 0) {
			return false;
		}
		if (torneo.getCuposPreferencialesLibres() > 0) {
			throw new CupoExcedidoException("No quedan cupos regulares en " + torneo.getNombre() + ". Quedan "
					+ torneo.getCuposPreferencialesLibres() + " cupos reservados para los mejores del ranking");
		}
		throw new CupoExcedidoException("El torneo " + torneo.getNombre() + " ya no tiene cupos");
	}

	/**
	 * Un socio es preferente si menos de N socios del mismo nivel tienen más ELO que él,
	 * donde N son los cupos reservados. Empleados y externos no tienen ranking.
	 */
	private boolean esPreferente(TorneoIndividual torneo, Usuario jugador) {
		if (!(jugador instanceof Socio)) {
			return false;
		}
		Inscripcion propia = ((Socio) jugador).getInscripcion(torneo.getDisciplina());
		if (propia == null || propia.getNivel() != torneo.getNivel()) {
			return false;
		}
		int mejores = 0;
		for (Inscripcion otra : torneo.getDisciplina().getInscripciones()) {
			if (otra.getNivel() == torneo.getNivel() && otra.getPuntajeELO() > propia.getPuntajeELO()) {
				mejores++;
			}
		}
		return mejores < torneo.getCuposReservados();
	}

	/**
	 * Un equipo es preferente si sumó puntos en las competencias de su deporte y menos de N equipos
	 * de la misma categoría sumaron más que él.
	 */
	private boolean esPreferente(TorneoConjunto torneo, Equipo equipo) {
		int propios = puntosEnCompetencias(equipo);
		if (propios == 0) {
			return false;
		}
		int mejores = 0;
		for (Equipo otro : club.getEquipos()) {
			if (otro != equipo && otro.getDeporte() == torneo.getDeporte()
					&& otro.getCategoria() == torneo.getCategoria() && puntosEnCompetencias(otro) > propios) {
				mejores++;
			}
		}
		return mejores < torneo.getCuposReservados();
	}

	/**
	 * Puntos que sacó el equipo en todas las competencias externas, según sus tablas.
	 */
	private int puntosEnCompetencias(Equipo equipo) {
		int puntos = 0;
		for (Competencia competencia : club.getCompetencias()) {
			if (competencia.getDeporte() == equipo.getDeporte()) {
				for (FilaTabla fila : competencia.calcularTabla(null)) {
					if (fila.getParticipante().representa(equipo)) {
						puntos += fila.getPuntos();
					}
				}
			}
		}
		return puntos;
	}

	// HU_T5: salirse de un torneo

	/**
	 * HU_T5. Retira una inscripción antes de que el torneo empiece y libera el cupo.
	 * Un jugador retira la suya (en dobles cualquiera de los dos); un equipo lo retira
	 * su entrenador o el administrador. Lo pagado se devuelve por fuera del sistema.
	 */
	public void retirarInscripcion(Usuario quien, Torneo torneo, InscripcionTorneo inscripcion)
			throws PermisoDenegadoException, EstadoInvalidoException {
		if (inscripcion instanceof InscripcionEquipo) {
			verificarAdministradorOEntrenadorDe(quien, ((InscripcionEquipo) inscripcion).getEquipo());
		} else if (!(quien instanceof Administrador) && !inscripcion.incluye(quien)) {
			throw new PermisoDenegadoException("Solo los jugadores de la inscripción o el administrador pueden retirarla");
		}
		verificarAbierto(torneo);

		torneo.retirarInscripcion(inscripcion);
	}

	// HU_T6: liberar los cupos preferenciales

	/**
	 * HU_T6. Los cupos preferenciales que no se llenaron pasan a ser regulares. Solo el administrador.
	 */
	public void liberarCuposPreferenciales(Usuario quien, Torneo torneo)
			throws PermisoDenegadoException, EstadoInvalidoException {
		verificarAdministrador(quien);
		verificarAbierto(torneo);
		if (torneo.isPreferencialesLiberados()) {
			throw new EstadoInvalidoException("Los cupos preferenciales de " + torneo.getNombre() + " ya se liberaron");
		}

		torneo.liberarCuposPreferenciales();
	}

	// HU_T7: iniciar el torneo

	/**
	 * HU_T7. Cierra las inscripciones y arma los encuentros según el formato. Solo el administrador.
	 * Desde aquí ya nadie se puede inscribir ni salir.
	 */
	public List<EncuentroTorneo> iniciarTorneo(Usuario quien, Torneo torneo)
			throws PermisoDenegadoException, EstadoInvalidoException, CupoExcedidoException {
		verificarAdministrador(quien);
		verificarAbierto(torneo);
		if (torneo.getInscripciones().size() < 2) {
			throw new CupoExcedidoException("El torneo " + torneo.getNombre() + " necesita al menos 2 inscritos para empezar");
		}

		List<EncuentroTorneo> encuentros = torneo.getFormato().crearEncuentrosIniciales(torneo);
		torneo.agregarEncuentros(encuentros);
		torneo.setEstado(EstadoTorneo.EN_CURSO);
		return encuentros;
	}

	// HU_T8: registrar resultados y premiar

	/**
	 * HU_T8. Registra el marcador de un encuentro. Si con eso termina la ronda, arma la siguiente;
	 * si termina el torneo, lo finaliza y entrega el premio (los bonos quedan guardados en el club).
	 * Solo el administrador. Devuelve los bonos creados (vacía si no hubo).
	 */
	public List<BonoDescuento> registrarResultado(Usuario quien, Torneo torneo, EncuentroTorneo encuentro,
			int marcadorLocal, int marcadorVisitante) throws PermisoDenegadoException, EstadoInvalidoException,
			DatoInvalidoException, ReglaDeportivaException {
		verificarAdministrador(quien);

		if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
			throw new EstadoInvalidoException("El torneo " + torneo.getNombre() + " no está en curso (está "
					+ torneo.getEstado() + ")");
		}
		if (!torneo.getEncuentros().contains(encuentro)) {
			throw new DatoInvalidoException("Ese encuentro no es del torneo " + torneo.getNombre());
		}
		if (encuentro.isJugado()) {
			throw new EstadoInvalidoException("El encuentro #" + encuentro.getNumero() + " ya tiene resultado");
		}
		if (marcadorLocal < 0 || marcadorVisitante < 0) {
			throw new DatoInvalidoException("Los marcadores no pueden ser negativos");
		}
		if (marcadorLocal == marcadorVisitante && !torneo.getFormato().admiteEmpate(torneo)) {
			throw new ReglaDeportivaException("En " + torneo.getNombre() + " no puede haber empate");
		}

		encuentro.registrarResultado(marcadorLocal, marcadorVisitante);
		torneo.agregarEncuentros(torneo.getFormato().crearSiguienteRonda(torneo));

		List<BonoDescuento> bonos = new ArrayList<BonoDescuento>();
		if (torneo.getFormato().estaTerminado(torneo)) {
			torneo.setEstado(EstadoTorneo.FINALIZADO);
			bonos = torneo.getTipo().crearBonos(torneo, torneo.getGanador(), club.siguienteIdBono());
			for (BonoDescuento bono : bonos) {
				club.agregarBono(bono);
			}
		}
		return bonos;
	}

	// HU_T9: consultar torneos e historial

	/**
	 * HU_T9. Torneos del club en un estado, o todos si el estado es null. Cualquier usuario.
	 */
	public List<Torneo> consultarTorneos(Usuario quien, EstadoTorneo estado) {
		List<Torneo> encontrados = new ArrayList<Torneo>();
		for (Torneo torneo : club.getTorneos()) {
			if (estado == null || torneo.getEstado() == estado) {
				encontrados.add(torneo);
			}
		}
		return encontrados;
	}

	/**
	 * HU_T9. Posiciones del torneo en cualquier estado. Cualquier usuario.
	 */
	public List<FilaTorneo> consultarPosiciones(Usuario quien, Torneo torneo) {
		return torneo.calcularPosiciones();
	}

	/**
	 * HU_T3. Torneos con inscripciones abiertas donde este usuario se puede inscribir
	 * (los individuales; los de conjunto los inscribe el entrenador).
	 */
	public List<Torneo> consultarTorneosAbiertosIndividuales(Usuario quien) {
		List<Torneo> abiertos = new ArrayList<Torneo>();
		for (Torneo torneo : club.getTorneos()) {
			if (torneo.estaAbierto() && torneo instanceof TorneoIndividual) {
				abiertos.add(torneo);
			}
		}
		return abiertos;
	}

	/**
	 * HU_T5 y HU_T9. Torneos donde juega el usuario, como jugador o en uno de los equipos que entrena.
	 */
	public List<Torneo> consultarMisTorneos(Usuario quien) {
		List<Torneo> mios = new ArrayList<Torneo>();
		for (Torneo torneo : club.getTorneos()) {
			if (torneo.buscarInscripcionDe(quien) != null) {
				mios.add(torneo);
			} else {
				for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
					if (inscripcion instanceof InscripcionEquipo
							&& ((InscripcionEquipo) inscripcion).getEquipo().getEntrenador() == quien
							&& !mios.contains(torneo)) {
						mios.add(torneo);
					}
				}
			}
		}
		return mios;
	}

	// HU_T10: bonos

	/**
	 * HU_T10. Bonos que ha ganado el usuario, usados y sin usar.
	 */
	public List<BonoDescuento> consultarBonos(Usuario quien) {
		return club.getBonosDe(quien);
	}
}
