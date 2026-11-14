package persistencia;

import java.time.LocalDate;
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
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.deportes.Equipo;
import modelo.torneos.BonoDescuento;
import modelo.torneos.Competitivo;
import modelo.torneos.EliminacionDirecta;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.FormatoTorneo;
import modelo.torneos.InscripcionEquipo;
import modelo.torneos.InscripcionIndividual;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Recreativo;
import modelo.torneos.TipoTorneo;
import modelo.torneos.TodosContraTodos;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Usuario;

/**
 * Guarda en torneos.json los torneos internos (con sus inscripciones y encuentros) y los
 * bonos de descuento ganados. Se carga de último porque necesita usuarios, equipos y disciplinas.
 * Las posiciones y el ganador no se guardan: se calculan con los encuentros.
 */
public class PersistenciaTorneos implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray torneos = json.getJSONArray("torneos");
			for (int i = 0; i < torneos.length(); i++) {
				cargarTorneo(torneos.getJSONObject(i), club, archivo);
			}

			JSONArray bonos = json.getJSONArray("bonos");
			for (int i = 0; i < bonos.length(); i++) {
				JSONObject jb = bonos.getJSONObject(i);
				// Los ids van de 1 a n porque el siguiente id es n + 1
				if (jb.getInt("id") != i + 1) {
					throw new PersistenciaException("Los bonos de " + archivo + " deben tener ids de 1 a n en orden");
				}
				BonoDescuento bono = new BonoDescuento(jb.getInt("id"), club.buscarUsuario(jb.getString("dueno")),
						jb.getInt("porcentaje"), jb.getString("torneo"));
				if (jb.getBoolean("usado")) {
					bono.usar();
				}
				club.agregarBono(bono);
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha mal escrita en " + archivo + " (debe ser AAAA-MM-DD): " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		} catch (DatoInvalidoException e) {
			throw new PersistenciaException("Dato inválido en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private void cargarTorneo(JSONObject jt, Club club, String archivo)
			throws NoEncontradoException, DatoInvalidoException, PersistenciaException {
		String nombre = jt.getString("nombre");
		if (club.existeTorneo(nombre)) {
			throw new PersistenciaException("Torneo repetido en " + archivo + ": " + nombre);
		}
		LocalDate fecha = LocalDate.parse(jt.getString("fecha"));
		int cupos = jt.getInt("cupos");
		TipoTorneo tipo = cargarTipo(jt.getJSONObject("tipo"), archivo);
		FormatoTorneo formato = cargarFormato(jt.getString("formato"), archivo);

		Torneo torneo;
		String clase = jt.getString("clase");
		if (clase.equals("Conjunto")) {
			torneo = new TorneoConjunto(nombre, fecha, cupos, tipo, formato, club.buscarDeporte(jt.getString("deporte")),
					club.buscarCategoria(jt.getString("categoria")));
		} else if (clase.equals("Individual")) {
			torneo = new TorneoIndividual(nombre, fecha, cupos, tipo, formato,
					club.buscarDisciplina(jt.getString("disciplina")), Nivel.desdeTexto(jt.getString("nivel")),
					Modalidad.desdeTexto(jt.getString("modalidad")));
		} else {
			throw new PersistenciaException("Clase de torneo desconocida en " + archivo + ": " + clase);
		}
		torneo.setEstado(cargarEstado(jt.getString("estado"), archivo));
		if (jt.getBoolean("preferencialesLiberados")) {
			torneo.liberarCuposPreferenciales();
		}
		club.agregarTorneo(torneo);

		JSONArray inscripciones = jt.getJSONArray("inscripciones");
		for (int i = 0; i < inscripciones.length(); i++) {
			torneo.agregarInscripcion(cargarInscripcion(inscripciones.getJSONObject(i), club));
		}

		JSONArray encuentros = jt.getJSONArray("encuentros");
		List<EncuentroTorneo> lista = new ArrayList<EncuentroTorneo>();
		for (int i = 0; i < encuentros.length(); i++) {
			JSONObject je = encuentros.getJSONObject(i);
			if (je.getInt("numero") != i + 1) {
				throw new PersistenciaException("Los encuentros de " + nombre + " en " + archivo + " deben ir numerados de 1 a n");
			}
			InscripcionTorneo local = torneo.buscarInscripcion(je.getString("local"));
			InscripcionTorneo visitante = null;
			if (!je.isNull("visitante")) {
				visitante = torneo.buscarInscripcion(je.getString("visitante"));
			}
			EncuentroTorneo encuentro = new EncuentroTorneo(je.getInt("numero"), je.getInt("ronda"), local, visitante);
			if (visitante != null && je.getBoolean("jugado")) {
				encuentro.registrarResultado(je.getInt("marcadorLocal"), je.getInt("marcadorVisitante"));
			}
			lista.add(encuentro);
		}
		torneo.agregarEncuentros(lista);
	}

	private TipoTorneo cargarTipo(JSONObject jt, String archivo) throws PersistenciaException {
		String nombre = jt.getString("nombre");
		if (nombre.equals("Recreativo")) {
			return new Recreativo(jt.getInt("porcentajeBono"));
		}
		if (nombre.equals("Competitivo")) {
			return new Competitivo(jt.getDouble("tarifa"));
		}
		throw new PersistenciaException("Tipo de torneo desconocido en " + archivo + ": " + nombre);
	}

	private FormatoTorneo cargarFormato(String nombre, String archivo) throws PersistenciaException {
		if (nombre.equals("TodosContraTodos")) {
			return new TodosContraTodos();
		}
		if (nombre.equals("EliminacionDirecta")) {
			return new EliminacionDirecta();
		}
		throw new PersistenciaException("Formato de torneo desconocido en " + archivo + ": " + nombre);
	}

	private EstadoTorneo cargarEstado(String nombre, String archivo) throws PersistenciaException {
		for (EstadoTorneo estado : EstadoTorneo.values()) {
			if (estado.name().equals(nombre)) {
				return estado;
			}
		}
		throw new PersistenciaException("Estado de torneo desconocido en " + archivo + ": " + nombre);
	}

	private InscripcionTorneo cargarInscripcion(JSONObject ji, Club club) throws NoEncontradoException {
		LocalDate fecha = LocalDate.parse(ji.getString("fecha"));
		boolean preferencial = ji.getBoolean("preferencial");
		InscripcionTorneo inscripcion;
		if (ji.has("equipo")) {
			Equipo equipo = club.buscarEquipo(ji.getString("equipo"));
			inscripcion = new InscripcionEquipo(equipo, fecha, preferencial);
		} else {
			Usuario jugador = club.buscarUsuario(ji.getString("jugador"));
			Usuario pareja = null;
			if (!ji.isNull("pareja")) {
				pareja = club.buscarUsuario(ji.getString("pareja"));
			}
			inscripcion = new InscripcionIndividual(jugador, pareja, fecha, preferencial);
		}
		inscripcion.setValorPagado(ji.getDouble("valorPagado"));
		return inscripcion;
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray torneos = new JSONArray();
		for (Torneo torneo : club.getTorneos()) {
			torneos.put(torneoAJSON(torneo));
		}

		JSONArray bonos = new JSONArray();
		for (BonoDescuento bono : club.getBonos()) {
			JSONObject jb = new JSONObject();
			jb.put("id", bono.getId());
			jb.put("dueno", bono.getDueno().getLogin());
			jb.put("porcentaje", bono.getPorcentaje());
			jb.put("torneo", bono.getTorneo());
			jb.put("usado", bono.isUsado());
			bonos.put(jb);
		}

		JSONObject json = new JSONObject();
		json.put("torneos", torneos);
		json.put("bonos", bonos);
		archivoJSON.escribir(archivo, json);
	}

	private JSONObject torneoAJSON(Torneo torneo) {
		JSONObject jt = new JSONObject();
		jt.put("nombre", torneo.getNombre());
		jt.put("fecha", torneo.getFecha().toString());
		jt.put("cupos", torneo.getCupos());
		jt.put("estado", torneo.getEstado().name());
		jt.put("preferencialesLiberados", torneo.isPreferencialesLiberados());

		if (torneo instanceof TorneoConjunto) {
			TorneoConjunto conjunto = (TorneoConjunto) torneo;
			jt.put("clase", "Conjunto");
			jt.put("deporte", conjunto.getDeporte().getNombre());
			jt.put("categoria", conjunto.getCategoria().getNombre());
		} else {
			TorneoIndividual individual = (TorneoIndividual) torneo;
			jt.put("clase", "Individual");
			jt.put("disciplina", individual.getDisciplina().getNombre());
			jt.put("nivel", individual.getNivel().name());
			jt.put("modalidad", individual.getModalidad().name());
		}

		JSONObject tipo = new JSONObject();
		tipo.put("nombre", torneo.getTipo().getNombre());
		if (torneo.getTipo() instanceof Recreativo) {
			tipo.put("porcentajeBono", ((Recreativo) torneo.getTipo()).getPorcentajeBono());
		} else {
			tipo.put("tarifa", ((Competitivo) torneo.getTipo()).getTarifa());
		}
		jt.put("tipo", tipo);
		jt.put("formato", torneo.getFormato() instanceof EliminacionDirecta ? "EliminacionDirecta" : "TodosContraTodos");

		JSONArray inscripciones = new JSONArray();
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			JSONObject ji = new JSONObject();
			if (inscripcion instanceof InscripcionEquipo) {
				ji.put("equipo", ((InscripcionEquipo) inscripcion).getEquipo().getNombre());
			} else {
				InscripcionIndividual individual = (InscripcionIndividual) inscripcion;
				ji.put("jugador", individual.getJugador().getLogin());
				ji.put("pareja", individual.getPareja() == null ? JSONObject.NULL : individual.getPareja().getLogin());
			}
			ji.put("fecha", inscripcion.getFecha().toString());
			ji.put("preferencial", inscripcion.isPreferencial());
			ji.put("valorPagado", inscripcion.getValorPagado());
			inscripciones.put(ji);
		}
		jt.put("inscripciones", inscripciones);

		JSONArray encuentros = new JSONArray();
		for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
			JSONObject je = new JSONObject();
			je.put("numero", encuentro.getNumero());
			je.put("ronda", encuentro.getRonda());
			je.put("local", encuentro.getLocal().getClave());
			je.put("visitante", encuentro.isPaseDirecto() ? JSONObject.NULL : encuentro.getVisitante().getClave());
			je.put("jugado", encuentro.isJugado());
			je.put("marcadorLocal", encuentro.getMarcadorLocal());
			je.put("marcadorVisitante", encuentro.getMarcadorVisitante());
			encuentros.put(je);
		}
		jt.put("encuentros", encuentros);
		return jt;
	}
}
