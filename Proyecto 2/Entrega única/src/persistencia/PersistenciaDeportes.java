package persistencia;

import java.time.format.DateTimeParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.partidos.PartidoInterno;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

/**
 * Guarda en deportes.json los deportes, categorías, equipos con sus jugadores
 * y los partidos internos con sus estadísticas y suspensiones.
 * Los socios y entrenadores se guardan por id y se buscan al cargar.
 */
public class PersistenciaDeportes extends PersistenciaPartidos {

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray deportes = json.getJSONArray("deportes");
			for (int i = 0; i < deportes.length(); i++) {
				JSONObject jd = deportes.getJSONObject(i);
				club.agregarDeporte(new DeporteEquipo(jd.getString("nombre"), jd.getInt("minimoHabilitados"),
						jd.getInt("puntosVictoria"), jd.getInt("puntosEmpate"), jd.getInt("puntosDerrota"),
						jd.getBoolean("admiteEmpate"), jd.getBoolean("marcadorPorSets")));
			}

			JSONArray categorias = json.getJSONArray("categorias");
			for (int i = 0; i < categorias.length(); i++) {
				JSONObject jc = categorias.getJSONObject(i);
				club.agregarCategoria(new Categoria(jc.getString("nombre"), jc.getInt("edadMinima"), jc.getInt("edadMaxima")));
			}

			JSONArray equipos = json.getJSONArray("equipos");
			for (int i = 0; i < equipos.length(); i++) {
				cargarEquipo(equipos.getJSONObject(i), club, archivo);
			}

			JSONArray partidos = json.getJSONArray("partidosInternos");
			for (int i = 0; i < partidos.length(); i++) {
				JSONObject jp = partidos.getJSONObject(i);
				int id = jp.getInt("id");
				verificarIdNuevo(id, club, archivo);
				Equipo local = club.buscarEquipo(jp.getString("local"));
				Equipo visitante = club.buscarEquipo(jp.getString("visitante"));
				PartidoInterno partido = new PartidoInterno(id, leerFecha(jp, "fecha"), local, visitante,
						jp.getString("proposito"));
				club.agregarPartidoInterno(partido);
				cargarResultadoYEstadisticas(jp, partido, local.getDeporte(), club);
			}
			for (int i = 0; i < partidos.length(); i++) {
				JSONObject jp = partidos.getJSONObject(i);
				cargarSuspensiones(jp, club.buscarPartido(jp.getInt("id")), club);
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha mal escrita en " + archivo + " (debe ser AAAA-MM-DD): " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private void cargarEquipo(JSONObject jq, Club club, String archivo)
			throws NoEncontradoException, PersistenciaException {
		String nombre = jq.getString("nombre");
		if (club.existeEquipo(nombre)) {
			throw new PersistenciaException("Equipo repetido en " + archivo + ": " + nombre);
		}
		DeporteEquipo deporte = club.buscarDeporte(jq.getString("deporte"));
		Categoria categoria = club.buscarCategoria(jq.getString("categoria"));
		Equipo equipo = new Equipo(nombre, deporte, categoria, jq.getInt("cupoMaximo"));

		if (!jq.isNull("entrenador")) {
			Empleado empleado = club.buscarEmpleado(jq.getInt("entrenador"));
			if (!(empleado instanceof Entrenador)) {
				throw new PersistenciaException("El empleado " + empleado.getIdEmpleado() + " no es entrenador (" + archivo + ")");
			}
			equipo.setEntrenador((Entrenador) empleado);
		}

		JSONArray jugadores = jq.getJSONArray("jugadores");
		for (int i = 0; i < jugadores.length(); i++) {
			JSONObject jj = jugadores.getJSONObject(i);
			Socio socio = club.buscarSocio(jj.getString("socioID"));
			JugadorEquipo jugador = new JugadorEquipo(socio, equipo, jj.getInt("numero"), jj.getString("posicion"));
			equipo.agregarJugador(jugador);
			socio.agregarParticipacion(jugador);
			if (!jj.getBoolean("activo")) {
				jugador.desactivar();
			}
		}

		if (!jq.getBoolean("activo")) {
			equipo.desactivar();
		}
		club.agregarEquipo(equipo);
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray deportes = new JSONArray();
		for (DeporteEquipo deporte : club.getDeportes()) {
			JSONObject jd = new JSONObject();
			jd.put("nombre", deporte.getNombre());
			jd.put("minimoHabilitados", deporte.getMinimoHabilitados());
			jd.put("puntosVictoria", deporte.getPuntosVictoria());
			jd.put("puntosEmpate", deporte.getPuntosEmpate());
			jd.put("puntosDerrota", deporte.getPuntosDerrota());
			jd.put("admiteEmpate", deporte.isAdmiteEmpate());
			jd.put("marcadorPorSets", deporte.isMarcadorPorSets());
			deportes.put(jd);
		}

		JSONArray categorias = new JSONArray();
		for (Categoria categoria : club.getCategorias()) {
			JSONObject jc = new JSONObject();
			jc.put("nombre", categoria.getNombre());
			jc.put("edadMinima", categoria.getEdadMinima());
			jc.put("edadMaxima", categoria.getEdadMaxima());
			categorias.put(jc);
		}

		JSONArray equipos = new JSONArray();
		for (Equipo equipo : club.getEquipos()) {
			JSONObject jq = new JSONObject();
			jq.put("nombre", equipo.getNombre());
			jq.put("deporte", equipo.getDeporte().getNombre());
			jq.put("categoria", equipo.getCategoria().getNombre());
			jq.put("cupoMaximo", equipo.getCupoMaximo());
			jq.put("activo", equipo.estaActivo());
			if (equipo.getEntrenador() == null) {
				jq.put("entrenador", JSONObject.NULL);
			} else {
				jq.put("entrenador", equipo.getEntrenador().getIdEmpleado());
			}
			JSONArray jugadores = new JSONArray();
			for (JugadorEquipo jugador : equipo.getJugadores()) {
				JSONObject jj = new JSONObject();
				jj.put("socioID", jugador.getSocio().getSocioID());
				jj.put("numero", jugador.getNumeroCamiseta());
				jj.put("posicion", jugador.getPosicion());
				jj.put("activo", jugador.estaActivo());
				jugadores.put(jj);
			}
			jq.put("jugadores", jugadores);
			equipos.put(jq);
		}

		JSONArray partidos = new JSONArray();
		for (PartidoInterno partido : club.getPartidosInternos()) {
			JSONObject jp = partidoAJSON(partido, club);
			jp.put("local", partido.getLocal().getNombre());
			jp.put("visitante", partido.getVisitante().getNombre());
			jp.put("proposito", partido.getProposito());
			partidos.put(jp);
		}

		JSONObject json = new JSONObject();
		json.put("deportes", deportes);
		json.put("categorias", categorias);
		json.put("equipos", equipos);
		json.put("partidosInternos", partidos);
		archivoJSON.escribir(archivo, json);
	}
}
