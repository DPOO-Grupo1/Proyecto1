package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.EstadoInvalidoException;
import excepciones.JugadorNoHabilitadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.competencias.Competencia;
import modelo.competencias.EquipoExterno;
import modelo.competencias.EquipoInscrito;
import modelo.competencias.FilaClasificacion;
import modelo.competencias.FilaTabla;
import modelo.competencias.Formato;
import modelo.competencias.ParticipanteCompetencia;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.PartidoCompetencia;
import modelo.usuarios.Usuario;

/**
 * Maneja las competencias externas: creación, inscripción de equipos, partidos
 * oficiales, tabla de posiciones y clasificaciones. Cubre RF2, RF25, RF26 y RF27.
 * Todo lo que modifica datos lo hace el administrador; las consultas las puede hacer cualquiera.
 */
public class GestorCompetencias extends Gestor {

	public GestorCompetencias(Club club) {
		super(club);
	}

	// ---------- RF25: crear competencias ----------

	/**
	 * RF25. Crea una competencia con su formato (Liga o GruposEliminacion). Solo el administrador.
	 * No puede haber dos con el mismo nombre en la misma temporada.
	 */
	public Competencia crearCompetencia(Usuario quien, String nombre, String entidad, int temporada,
			DeporteEquipo deporte, Categoria categoria, int nominaMinima, int amarillasParaSuspension, Formato formato)
			throws PermisoDenegadoException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);

		if (club.existeCompetencia(nombre, temporada)) {
			throw new DuplicadoException("Ya existe la competencia '" + nombre + "' en la temporada " + temporada);
		}
		if (temporada <= 0 || nominaMinima <= 0 || amarillasParaSuspension <= 0) {
			throw new DatoInvalidoException("La temporada, la nómina mínima y las amarillas para suspensión deben ser positivas");
		}

		Competencia competencia = new Competencia(nombre, entidad, temporada, deporte, categoria, nominaMinima,
				amarillasParaSuspension, formato);
		club.agregarCompetencia(competencia);
		return competencia;
	}

	/**
	 * RF25. Agrega un rival externo a una competencia. Solo el administrador.
	 * En grupos hay que darle un grupo válido; en liga el grupo va en null.
	 */
	public EquipoExterno agregarRival(Usuario quien, Competencia competencia, String nombre, String institucion,
			String grupo) throws PermisoDenegadoException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);

		if (competencia.tieneParticipante(nombre)) {
			throw new DuplicadoException("'" + nombre + "' ya participa en " + competencia.getNombre());
		}
		verificarGrupo(competencia, grupo);

		EquipoExterno rival = new EquipoExterno(nombre, institucion, grupo);
		competencia.agregarParticipante(rival);
		return rival;
	}

	// ---------- RF26: inscribir equipos ----------

	/**
	 * RF26. Inscribe un equipo del club con la nómina que se escoja (números de camiseta).
	 * Solo el administrador. El equipo debe estar activo y ser del deporte y la categoría de la
	 * competencia. Cada número debe ser de un jugador activo del equipo y la nómina debe
	 * tener al menos la nómina mínima; si no, se informa cuántos faltan.
	 */
	public EquipoInscrito inscribirEquipo(Usuario quien, Competencia competencia, String nombreEquipo,
			List<Integer> numerosNomina, String grupo) throws PermisoDenegadoException, NoEncontradoException,
			EstadoInvalidoException, ReglaDeportivaException, DuplicadoException, DatoInvalidoException {
		verificarAdministrador(quien);

		Equipo equipo = club.buscarEquipo(nombreEquipo);

		if (!equipo.estaActivo()) {
			throw new EstadoInvalidoException("El equipo " + equipo.getNombre() + " está inactivo");
		}
		if (equipo.getDeporte() != competencia.getDeporte()) {
			throw new ReglaDeportivaException(equipo.getNombre() + " es de " + equipo.getDeporte().getNombre()
					+ " y la competencia es de " + competencia.getDeporte().getNombre());
		}
		if (equipo.getCategoria() != competencia.getCategoria()) {
			throw new ReglaDeportivaException("La categoría " + equipo.getCategoria().getNombre()
					+ " no está habilitada en " + competencia.getNombre() + " (es " + competencia.getCategoria().getNombre() + ")");
		}
		if (competencia.getParticipanteDe(equipo) != null || competencia.tieneParticipante(equipo.getNombre())) {
			throw new DuplicadoException(equipo.getNombre() + " ya está inscrito en " + competencia.getNombre());
		}
		verificarGrupo(competencia, grupo);

		List<JugadorEquipo> nomina = new ArrayList<JugadorEquipo>();
		for (Integer numero : numerosNomina) {
			JugadorEquipo jugador = equipo.buscarJugador(numero);
			if (nomina.contains(jugador)) {
				throw new DatoInvalidoException("El número " + numero + " está repetido en la nómina");
			}
			nomina.add(jugador);
		}
		int habilitados = 0;
		for (JugadorEquipo jugador : nomina) {
			if (jugador.estaHabilitado()) {
				habilitados++;
			}
		}
		if (habilitados < competencia.getNominaMinima()) {
			int faltan = competencia.getNominaMinima() - habilitados;
			throw new ReglaDeportivaException("La nómina tiene " + habilitados + " habilitados y el mínimo es "
					+ competencia.getNominaMinima() + ": faltan " + faltan);
		}

		EquipoInscrito inscrito = new EquipoInscrito(equipo, nomina, grupo, club.getFechaActual());
		competencia.agregarParticipante(inscrito);
		return inscrito;
	}

	// ---------- RF27: partidos oficiales ----------

	/**
	 * RF27. Programa un partido entre dos participantes de la competencia. Solo el administrador.
	 * Puede ser entre dos rivales externos.
	 */
	public PartidoCompetencia programarPartido(Usuario quien, Competencia competencia, String nombreLocal,
			String nombreVisitante, LocalDate fecha, String escenario, String fase)
			throws PermisoDenegadoException, NoEncontradoException, ReglaDeportivaException {
		verificarAdministrador(quien);

		ParticipanteCompetencia local = competencia.buscarParticipante(nombreLocal);
		ParticipanteCompetencia visitante = competencia.buscarParticipante(nombreVisitante);

		if (local == visitante) {
			throw new ReglaDeportivaException("Un partido enfrenta a dos participantes distintos");
		}

		PartidoCompetencia partido = new PartidoCompetencia(club.siguienteIdPartido(), fecha, competencia, local,
				visitante, escenario, fase);
		competencia.agregarPartido(partido);
		return partido;
	}

	/**
	 * RF27. Registra el marcador de un partido oficial. Solo el administrador.
	 * Mismas reglas que en los internos: una sola vez, sin negativos y sin empates si el deporte
	 * no los admite. Al final se cumplen las suspensiones de esa competencia.
	 */
	public void registrarResultado(Usuario quien, PartidoCompetencia partido, int local, int visitante)
			throws PermisoDenegadoException, EstadoInvalidoException, DatoInvalidoException,
			ReglaDeportivaException {
		verificarAdministrador(quien);

		if (partido.isJugado()) {
			throw new EstadoInvalidoException("El resultado del partido #" + partido.getId() + " ya fue registrado");
		}
		if (local < 0 || visitante < 0) {
			throw new DatoInvalidoException("El marcador no admite valores negativos");
		}
		DeporteEquipo deporte = partido.getCompetencia().getDeporte();
		if (local == visitante && !deporte.isAdmiteEmpate()) {
			throw new ReglaDeportivaException("En " + deporte.getNombre() + " no hay empates");
		}

		partido.registrarResultado(local, visitante);
		cumplirSuspensiones(partido);
	}

	/**
	 * RF27. Registra la estadística de un jugador del club en un partido oficial. Solo el administrador.
	 * El jugador debe estar en la nómina y no estar suspendido en esa competencia.
	 * En fútbol, una roja o dos amarillas suspenden, y también llegar al límite de amarillas acumuladas.
	 */
	public void registrarEstadistica(Usuario quien, PartidoCompetencia partido, JugadorEquipo jugador,
			Estadistica estadistica) throws PermisoDenegadoException, DatoInvalidoException,
			ReglaDeportivaException, JugadorNoHabilitadoException, DuplicadoException {
		verificarAdministrador(quien);

		if (estadistica.getJugador() != jugador) {
			throw new DatoInvalidoException("La estadística no corresponde a " + jugador.getSocio().getNombreCompleto());
		}
		ParticipanteCompetencia participante = partido.getParticipanteDe(jugador.getEquipo());
		if (participante == null) {
			throw new ReglaDeportivaException(jugador.getEquipo().getNombre() + " no juega el partido #" + partido.getId());
		}
		if (!participante.estaEnNomina(jugador)) {
			throw new JugadorNoHabilitadoException(jugador.getSocio().getNombreCompleto() + " no está en la nómina de "
					+ participante.getNombre());
		}
		String motivoNoHabilitado = jugador.motivoNoHabilitadoPara(partido);
		if (motivoNoHabilitado != null) {
			throw new JugadorNoHabilitadoException(motivoNoHabilitado);
		}
		Competencia competencia = partido.getCompetencia();
		if (!competencia.getDeporte().admiteEstadistica(estadistica)) {
			throw new ReglaDeportivaException("La estadística es de " + estadistica.getNombreDeporte()
					+ " y la competencia es de " + competencia.getDeporte().getNombre());
		}
		if (partido.tieneEstadisticaDe(jugador)) {
			throw new DuplicadoException(jugador.getSocio().getNombreCompleto() + " ya tiene estadística en el partido #"
					+ partido.getId());
		}

		partido.agregarEstadistica(estadistica);
		if (estadistica instanceof EstadisticaFutbol) {
			String motivo = ((EstadisticaFutbol) estadistica).motivoSuspension();
			if (motivo != null) {
				jugador.agregarSuspension(new Suspension(motivo, partido));
			}
			acumularAmarillas(competencia, jugador, partido);
		}
	}

	// ---------- RF2: consultas ----------

	/**
	 * RF2. Lista las competencias, con filtro opcional por deporte y por categoría (null = sin filtro).
	 * Si no hay ninguna, devuelve la lista vacía.
	 */
	public List<Competencia> consultarCompetencias(Usuario quien, String deporte, String categoria) {
		List<Competencia> resultado = new ArrayList<Competencia>();
		for (Competencia competencia : club.getCompetencias()) {
			boolean deporteSirve = deporte == null || competencia.getDeporte().getNombre().equalsIgnoreCase(deporte);
			boolean categoriaSirve = categoria == null
					|| competencia.getCategoria().getNombre().equalsIgnoreCase(categoria);
			if (deporteSirve && categoriaSirve) {
				resultado.add(competencia);
			}
		}
		return resultado;
	}

	/**
	 * RF2. Busca una competencia para consultarla.
	 */
	public Competencia consultarCompetencia(Usuario quien, String nombre, int temporada) throws NoEncontradoException {
		return club.buscarCompetencia(nombre, temporada);
	}

	/**
	 * RF2. Tabla de posiciones de la competencia, o de un grupo (null = todos).
	 */
	public List<FilaTabla> consultarTabla(Usuario quien, Competencia competencia, String grupo)
			throws DatoInvalidoException {
		if (grupo != null) {
			verificarGrupo(competencia, grupo);
		}
		return competencia.calcularTabla(grupo);
	}

	/**
	 * RF2. Participantes que clasifican, según el formato de la competencia.
	 */
	public List<ParticipanteCompetencia> consultarClasificados(Usuario quien, Competencia competencia) {
		return competencia.getFormato().getClasificados(competencia);
	}

	/**
	 * RF2. Goleadores de la competencia, de mayor a menor. Solo para fútbol.
	 * Solo cuenta jugadores del club, porque de los externos no se registran estadísticas.
	 */
	public List<FilaClasificacion> consultarGoleadores(Usuario quien, Competencia competencia)
			throws ReglaDeportivaException {
		if (!competencia.getDeporte().getNombre().equalsIgnoreCase(EstadisticaFutbol.DEPORTE)) {
			throw new ReglaDeportivaException("La tabla de goleadores solo existe en fútbol");
		}

		List<FilaClasificacion> filas = new ArrayList<FilaClasificacion>();
		for (PartidoCompetencia partido : competencia.getPartidos()) {
			for (Estadistica estadistica : partido.getEstadisticas()) {
				int goles = ((EstadisticaFutbol) estadistica).getGoles();
				filaDe(filas, estadistica.getJugador()).sumar(goles);
			}
		}
		return ordenarDeMayorAMenor(filas);
	}

	/**
	 * RF2. Jugadores destacados de la competencia, de mayor a menor.
	 * Cada deporte decide qué cuenta (asistencias, puntos o aces) con valorDestacado().
	 */
	public List<FilaClasificacion> consultarDestacados(Usuario quien, Competencia competencia) {
		List<FilaClasificacion> filas = new ArrayList<FilaClasificacion>();
		for (PartidoCompetencia partido : competencia.getPartidos()) {
			for (Estadistica estadistica : partido.getEstadisticas()) {
				filaDe(filas, estadistica.getJugador()).sumar(estadistica.valorDestacado());
			}
		}
		return ordenarDeMayorAMenor(filas);
	}

	// ---------- Métodos privados ----------

	private void verificarGrupo(Competencia competencia, String grupo) throws DatoInvalidoException {
		if (!competencia.getFormato().grupoValido(grupo)) {
			throw new DatoInvalidoException("El grupo '" + grupo + "' no es válido para el formato "
					+ competencia.getFormato());
		}
	}

	// RF27: al llegar al límite de amarillas de la competencia, se suspende para el siguiente oficial.
	private void acumularAmarillas(Competencia competencia, JugadorEquipo jugador, PartidoCompetencia partido) {
		if (competencia.contarAmarillasDesdeUltimaSancion(jugador) >= competencia.getAmarillasParaSuspension()) {
			jugador.agregarSuspension(new Suspension(Suspension.MOTIVO_ACUMULACION, partido));
		}
	}

	// Los de la nómina que tenían suspensión para este partido y no lo jugaron, ya la cumplieron.
	private void cumplirSuspensiones(PartidoCompetencia partido) {
		List<JugadorEquipo> jugadores = new ArrayList<JugadorEquipo>();
		jugadores.addAll(partido.getLocal().getNomina());
		jugadores.addAll(partido.getVisitante().getNomina());
		for (JugadorEquipo jugador : jugadores) {
			Suspension suspension = jugador.getSuspensionPendientePara(partido);
			if (suspension != null && !partido.tieneEstadisticaDe(jugador)) {
				suspension.cumplir(partido);
			}
		}
	}

	private FilaClasificacion filaDe(List<FilaClasificacion> filas, JugadorEquipo jugador) {
		for (FilaClasificacion fila : filas) {
			if (fila.getJugador() == jugador) {
				return fila;
			}
		}
		FilaClasificacion nueva = new FilaClasificacion(jugador);
		filas.add(nueva);
		return nueva;
	}

	// Deja solo a los que tienen valor mayor a 0 y los ordena por inserción, de mayor a menor.
	private List<FilaClasificacion> ordenarDeMayorAMenor(List<FilaClasificacion> filas) {
		List<FilaClasificacion> ordenadas = new ArrayList<FilaClasificacion>();
		for (FilaClasificacion fila : filas) {
			if (fila.getValor() > 0) {
				int i = 0;
				while (i < ordenadas.size() && ordenadas.get(i).getValor() >= fila.getValor()) {
					i++;
				}
				ordenadas.add(i, fila);
			}
		}
		return ordenadas;
	}
}
