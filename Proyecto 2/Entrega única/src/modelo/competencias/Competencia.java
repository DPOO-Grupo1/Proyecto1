package modelo.competencias;

import java.util.ArrayList;
import java.util.List;

import excepciones.NoEncontradoException;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.PartidoCompetencia;

/**
 * Competencia externa (ASCUN, Torneo de los Cerros...).
 * La tabla no se guarda, se calcula con los partidos. Las suspensiones quedan en los jugadores.
 */
public class Competencia {

	private String nombre;
	private String entidadOrganizadora;
	private int temporada;
	private int nominaMinima;
	private int amarillasParaSuspension;
	private DeporteEquipo deporte;
	private Categoria categoria;
	private Formato formato;
	private List<ParticipanteCompetencia> participantes;
	private List<PartidoCompetencia> partidos;

	public Competencia(String nombre, String entidadOrganizadora, int temporada, DeporteEquipo deporte,
			Categoria categoria, int nominaMinima, int amarillasParaSuspension, Formato formato) {
		this.nombre = nombre;
		this.entidadOrganizadora = entidadOrganizadora;
		this.temporada = temporada;
		this.deporte = deporte;
		this.categoria = categoria;
		this.nominaMinima = nominaMinima;
		this.amarillasParaSuspension = amarillasParaSuspension;
		this.formato = formato;
		this.participantes = new ArrayList<ParticipanteCompetencia>();
		this.partidos = new ArrayList<PartidoCompetencia>();
	}

	public String getNombre() {
		return nombre;
	}

	public String getEntidadOrganizadora() {
		return entidadOrganizadora;
	}

	public int getTemporada() {
		return temporada;
	}

	public int getNominaMinima() {
		return nominaMinima;
	}

	public int getAmarillasParaSuspension() {
		return amarillasParaSuspension;
	}

	public DeporteEquipo getDeporte() {
		return deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public Formato getFormato() {
		return formato;
	}

	// Participantes
	public List<ParticipanteCompetencia> getParticipantes() {
		return participantes;
	}

	public void agregarParticipante(ParticipanteCompetencia participante) {
		participantes.add(participante);
	}

	public boolean tieneParticipante(String nombre) {
		for (ParticipanteCompetencia participante : participantes) {
			if (participante.getNombre().equalsIgnoreCase(nombre)) {
				return true;
			}
		}
		return false;
	}

	public ParticipanteCompetencia buscarParticipante(String nombre) throws NoEncontradoException {
		for (ParticipanteCompetencia participante : participantes) {
			if (participante.getNombre().equalsIgnoreCase(nombre)) {
				return participante;
			}
		}
		throw new NoEncontradoException("'" + nombre + "' no participa en " + this.nombre);
	}

	/**
	 * Null si el equipo no está inscrito.
	 */
	public ParticipanteCompetencia getParticipanteDe(Equipo equipo) {
		for (ParticipanteCompetencia participante : participantes) {
			if (participante.representa(equipo)) {
				return participante;
			}
		}
		return null;
	}

	// Partidos
	public List<PartidoCompetencia> getPartidos() {
		return partidos;
	}

	public void agregarPartido(PartidoCompetencia partido) {
		partidos.add(partido);
	}

	// Cálculos
	/**
	 * Calcula la tabla con los partidos jugados. Si el grupo es null, incluye a todos.
	 */
	public List<FilaTabla> calcularTabla(String grupo) {
		List<FilaTabla> filas = new ArrayList<FilaTabla>();
		for (ParticipanteCompetencia participante : participantes) {
			if (grupo == null || grupo.equalsIgnoreCase(participante.getGrupo())) {
				filas.add(new FilaTabla(participante));
			}
		}
		for (PartidoCompetencia partido : partidos) {
			if (partido.isJugado()) {
				FilaTabla filaLocal = buscarFila(filas, partido.getLocal());
				FilaTabla filaVisitante = buscarFila(filas, partido.getVisitante());
				if (filaLocal != null && filaVisitante != null) {
					int local = partido.getMarcadorLocal();
					int visitante = partido.getMarcadorVisitante();
					filaLocal.registrarPartido(local, visitante, deporte.puntosPara(local, visitante));
					filaVisitante.registrarPartido(visitante, local, deporte.puntosPara(visitante, local));
				}
			}
		}
		formato.ordenar(filas, this);
		return filas;
	}

	private FilaTabla buscarFila(List<FilaTabla> filas, ParticipanteCompetencia participante) {
		for (FilaTabla fila : filas) {
			if (fila.getParticipante() == participante) {
				return fila;
			}
		}
		return null;
	}

	/**
	 * Puntos que sacó a en sus partidos contra b. Sirve para desempatar.
	 */
	public int puntosEnfrentamientoDirecto(ParticipanteCompetencia a, ParticipanteCompetencia b) {
		int puntos = 0;
		for (PartidoCompetencia partido : partidos) {
			if (partido.isJugado()) {
				if (partido.getLocal() == a && partido.getVisitante() == b) {
					puntos += deporte.puntosPara(partido.getMarcadorLocal(), partido.getMarcadorVisitante());
				} else if (partido.getLocal() == b && partido.getVisitante() == a) {
					puntos += deporte.puntosPara(partido.getMarcadorVisitante(), partido.getMarcadorLocal());
				}
			}
		}
		return puntos;
	}

	/**
	 * Amarillas del jugador en esta competencia que todavía no han generado
	 * una suspensión por acumulación.
	 */
	public int contarAmarillasDesdeUltimaSancion(JugadorEquipo jugador) {
		int amarillas = 0;
		for (PartidoCompetencia partido : partidos) {
			Estadistica estadistica = partido.getEstadisticaDe(jugador);
			if (estadistica instanceof EstadisticaFutbol) {
				amarillas += ((EstadisticaFutbol) estadistica).getAmarillas();
			}
		}
		int sanciones = 0;
		for (Suspension suspension : jugador.getSuspensiones()) {
			if (suspension.getMotivo().equals(Suspension.MOTIVO_ACUMULACION)
					&& partidos.contains(suspension.getOrigen())) {
				sanciones++;
			}
		}
		return amarillas - sanciones * amarillasParaSuspension;
	}

	@Override
	public String toString() {
		return nombre + " " + temporada + " (" + deporte.getNombre() + ", " + categoria.getNombre() + ", " + formato
				+ ")";
	}
}
