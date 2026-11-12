package persistencia;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
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
import modelo.partidos.PartidoCompetencia;

/**
 * Guarda en competencias.json las competencias con su formato, sus participantes
 * (la nómina va como lista de socioID) y sus partidos oficiales.
 * Se carga después de deportes.json porque necesita los equipos.
 */
public class PersistenciaCompetencias extends PersistenciaPartidos {

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray competencias = json.getJSONArray("competencias");
			for (int i = 0; i < competencias.length(); i++) {
				cargarCompetencia(competencias.getJSONObject(i), club, archivo);
			}
			// Las suspensiones van al final porque cumplidaEn puede apuntar a un partido posterior.
			for (int i = 0; i < competencias.length(); i++) {
				JSONArray partidos = competencias.getJSONObject(i).getJSONArray("partidos");
				for (int k = 0; k < partidos.length(); k++) {
					JSONObject jp = partidos.getJSONObject(k);
					cargarSuspensiones(jp, club.buscarPartido(jp.getInt("id")), club);
				}
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha mal escrita en " + archivo + " (debe ser AAAA-MM-DD): " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private void cargarCompetencia(JSONObject jc, Club club, String archivo)
			throws NoEncontradoException, PersistenciaException {
		String nombre = jc.getString("nombre");
		int temporada = jc.getInt("temporada");
		if (club.existeCompetencia(nombre, temporada)) {
			throw new PersistenciaException("Competencia repetida en " + archivo + ": " + nombre + " " + temporada);
		}
		DeporteEquipo deporte = club.buscarDeporte(jc.getString("deporte"));
		Categoria categoria = club.buscarCategoria(jc.getString("categoria"));
		Competencia competencia = new Competencia(nombre, jc.getString("entidadOrganizadora"), temporada, deporte,
				categoria, jc.getInt("nominaMinima"), jc.getInt("amarillasParaSuspension"),
				cargarFormato(jc.getJSONObject("formato"), archivo));
		club.agregarCompetencia(competencia);

		JSONArray participantes = jc.getJSONArray("participantes");
		for (int i = 0; i < participantes.length(); i++) {
			competencia.agregarParticipante(cargarParticipante(participantes.getJSONObject(i), club, archivo));
		}

		JSONArray partidos = jc.getJSONArray("partidos");
		for (int i = 0; i < partidos.length(); i++) {
			JSONObject jp = partidos.getJSONObject(i);
			int id = jp.getInt("id");
			verificarIdNuevo(id, club, archivo);
			ParticipanteCompetencia local = competencia.buscarParticipante(jp.getString("local"));
			ParticipanteCompetencia visitante = competencia.buscarParticipante(jp.getString("visitante"));
			PartidoCompetencia partido = new PartidoCompetencia(id, leerFecha(jp, "fecha"), competencia, local,
					visitante, jp.getString("escenario"), jp.getString("fase"));
			competencia.agregarPartido(partido);
			cargarResultadoYEstadisticas(jp, partido, deporte, club);
		}
	}

	private Formato cargarFormato(JSONObject jf, String archivo) throws PersistenciaException {
		String tipo = jf.getString("tipo");
		if (tipo.equals("Liga")) {
			return new Liga(jf.getBoolean("idaYVuelta"));
		}
		if (tipo.equals("GruposEliminacion")) {
			return new GruposEliminacion(jf.getInt("numeroGrupos"), jf.getInt("clasificadosPorGrupo"));
		}
		throw new PersistenciaException("Formato desconocido en " + archivo + ": " + tipo);
	}

	private ParticipanteCompetencia cargarParticipante(JSONObject jp, Club club, String archivo)
			throws NoEncontradoException, PersistenciaException {
		String tipo = jp.getString("tipo");
		String grupo = leerTextoOpcional(jp, "grupo");
		if (tipo.equals("Externo")) {
			return new EquipoExterno(jp.getString("nombre"), jp.getString("institucion"), grupo);
		}
		if (tipo.equals("Inscrito")) {
			Equipo equipo = club.buscarEquipo(jp.getString("equipo"));
			List<JugadorEquipo> nomina = new ArrayList<JugadorEquipo>();
			JSONArray jn = jp.getJSONArray("nomina");
			for (int i = 0; i < jn.length(); i++) {
				nomina.add(equipo.buscarJugadorPorSocio(jn.getString(i)));
			}
			return new EquipoInscrito(equipo, nomina, grupo, leerFecha(jp, "fechaInscripcion"));
		}
		throw new PersistenciaException("Tipo de participante desconocido en " + archivo + ": " + tipo);
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray competencias = new JSONArray();
		for (Competencia competencia : club.getCompetencias()) {
			JSONObject jc = new JSONObject();
			jc.put("nombre", competencia.getNombre());
			jc.put("entidadOrganizadora", competencia.getEntidadOrganizadora());
			jc.put("temporada", competencia.getTemporada());
			jc.put("deporte", competencia.getDeporte().getNombre());
			jc.put("categoria", competencia.getCategoria().getNombre());
			jc.put("nominaMinima", competencia.getNominaMinima());
			jc.put("amarillasParaSuspension", competencia.getAmarillasParaSuspension());
			jc.put("formato", formatoAJSON(competencia.getFormato()));

			JSONArray participantes = new JSONArray();
			for (ParticipanteCompetencia participante : competencia.getParticipantes()) {
				participantes.put(participanteAJSON(participante));
			}
			jc.put("participantes", participantes);

			JSONArray partidos = new JSONArray();
			for (PartidoCompetencia partido : competencia.getPartidos()) {
				JSONObject jp = partidoAJSON(partido, club);
				jp.put("local", partido.getLocal().getNombre());
				jp.put("visitante", partido.getVisitante().getNombre());
				jp.put("escenario", partido.getEscenario());
				jp.put("fase", partido.getFase());
				partidos.put(jp);
			}
			jc.put("partidos", partidos);
			competencias.put(jc);
		}

		JSONObject json = new JSONObject();
		json.put("competencias", competencias);
		archivoJSON.escribir(archivo, json);
	}

	private JSONObject formatoAJSON(Formato formato) {
		JSONObject jf = new JSONObject();
		if (formato instanceof Liga) {
			jf.put("tipo", "Liga");
			jf.put("idaYVuelta", ((Liga) formato).isIdaYVuelta());
		} else {
			GruposEliminacion grupos = (GruposEliminacion) formato;
			jf.put("tipo", "GruposEliminacion");
			jf.put("numeroGrupos", grupos.getNumeroGrupos());
			jf.put("clasificadosPorGrupo", grupos.getClasificadosPorGrupo());
		}
		return jf;
	}

	private JSONObject participanteAJSON(ParticipanteCompetencia participante) {
		JSONObject jp = new JSONObject();
		jp.put("grupo", textoOpcional(participante.getGrupo()));
		if (participante instanceof EquipoInscrito) {
			EquipoInscrito inscrito = (EquipoInscrito) participante;
			jp.put("tipo", "Inscrito");
			jp.put("equipo", inscrito.getEquipo().getNombre());
			jp.put("fechaInscripcion", inscrito.getFechaInscripcion().toString());
			JSONArray nomina = new JSONArray();
			for (JugadorEquipo jugador : inscrito.getNomina()) {
				nomina.put(jugador.getSocio().getSocioID());
			}
			jp.put("nomina", nomina);
		} else {
			EquipoExterno externo = (EquipoExterno) participante;
			jp.put("tipo", "Externo");
			jp.put("nombre", externo.getNombre());
			jp.put("institucion", externo.getInstitucion());
		}
		return jp;
	}
}
