package persistencia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.DatoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.academias.Disciplina;
import modelo.academias.Instalacion;
import modelo.deportes.Equipo;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.Evaluacion;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Guarda y carga las sesiones de entrenamiento, convocatorias,
 * asistencias y evaluaciones en entrenamiento.json.
 */
public class PersistenciaEntrenamiento implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			if (json.has("sesiones")) {
				JSONArray sesiones = json.getJSONArray("sesiones");
				for (int i = 0; i < sesiones.length(); i++) {
					JSONObject js = sesiones.getJSONObject(i);
					cargarSesion(js, club);
				}
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha u hora mal escrita en " + archivo + ": " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		} catch (DatoInvalidoException e) {
			throw new PersistenciaException("Dato inválido en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private void cargarSesion(JSONObject js, Club club)
			throws JSONException, NoEncontradoException, DatoInvalidoException {
		String id = js.getString("id");
		String loginEntrenador = js.getString("entrenador");
		Usuario usuarioEntrenador = club.buscarUsuario(loginEntrenador);
		if (!(usuarioEntrenador instanceof Entrenador)) {
			throw new DatoInvalidoException("El usuario '" + loginEntrenador + "' no es un entrenador");
		}
		Entrenador entrenador = (Entrenador) usuarioEntrenador;

		Instalacion instalacion = club.buscarInstalacion(js.getString("instalacion"));
		LocalDate fecha = LocalDate.parse(js.getString("fecha"));
		LocalTime hora = LocalTime.parse(js.getString("hora"));
		int duracionMin = js.getInt("duracionMin");
		boolean finalizada = js.optBoolean("finalizada", false);

		SesionEntrenamiento sesion;
		if (js.has("equipo") && !js.isNull("equipo") && !js.getString("equipo").isEmpty()) {
			Equipo equipo = club.buscarEquipo(js.getString("equipo"));
			sesion = new SesionEntrenamiento(id, entrenador, instalacion, fecha, hora, duracionMin, equipo);
		} else {
			Disciplina disciplina = null;
			if (js.has("disciplina") && !js.isNull("disciplina") && !js.getString("disciplina").isEmpty()) {
				disciplina = club.buscarDisciplina(js.getString("disciplina"));
			}
			sesion = new SesionEntrenamiento(id, entrenador, instalacion, fecha, hora, duracionMin, disciplina);
		}
		sesion.setFinalizada(finalizada);

		if (js.has("convocatorias")) {
			JSONArray jConvs = js.getJSONArray("convocatorias");
			for (int c = 0; c < jConvs.length(); c++) {
				JSONObject jc = jConvs.getJSONObject(c);
				Socio socio = club.buscarSocio(jc.getString("socioID"));
				Convocatoria conv = new Convocatoria(socio, sesion);

				String estadoTexto = jc.optString("asistencia", "pendiente");
				conv.setAsistencia(EstadoAsistencia.desdeTexto(estadoTexto));

				if (jc.has("evaluacion") && !jc.isNull("evaluacion")) {
					JSONObject je = jc.getJSONObject("evaluacion");
					int nota = je.getInt("nota");
					String comentario = je.optString("comentario", "");
					conv.asignarEvaluacion(new Evaluacion(nota, comentario));
				}
				sesion.agregarConvocatoria(conv);
			}
		}

		club.agregarSesion(sesion);
		instalacion.agregarSesion(sesion);
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = new JSONObject();
		JSONArray jSesiones = new JSONArray();

		for (SesionEntrenamiento s : club.getSesiones()) {
			JSONObject js = new JSONObject();
			js.put("id", s.getId());
			js.put("entrenador", s.getEntrenador().getLogin());
			js.put("instalacion", s.getInstalacion().getId());
			js.put("fecha", s.getFecha().toString());
			js.put("hora", s.getHora().toString());
			js.put("duracionMin", s.getDuracionMin());
			js.put("finalizada", s.estaFinalizada());
			js.put("disciplina", s.getDisciplina() != null ? s.getDisciplina().getNombre() : JSONObject.NULL);
			js.put("equipo", s.getEquipo() != null ? s.getEquipo().getNombre() : JSONObject.NULL);

			JSONArray jConvs = new JSONArray();
			for (Convocatoria c : s.getConvocatorias()) {
				JSONObject jc = new JSONObject();
				jc.put("socioID", c.getSocio().getSocioID());
				jc.put("asistencia", c.getAsistencia().toString());

				if (c.tieneEvaluacion()) {
					JSONObject je = new JSONObject();
					je.put("nota", c.getEvaluacion().getNota());
					je.put("comentario", c.getEvaluacion().getComentario());
					jc.put("evaluacion", je);
				} else {
					jc.put("evaluacion", JSONObject.NULL);
				}
				jConvs.put(jc);
			}
			js.put("convocatorias", jConvs);
			jSesiones.put(js);
		}

		json.put("sesiones", jSesiones);
		archivoJSON.escribir(archivo, json);
	}
}
