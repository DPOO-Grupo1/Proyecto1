package persistencia;

import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.EstadisticaVoleibol;
import modelo.partidos.Partido;

/**
 * Lo que comparten PersistenciaDeportes y PersistenciaCompetencias: guardar y cargar
 * el resultado, las estadísticas y las suspensiones de un partido.
 * Las suspensiones se guardan dentro del partido donde nacieron.
 */
public abstract class PersistenciaPartidos implements IPersistenciaArea {

	protected ArchivoJSON archivoJSON = new ArchivoJSON();

	// ---------- Guardar ----------

	protected JSONObject partidoAJSON(Partido partido, Club club) {
		JSONObject jp = new JSONObject();
		jp.put("id", partido.getId());
		jp.put("fecha", partido.getFecha().toString());
		jp.put("jugado", partido.isJugado());
		jp.put("marcadorLocal", partido.getMarcadorLocal());
		jp.put("marcadorVisitante", partido.getMarcadorVisitante());

		JSONArray estadisticas = new JSONArray();
		for (Estadistica estadistica : partido.getEstadisticas()) {
			estadisticas.put(estadisticaAJSON(estadistica));
		}
		jp.put("estadisticas", estadisticas);

		JSONArray suspensiones = new JSONArray();
		for (Equipo equipo : club.getEquipos()) {
			for (JugadorEquipo jugador : equipo.getJugadores()) {
				for (Suspension suspension : jugador.getSuspensiones()) {
					if (suspension.getOrigen() == partido) {
						suspensiones.put(suspensionAJSON(suspension, jugador));
					}
				}
			}
		}
		jp.put("suspensiones", suspensiones);
		return jp;
	}

	private JSONObject estadisticaAJSON(Estadistica estadistica) {
		JSONObject je = new JSONObject();
		je.put("equipo", estadistica.getJugador().getEquipo().getNombre());
		je.put("socioID", estadistica.getJugador().getSocio().getSocioID());
		if (estadistica instanceof EstadisticaFutbol) {
			EstadisticaFutbol futbol = (EstadisticaFutbol) estadistica;
			je.put("goles", futbol.getGoles());
			je.put("asistencias", futbol.getAsistencias());
			je.put("amarillas", futbol.getAmarillas());
			je.put("rojas", futbol.getRojas());
		} else if (estadistica instanceof EstadisticaBaloncesto) {
			EstadisticaBaloncesto baloncesto = (EstadisticaBaloncesto) estadistica;
			je.put("puntos", baloncesto.getPuntos());
			je.put("rebotes", baloncesto.getRebotes());
		} else if (estadistica instanceof EstadisticaVoleibol) {
			EstadisticaVoleibol voleibol = (EstadisticaVoleibol) estadistica;
			je.put("aces", voleibol.getAces());
			je.put("bloqueos", voleibol.getBloqueos());
		}
		return je;
	}

	private JSONObject suspensionAJSON(Suspension suspension, JugadorEquipo jugador) {
		JSONObject js = new JSONObject();
		js.put("equipo", jugador.getEquipo().getNombre());
		js.put("socioID", jugador.getSocio().getSocioID());
		js.put("motivo", suspension.getMotivo());
		if (suspension.getCumplidaEn() == null) {
			js.put("cumplidaEn", JSONObject.NULL);
		} else {
			js.put("cumplidaEn", suspension.getCumplidaEn().getId());
		}
		return js;
	}

	// ---------- Cargar ----------

	protected void verificarIdNuevo(int id, Club club, String archivo) throws PersistenciaException {
		try {
			club.buscarPartido(id);
		} catch (NoEncontradoException e) {
			return;
		}
		throw new PersistenciaException("Id de partido repetido en " + archivo + ": " + id);
	}

	protected LocalDate leerFecha(JSONObject json, String campo) {
		return LocalDate.parse(json.getString(campo));
	}

	/**
	 * Carga el marcador y las estadísticas. El tipo de estadística depende del deporte del partido.
	 */
	protected void cargarResultadoYEstadisticas(JSONObject jp, Partido partido, DeporteEquipo deporte, Club club)
			throws NoEncontradoException, PersistenciaException {
		if (jp.getBoolean("jugado")) {
			partido.registrarResultado(jp.getInt("marcadorLocal"), jp.getInt("marcadorVisitante"));
		}
		JSONArray estadisticas = jp.getJSONArray("estadisticas");
		for (int i = 0; i < estadisticas.length(); i++) {
			JSONObject je = estadisticas.getJSONObject(i);
			JugadorEquipo jugador = buscarJugador(je, club);
			partido.agregarEstadistica(crearEstadistica(je, jugador, deporte));
		}
	}

	private Estadistica crearEstadistica(JSONObject je, JugadorEquipo jugador, DeporteEquipo deporte)
			throws PersistenciaException {
		String nombre = deporte.getNombre();
		if (nombre.equalsIgnoreCase(EstadisticaFutbol.DEPORTE)) {
			return new EstadisticaFutbol(jugador, je.getInt("goles"), je.getInt("asistencias"), je.getInt("amarillas"),
					je.getInt("rojas"));
		}
		if (nombre.equalsIgnoreCase(EstadisticaBaloncesto.DEPORTE)) {
			return new EstadisticaBaloncesto(jugador, je.getInt("puntos"), je.getInt("rebotes"));
		}
		if (nombre.equalsIgnoreCase(EstadisticaVoleibol.DEPORTE)) {
			return new EstadisticaVoleibol(jugador, je.getInt("aces"), je.getInt("bloqueos"));
		}
		throw new PersistenciaException("No hay estadísticas para el deporte " + nombre);
	}

	/**
	 * Se llama después de cargar todos los partidos del archivo, porque cumplidaEn
	 * puede apuntar a un partido que está más abajo.
	 */
	protected void cargarSuspensiones(JSONObject jp, Partido partido, Club club) throws NoEncontradoException {
		JSONArray suspensiones = jp.getJSONArray("suspensiones");
		for (int i = 0; i < suspensiones.length(); i++) {
			JSONObject js = suspensiones.getJSONObject(i);
			JugadorEquipo jugador = buscarJugador(js, club);
			Suspension suspension = new Suspension(js.getString("motivo"), partido);
			if (!js.isNull("cumplidaEn")) {
				suspension.cumplir(club.buscarPartido(js.getInt("cumplidaEn")));
			}
			jugador.agregarSuspension(suspension);
		}
	}

	protected JugadorEquipo buscarJugador(JSONObject json, Club club) throws NoEncontradoException {
		Equipo equipo = club.buscarEquipo(json.getString("equipo"));
		return equipo.buscarJugadorPorSocio(json.getString("socioID"));
	}

	// El grupo puede ser null (en liga).
	protected Object textoOpcional(String texto) {
		if (texto == null) {
			return JSONObject.NULL;
		}
		return texto;
	}

	protected String leerTextoOpcional(JSONObject json, String campo) {
		if (json.isNull(campo)) {
			return null;
		}
		return json.getString(campo);
	}
}
