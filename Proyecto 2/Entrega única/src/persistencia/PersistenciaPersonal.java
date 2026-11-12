package persistencia;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.personal.EstadoSolicitud;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;
import modelo.usuarios.Empleado;

/**
 * Guarda en personal.json los turnos con sus solicitudes de cambio y las sugerencias de productos.
 * Los empleados se guardan por id y se buscan al cargar.
 */
public class PersistenciaPersonal implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray turnos = json.getJSONArray("turnos");
			for (int i = 0; i < turnos.length(); i++) {
				JSONObject jt = turnos.getJSONObject(i);
				Empleado empleado = club.buscarEmpleado(jt.getInt("empleado"));
				Turno turno = new Turno(jt.getInt("id"), empleado, DayOfWeek.valueOf(jt.getString("dia")),
						LocalTime.parse(jt.getString("inicio")), LocalTime.parse(jt.getString("fin")));

				JSONArray solicitudes = jt.getJSONArray("solicitudes");
				for (int j = 0; j < solicitudes.length(); j++) {
					JSONObject js = solicitudes.getJSONObject(j);
					SolicitudCambioTurno s = new SolicitudCambioTurno(turno, LocalDate.parse(js.getString("fecha")),
							DayOfWeek.valueOf(js.getString("nuevoDia")), LocalTime.parse(js.getString("nuevoInicio")),
							LocalTime.parse(js.getString("nuevoFin")));
					EstadoSolicitud estado = EstadoSolicitud.valueOf(js.getString("estado"));
					if (estado == EstadoSolicitud.APROBADA) {
						s.aprobar();
					} else if (estado == EstadoSolicitud.RECHAZADA) {
						s.rechazar();
					}
					turno.agregarSolicitud(s);
				}
				club.getTurnos().add(turno);
				empleado.agregarTurno(turno);
			}

			JSONArray sugerencias = json.getJSONArray("sugerencias");
			for (int i = 0; i < sugerencias.length(); i++) {
				JSONObject jg = sugerencias.getJSONObject(i);
				Empleado empleado = club.buscarEmpleado(jg.getInt("empleado"));
				Sugerencia sugerencia = new Sugerencia(empleado, jg.getString("nombre"), jg.getString("tipo"),
						jg.getString("descripcion"), LocalDate.parse(jg.getString("fecha")));
				EstadoSolicitud estado = EstadoSolicitud.valueOf(jg.getString("estado"));
				if (estado == EstadoSolicitud.APROBADA) {
					sugerencia.aprobar();
				} else if (estado == EstadoSolicitud.RECHAZADA) {
					sugerencia.rechazar(jg.getString("motivo"));
				}
				club.agregarSugerencia(sugerencia);
				empleado.agregarSugerencia(sugerencia);
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha u hora mal escrita en " + archivo + ": " + e.getParsedString(), e);
		} catch (IllegalArgumentException e) {
			throw new PersistenciaException("Día o estado desconocido en " + archivo + ": " + e.getMessage(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		}
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray turnos = new JSONArray();
		for (Turno t : club.getTurnos()) {
			JSONObject jt = new JSONObject();
			jt.put("id", t.getId());
			jt.put("empleado", t.getEmpleado().getIdEmpleado());
			jt.put("dia", t.getDia().name());
			jt.put("inicio", t.getHoraInicio().toString());
			jt.put("fin", t.getHoraFin().toString());
			JSONArray solicitudes = new JSONArray();
			for (SolicitudCambioTurno s : t.getSolicitudes()) {
				JSONObject js = new JSONObject();
				js.put("fecha", s.getFechaSolicitud().toString());
				js.put("nuevoDia", s.getNuevoDia().name());
				js.put("nuevoInicio", s.getNuevaHoraInicio().toString());
				js.put("nuevoFin", s.getNuevaHoraFin().toString());
				js.put("estado", s.getEstado().name());
				solicitudes.put(js);
			}
			jt.put("solicitudes", solicitudes);
			turnos.put(jt);
		}

		JSONArray sugerencias = new JSONArray();
		for (Sugerencia s : club.getSugerencias()) {
			JSONObject jg = new JSONObject();
			jg.put("empleado", s.getEmpleado().getIdEmpleado());
			jg.put("nombre", s.getNombreProducto());
			jg.put("tipo", s.getTipo());
			jg.put("descripcion", s.getDescripcion());
			jg.put("fecha", s.getFecha().toString());
			jg.put("estado", s.getEstado().name());
			if (s.getMotivoRechazo() == null) {
				jg.put("motivo", JSONObject.NULL);
			} else {
				jg.put("motivo", s.getMotivoRechazo());
			}
			sugerencias.put(jg);
		}

		JSONObject json = new JSONObject();
		json.put("turnos", turnos);
		json.put("sugerencias", sugerencias);
		archivoJSON.escribir(archivo, json);
	}
}
