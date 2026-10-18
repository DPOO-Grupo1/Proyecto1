package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.CupoExcedidoException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.Partido;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Maneja los equipos, jugadores, partidos internos, estadísticas y suspensiones.
 * Cubre RF1, RF12, RF13, RF14, RF22, RF23 y RF24.
 */
public class GestorDeportes extends Gestor {

	public GestorDeportes(Club club) {
		super(club);
	}

	// RF22: crear equipos
	/**
	 * RF22. Crea un equipo sin jugadores. Solo el administrador.
	 * Se rechaza si ya hay un equipo con ese nombre o si el cupo es menor
	 * al mínimo de habilitados del deporte.
	 */
	public Equipo crearEquipo(Usuario quien, String nombre, DeporteEquipo deporte, Categoria categoria, int cupo)
			throws PermisoDenegadoException, DuplicadoException, CupoExcedidoException {
		verificarAdministrador(quien);

		if (club.existeEquipo(nombre)) {
			throw new DuplicadoException("Ya existe un equipo llamado '" + nombre + "'");
		}
		if (cupo < deporte.getMinimoHabilitados()) {
			throw new CupoExcedidoException("El cupo (" + cupo + ") es menor al mínimo de habilitados de "
					+ deporte.getNombre() + " (" + deporte.getMinimoHabilitados() + ")");
		}

		Equipo equipo = new Equipo(nombre, deporte, categoria, cupo);
		club.agregarEquipo(equipo);
		return equipo;
	}

	/**
	 * RF22. Le asigna un entrenador a un equipo. Solo el administrador.
	 */
	public void asignarEntrenador(Usuario quien, String nombreEquipo, int idEntrenador)
			throws PermisoDenegadoException, NoEncontradoException {
		verificarAdministrador(quien);

		Equipo equipo = club.buscarEquipo(nombreEquipo);
		Empleado empleado = club.buscarEmpleado(idEntrenador);
		if (!(empleado instanceof Entrenador)) {
			throw new NoEncontradoException("El empleado " + idEntrenador + " no es entrenador");
		}

		equipo.setEntrenador((Entrenador) empleado);
	}

	// RF23: inscribir jugadores
	/**
	 * RF23. Inscribe a un socio en un equipo con número y posición. Solo el administrador.
	 * Revisa que el equipo esté activo y tenga cupo, que la edad sirva para la categoría,
	 * que el socio no esté en otro equipo del mismo deporte y que el número esté libre.
	 */
	public JugadorEquipo inscribirJugador(Usuario quien, String socioID, String nombreEquipo, int numero,
			String posicion) throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException,
			EstadoInvalidoException, CupoExcedidoException, ReglaDeportivaException, DuplicadoException {
		verificarAdministrador(quien);

		Socio socio = club.buscarSocio(socioID);
		Equipo equipo = club.buscarEquipo(nombreEquipo);

		if (numero <= 0) {
			throw new DatoInvalidoException("El número de camiseta debe ser mayor a 0");
		}
		if (!equipo.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + equipo.getNombre() + " está inactivo");
		}
		if (!equipo.tieneCupo()) {
			throw new CupoExcedidoException("El equipo " + equipo.getNombre() + " tiene el cupo lleno ("
					+ equipo.getCupoMaximo() + " jugadores activos)");
		}
		Categoria categoria = equipo.getCategoria();
		if (!categoria.admite(socio, club.getFechaActual())) {
			throw new ReglaDeportivaException("La edad de " + socio.getNombreCompleto()
					+ " no corresponde a la categoría " + categoria.getNombre());
		}
		if (socio.juegaEnDeporte(equipo.getDeporte())) {
			throw new ReglaDeportivaException(socio.getNombreCompleto() + " ya juega en otro equipo de "
					+ equipo.getDeporte().getNombre());
		}
		if (!equipo.numeroDisponible(numero)) {
			throw new DuplicadoException("El número " + numero + " ya está en uso en " + equipo.getNombre());
		}

		JugadorEquipo jugador = new JugadorEquipo(socio, equipo, numero, posicion);
		equipo.agregarJugador(jugador);
		socio.agregarParticipacion(jugador);
		return jugador;
	}

	// RF24: bajas
	/**
	 * RF24. Da de baja un equipo que no tenga partidos pendientes. Solo el administrador.
	 * Sus jugadores quedan inactivos, pero no se borra nada.
	 */
	public void darDeBajaEquipo(Usuario quien, String nombreEquipo)
			throws PermisoDenegadoException, NoEncontradoException, EstadoInvalidoException {
		verificarAdministrador(quien);

		Equipo equipo = club.buscarEquipo(nombreEquipo);

		if (!equipo.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + equipo.getNombre() + " ya está inactivo");
		}
		if (tieneCompromisosPendientes(equipo)) {
			throw new EstadoInvalidoException("El equipo " + equipo.getNombre()
					+ " tiene compromisos pendientes (partidos sin jugar o competencias vigentes)");
		}

		equipo.desactivar();
	}

	/**
	 * RF24. Retira a un jugador de un equipo y deja libre su número. Solo el administrador.
	 */
	public void retirarJugador(Usuario quien, String nombreEquipo, int numero)
			throws PermisoDenegadoException, NoEncontradoException {
		verificarAdministrador(quien);

		Equipo equipo = club.buscarEquipo(nombreEquipo);
		JugadorEquipo jugador = equipo.buscarJugador(numero);

		jugador.desactivar();
	}

	// RF12: programar partidos internos
	/**
	 * RF12. Programa un partido interno. Solo un entrenador.
	 * Los dos equipos deben ser distintos, estar activos, ser del mismo deporte y categoría
	 * y tener el mínimo de habilitados. La fecha tiene que ser futura.
	 */
	public PartidoInterno programarPartidoInterno(Usuario quien, String nombreLocal, String nombreVisitante,
			LocalDate fecha, String proposito) throws PermisoDenegadoException, NoEncontradoException,
			EstadoInvalidoException, ReglaDeportivaException, DatoInvalidoException {
		verificarEntrenador(quien);

		Equipo local = club.buscarEquipo(nombreLocal);
		Equipo visitante = club.buscarEquipo(nombreVisitante);

		if (local == visitante) {
			throw new ReglaDeportivaException("Un partido interno enfrenta a dos equipos distintos");
		}
		if (!local.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + local.getNombre() + " está inactivo");
		}
		if (!visitante.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + visitante.getNombre() + " está inactivo");
		}
		if (local.getDeporte() != visitante.getDeporte()) {
			throw new ReglaDeportivaException("Los equipos son de deportes distintos ("
					+ local.getDeporte().getNombre() + " y " + visitante.getDeporte().getNombre() + ")");
		}
		if (local.getCategoria() != visitante.getCategoria()) {
			throw new ReglaDeportivaException("Los equipos son de categorías distintas ("
					+ local.getCategoria().getNombre() + " y " + visitante.getCategoria().getNombre() + ")");
		}
		if (!fecha.isAfter(club.getFechaActual())) {
			throw new DatoInvalidoException("La fecha " + fecha + " no es futura (hoy es " + club.getFechaActual() + ")");
		}

		// El partido se crea antes de contar habilitados porque las suspensiones dependen del partido.
		// Solo se agrega al club si pasa, así que un rechazo no gasta un id.
		PartidoInterno partido = new PartidoInterno(club.siguienteIdPartido(), fecha, local, visitante, proposito);
		verificarMinimoHabilitados(local, partido);
		verificarMinimoHabilitados(visitante, partido);

		club.agregarPartidoInterno(partido);
		return partido;
	}

	// RF13 y RF14: resultados, estadísticas y tarjetas
	/**
	 * RF13. Registra el marcador de un partido interno (en voleibol es por sets). Solo un entrenador.
	 * No se puede registrar dos veces, ni con marcadores negativos, ni empates si el deporte no los admite.
	 * Al final se cumplen las suspensiones de los que no pudieron jugar.
	 */
	public void registrarResultadoInterno(Usuario quien, PartidoInterno partido, int local, int visitante)
			throws PermisoDenegadoException, EstadoInvalidoException, DatoInvalidoException,
			ReglaDeportivaException {
		verificarEntrenador(quien);

		if (partido.isJugado()) {
			throw new EstadoInvalidoException("El resultado del partido #" + partido.getId() + " ya fue registrado");
		}
		if (local < 0 || visitante < 0) {
			throw new DatoInvalidoException("El marcador no admite valores negativos");
		}
		DeporteEquipo deporte = partido.getLocal().getDeporte();
		if (local == visitante && !deporte.isAdmiteEmpate()) {
			throw new ReglaDeportivaException("En " + deporte.getNombre() + " no hay empates");
		}

		partido.registrarResultado(local, visitante);
		cumplirSuspensiones(partido);
	}

	/**
	 * RF13 y RF14. Registra la estadística de un jugador en un partido interno. Solo un entrenador.
	 * El jugador debe ser de uno de los dos equipos y estar habilitado, y la estadística
	 * debe ser del deporte del partido. En fútbol se revisan las tarjetas.
	 */
	public void registrarEstadistica(Usuario quien, PartidoInterno partido, JugadorEquipo jugador,
			Estadistica estadistica) throws PermisoDenegadoException, DatoInvalidoException,
			ReglaDeportivaException, JugadorNoHabilitadoException, DuplicadoException {
		verificarEntrenador(quien);

		if (estadistica.getJugador() != jugador) {
			throw new DatoInvalidoException("La estadística no corresponde a " + jugador.getSocio().getNombreCompleto());
		}
		Equipo equipo = jugador.getEquipo();
		if (!partido.participa(equipo)) {
			throw new ReglaDeportivaException(jugador.getSocio().getNombreCompleto() + " no es de los equipos que disputan el partido #"
					+ partido.getId());
		}
		String motivoNoHabilitado = jugador.motivoNoHabilitadoPara(partido);
		if (motivoNoHabilitado != null) {
			throw new JugadorNoHabilitadoException(motivoNoHabilitado);
		}
		DeporteEquipo deporte = equipo.getDeporte();
		if (!deporte.admiteEstadistica(estadistica)) {
			throw new ReglaDeportivaException("La estadística es de " + estadistica.getNombreDeporte()
					+ " y el partido es de " + deporte.getNombre());
		}
		if (partido.tieneEstadisticaDe(jugador)) {
			throw new DuplicadoException(jugador.getSocio().getNombreCompleto() + " ya tiene estadística en el partido #"
					+ partido.getId());
		}

		partido.agregarEstadistica(estadistica);
		// RF14: una roja o dos amarillas suspenden para el siguiente partido interno.
		if (estadistica instanceof EstadisticaFutbol) {
			String motivo = ((EstadisticaFutbol) estadistica).motivoSuspension();
			if (motivo != null) {
				jugador.agregarSuspension(new Suspension(motivo, partido));
			}
		}
	}

	// RF1: información deportiva
	/**
	 * RF1. Devuelve los equipos en que juega o jugó el socio que consulta.
	 */
	public List<JugadorEquipo> consultarInformacion(Usuario quien) throws PermisoDenegadoException {
		verificarSocio(quien);
		Socio socio = (Socio) quien;
		return socio.getParticipaciones();
	}

	/**
	 * RF1. Devuelve los partidos del equipo de una de las participaciones del socio.
	 * Un socio no puede consultar la de otro.
	 */
	public List<Partido> consultarPartidosDe(Usuario quien, JugadorEquipo jugador) throws PermisoDenegadoException {
		verificarSocio(quien);
		if (jugador.getSocio() != quien) {
			throw new PermisoDenegadoException("Un socio solo consulta su propia información deportiva");
		}

		return club.getPartidosDe(jugador.getEquipo());
	}

	// Métodos privados
	private void verificarMinimoHabilitados(Equipo equipo, Partido partido) throws ReglaDeportivaException {
		int habilitados = equipo.contarHabilitadosPara(partido);
		int minimo = equipo.getDeporte().getMinimoHabilitados();
		if (habilitados < minimo) {
			throw new ReglaDeportivaException("El equipo " + equipo.getNombre() + " tiene " + habilitados
					+ " habilitados y " + equipo.getDeporte().getNombre() + " exige " + minimo);
		}
	}

	// RF14: los que tenían suspensión para este partido y no lo jugaron, ya la cumplieron.
	private void cumplirSuspensiones(PartidoInterno partido) {
		List<JugadorEquipo> jugadores = new ArrayList<JugadorEquipo>();
		jugadores.addAll(partido.getLocal().getJugadores());
		jugadores.addAll(partido.getVisitante().getJugadores());
		for (JugadorEquipo jugador : jugadores) {
			Suspension suspension = jugador.getSuspensionPendientePara(partido);
			if (suspension != null && !partido.tieneEstadisticaDe(jugador)) {
				suspension.cumplir(partido);
			}
		}
	}

	// El equipo no conoce sus partidos, por eso esto se busca en el club.
	private boolean tieneCompromisosPendientes(Equipo equipo) {
		for (Partido partido : club.getPartidosDe(equipo)) {
			if (!partido.isJugado()) {
				return true;
			}
		}
		return false;
	}
}
