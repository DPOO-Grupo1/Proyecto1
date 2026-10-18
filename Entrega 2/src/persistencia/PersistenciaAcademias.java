package persistencia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.DatoInvalidoException;
import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.EscenarioConjunto;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisMesa;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.EstadoReserva;
import modelo.academias.Inscripcion;
import modelo.academias.Instalacion;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.usuarios.Socio;

/**
 * Guarda y carga los datos del Área B (Academias) en academias.json:
 * disciplinas, instalaciones deportivas, inscripciones con ELO,
 * reservas e historial de partidos de práctica con estadísticas.
 */
public class PersistenciaAcademias implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			// 1. Disciplinas
			if (json.has("disciplinas")) {
				JSONArray disciplinas = json.getJSONArray("disciplinas");
				for (int i = 0; i < disciplinas.length(); i++) {
					JSONObject jd = disciplinas.getJSONObject(i);
					String nombre = jd.getString("nombre");
					int cupo = jd.getInt("cupoInstalaciones");
					try {
						club.buscarDisciplina(nombre);
					} catch (NoEncontradoException e) {
						club.agregarDisciplina(new Disciplina(nombre, cupo));
					}
				}
			}

			// 2. Instalaciones
			if (json.has("instalaciones")) {
				JSONArray instalaciones = json.getJSONArray("instalaciones");
				for (int i = 0; i < instalaciones.length(); i++) {
					JSONObject ji = instalaciones.getJSONObject(i);
					cargarInstalacion(ji, club, archivo);
				}
			}

			// 3. Inscripciones de socios
			if (json.has("inscripciones")) {
				JSONArray inscripciones = json.getJSONArray("inscripciones");
				for (int i = 0; i < inscripciones.length(); i++) {
					JSONObject jin = inscripciones.getJSONObject(i);
					Socio socio = club.buscarSocio(jin.getString("socioID"));
					Disciplina disciplina = club.buscarDisciplina(jin.getString("disciplina"));
					Nivel nivel = Nivel.desdeTexto(jin.getString("nivel"));
					int elo = jin.optInt("puntajeELO", Inscripcion.ELO_INICIAL);

					if (socio.getInscripcion(disciplina) == null) {
						Inscripcion ins = new Inscripcion(socio, disciplina, nivel, elo);
						socio.agregarInscripcion(ins);
						disciplina.agregarInscripcion(ins);
					}
				}
			}

			// 4. Reservas
			if (json.has("reservas")) {
				JSONArray reservas = json.getJSONArray("reservas");
				for (int i = 0; i < reservas.length(); i++) {
					JSONObject jr = reservas.getJSONObject(i);
					String id = jr.getString("id");
					Socio titular = club.buscarSocio(jr.getString("titular"));
					Instalacion inst = club.buscarInstalacion(jr.getString("instalacion"));
					LocalDate fecha = LocalDate.parse(jr.getString("fecha"));
					LocalTime inicio = LocalTime.parse(jr.getString("horaInicio"));
					LocalTime fin = LocalTime.parse(jr.getString("horaFin"));
					int numJugadores = jr.getInt("numJugadores");
					Modalidad modalidad = Modalidad.desdeTexto(jr.getString("modalidad"));
					EstadoReserva estado = EstadoReserva.valueOf(jr.getString("estado").toUpperCase());

					Reserva reserva = new Reserva(id, titular, inst, fecha, inicio, fin, numJugadores, modalidad);
					reserva.setEstado(estado);
					club.agregarReserva(reserva);
					inst.agregarReserva(reserva);
					titular.agregarReserva(reserva);
				}
			}

			// 5. Partidos de práctica
			if (json.has("partidosPractica")) {
				JSONArray partidos = json.getJSONArray("partidosPractica");
				for (int i = 0; i < partidos.length(); i++) {
					JSONObject jp = partidos.getJSONObject(i);
					cargarPartidoPractica(jp, club);
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

	private void cargarInstalacion(JSONObject ji, Club club, String archivo)
			throws JSONException, NoEncontradoException, PersistenciaException {
		String id = ji.getString("id");
		try {
			club.buscarInstalacion(id);
			return; // Ya existe en el club
		} catch (NoEncontradoException e) {
			// Continúa para crearla
		}

		String nombre = ji.getString("nombre");
		String tipo = ji.getString("tipo").toLowerCase();

		Instalacion instalacion;
		if (tipo.equals("tenis")) {
			Disciplina d = club.buscarDisciplina(ji.getString("disciplina"));
			instalacion = new CanchaTenis(id, nombre, d);
		} else if (tipo.equals("padel") || tipo.equals("pádel")) {
			Disciplina d = club.buscarDisciplina(ji.getString("disciplina"));
			instalacion = new CanchaPadel(id, nombre, d);
		} else if (tipo.equals("tenis_mesa") || tipo.equals("tenis de mesa")) {
			Disciplina d = club.buscarDisciplina(ji.getString("disciplina"));
			boolean techada = ji.optBoolean("techada", false);
			instalacion = new MesaTenisMesa(id, nombre, d, techada);
		} else if (tipo.equals("escenario_conjunto") || tipo.equals("conjunto")) {
			String tipoDeporte = ji.getString("tipoDeporte");
			instalacion = new EscenarioConjunto(id, nombre, tipoDeporte);
		} else {
			throw new PersistenciaException("Tipo de instalación desconocido en " + archivo + ": " + tipo);
		}
		club.agregarInstalacion(instalacion);
	}

	private void cargarPartidoPractica(JSONObject jp, Club club)
			throws JSONException, NoEncontradoException, DatoInvalidoException {
		String id = jp.getString("id");
		Reserva reserva = club.buscarReserva(jp.getString("idReserva"));

		JSONArray jparts = jp.getJSONArray("participantes");
		List<Socio> participantes = new ArrayList<Socio>();
		for (int p = 0; p < jparts.length(); p++) {
			participantes.add(club.buscarSocio(jparts.getString(p)));
		}

		PartidoPractica partido = new PartidoPractica(id, reserva, participantes);
		partido.setCancelado(jp.optBoolean("cancelado", false));
		partido.setJugado(jp.optBoolean("jugado", false));

		if (!jp.isNull("ganador")) {
			partido.setGanador(club.buscarSocio(jp.getString("ganador")));
		}

		if (jp.has("estadisticas")) {
			JSONArray jstats = jp.getJSONArray("estadisticas");
			for (int s = 0; s < jstats.length(); s++) {
				JSONObject js = jstats.getJSONObject(s);
				Socio socio = club.buscarSocio(js.getString("socioID"));
				int sets = js.getInt("setsGanados");
				if (js.has("gamesGanados")) {
					int games = js.getInt("gamesGanados");
					int aces = js.getInt("aces");
					int errores = js.getInt("erroresNoForzados");
					partido.agregarEstadistica(socio, new EstadisticaTenisPadel(sets, games, aces, errores));
				} else if (js.has("puntosTotales")) {
					int puntos = js.getInt("puntosTotales");
					partido.agregarEstadistica(socio, new EstadisticaTenisMesa(sets, puntos));
				}
			}
		}

		club.agregarPartidoPractica(partido);
		reserva.setPartido(partido);

		// Conectar con el historial de la disciplina
		if (reserva.getInstalacion() != null && reserva.getInstalacion().getDisciplina() != null) {
			Disciplina disc = reserva.getInstalacion().getDisciplina();
			for (Socio part : participantes) {
				Inscripcion ins = part.getInscripcion(disc);
				if (ins != null && !ins.getPartidos().contains(partido)) {
					ins.agregarPartido(partido);
				}
			}
		}
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = new JSONObject();

		// 1. Disciplinas
		JSONArray jDisciplinas = new JSONArray();
		for (Disciplina d : club.getDisciplinas()) {
			JSONObject jd = new JSONObject();
			jd.put("nombre", d.getNombre());
			jd.put("cupoInstalaciones", d.getCupoInstalaciones());
			jDisciplinas.put(jd);
		}
		json.put("disciplinas", jDisciplinas);

		// 2. Instalaciones
		JSONArray jInstalaciones = new JSONArray();
		for (Instalacion inst : club.getInstalaciones()) {
			JSONObject ji = new JSONObject();
			ji.put("id", inst.getId());
			ji.put("nombre", inst.getNombre());
			if (inst instanceof CanchaTenis) {
				ji.put("tipo", "tenis");
				ji.put("disciplina", inst.getDisciplina().getNombre());
			} else if (inst instanceof CanchaPadel) {
				ji.put("tipo", "padel");
				ji.put("disciplina", inst.getDisciplina().getNombre());
			} else if (inst instanceof MesaTenisMesa) {
				ji.put("tipo", "tenis_mesa");
				ji.put("disciplina", inst.getDisciplina().getNombre());
				ji.put("techada", ((MesaTenisMesa) inst).isTechada());
			} else if (inst instanceof EscenarioConjunto) {
				ji.put("tipo", "escenario_conjunto");
				ji.put("tipoDeporte", ((EscenarioConjunto) inst).getTipo());
			}
			jInstalaciones.put(ji);
		}
		json.put("instalaciones", jInstalaciones);

		// 3. Inscripciones
		JSONArray jInscripciones = new JSONArray();
		for (Disciplina d : club.getDisciplinas()) {
			for (Inscripcion ins : d.getInscripciones()) {
				JSONObject jin = new JSONObject();
				jin.put("socioID", ins.getSocio().getSocioID());
				jin.put("disciplina", ins.getDisciplina().getNombre());
				jin.put("nivel", ins.getNivel().name());
				jin.put("puntajeELO", ins.getPuntajeELO());
				jInscripciones.put(jin);
			}
		}
		json.put("inscripciones", jInscripciones);

		// 4. Reservas
		JSONArray jReservas = new JSONArray();
		for (Reserva r : club.getReservas()) {
			JSONObject jr = new JSONObject();
			jr.put("id", r.getId());
			jr.put("titular", r.getTitular().getSocioID());
			jr.put("instalacion", r.getInstalacion().getId());
			jr.put("fecha", r.getFecha().toString());
			jr.put("horaInicio", r.getHoraInicio().toString());
			jr.put("horaFin", r.getHoraFin().toString());
			jr.put("numJugadores", r.getNumJugadores());
			jr.put("modalidad", r.getModalidad().name());
			jr.put("estado", r.getEstado().name());
			jReservas.put(jr);
		}
		json.put("reservas", jReservas);

		// 5. Partidos de práctica
		JSONArray jPartidos = new JSONArray();
		for (PartidoPractica pp : club.getPartidosPractica()) {
			JSONObject jp = new JSONObject();
			jp.put("id", pp.getId());
			jp.put("idReserva", pp.getReserva().getId());
			JSONArray jParts = new JSONArray();
			for (Socio s : pp.getParticipantes()) {
				jParts.put(s.getSocioID());
			}
			jp.put("participantes", jParts);
			jp.put("jugado", pp.isJugado());
			jp.put("cancelado", pp.estaCancelado());
			jp.put("ganador", pp.getGanador() != null ? pp.getGanador().getSocioID() : JSONObject.NULL);

			JSONArray jStats = new JSONArray();
			for (Socio s : pp.getParticipantes()) {
				EstadisticaPractica stat = pp.getEstadistica(s);
				if (stat != null) {
					JSONObject js = new JSONObject();
					js.put("socioID", s.getSocioID());
					js.put("setsGanados", stat.getSetsGanados());
					if (stat instanceof EstadisticaTenisPadel) {
						EstadisticaTenisPadel tp = (EstadisticaTenisPadel) stat;
						js.put("gamesGanados", tp.getGamesGanados());
						js.put("aces", tp.getAces());
						js.put("erroresNoForzados", tp.getErroresNoForzados());
					} else if (stat instanceof EstadisticaTenisMesa) {
						EstadisticaTenisMesa tm = (EstadisticaTenisMesa) stat;
						js.put("puntosTotales", tm.getPuntosTotales());
					}
					jStats.put(js);
				}
			}
			jp.put("estadisticas", jStats);
			jPartidos.put(jp);
		}
		json.put("partidosPractica", jPartidos);

		archivoJSON.escribir(archivo, json);
	}
}
