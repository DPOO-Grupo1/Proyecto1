package persistencia;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;
import modelo.usuarios.Administrador;
import modelo.usuarios.Cliente;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Guarda los usuarios en usuarios.json: administradores, socios y empleados.
 * Es del núcleo común; las áreas B y C le agregan lo suyo (mensualidades, códigos de descuento).
 */
public class PersistenciaUsuarios implements IPersistenciaArea {

	private ArchivoJSON archivoJSON = new ArchivoJSON();

	@Override
	public void cargar(String archivo, Club club) throws PersistenciaException {
		JSONObject json = archivoJSON.leer(archivo);
		try {
			JSONArray administradores = json.getJSONArray("administradores");
			for (int i = 0; i < administradores.length(); i++) {
				JSONObject ja = administradores.getJSONObject(i);
				agregar(club, new Administrador(ja.getString("login"), ja.getString("password")), archivo);
			}

			JSONArray socios = json.getJSONArray("socios");
			for (int i = 0; i < socios.length(); i++) {
				JSONObject js = socios.getJSONObject(i);
				Socio socio = new Socio(js.getString("login"), js.getString("password"), js.getString("nombre"),
						js.getString("apellidos"), js.getString("socioID"), LocalDate.parse(js.getString("fechaNacimiento")));
				socio.setPuntosFidelidad(js.getInt("puntosFidelidad"));
				agregar(club, socio, archivo);
			}

			JSONArray empleados = json.getJSONArray("empleados");
			for (int i = 0; i < empleados.length(); i++) {
				JSONObject je = empleados.getJSONObject(i);
				Empleado empleado = crearEmpleado(je, archivo);
				empleado.setPuntosFidelidad(je.getInt("puntosFidelidad"));
				// Un entrenador también puede ser socio.
				if (empleado instanceof Entrenador && !je.isNull("socioID")) {
					((Entrenador) empleado).setSocio(club.buscarSocio(je.getString("socioID")));
				}
				agregar(club, empleado, archivo);
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Falta un dato o tiene un tipo incorrecto en " + archivo + ": " + e.getMessage(), e);
		} catch (DateTimeParseException e) {
			throw new PersistenciaException("Fecha mal escrita en " + archivo + " (debe ser AAAA-MM-DD): " + e.getParsedString(), e);
		} catch (NoEncontradoException e) {
			throw new PersistenciaException("Referencia rota en " + archivo + ": " + e.getMessage(), e);
		}
	}

	private Empleado crearEmpleado(JSONObject je, String archivo) throws PersistenciaException {
		String tipo = je.getString("tipo");
		String login = je.getString("login");
		String password = je.getString("password");
		String nombre = je.getString("nombre");
		String apellidos = je.getString("apellidos");
		int id = je.getInt("idEmpleado");
		if (tipo.equals("Entrenador")) {
			return new Entrenador(login, password, nombre, apellidos, id);
		}
		if (tipo.equals("Fisioterapeuta")) {
			return new Fisioterapeuta(login, password, nombre, apellidos, id);
		}
		if (tipo.equals("Empleado")) {
			return new Empleado(login, password, nombre, apellidos, id);
		}
		throw new PersistenciaException("Tipo de empleado desconocido en " + archivo + ": " + tipo);
	}

	private void agregar(Club club, Usuario usuario, String archivo) throws PersistenciaException {
		if (club.existeUsuario(usuario.getLogin())) {
			throw new PersistenciaException("Login repetido en " + archivo + ": " + usuario.getLogin());
		}
		club.agregarUsuario(usuario);
	}

	@Override
	public void guardar(String archivo, Club club) throws PersistenciaException {
		JSONArray administradores = new JSONArray();
		JSONArray socios = new JSONArray();
		JSONArray empleados = new JSONArray();

		for (Usuario usuario : club.getUsuarios()) {
			JSONObject ju = new JSONObject();
			ju.put("login", usuario.getLogin());
			ju.put("password", usuario.getPassword());
			if (usuario instanceof Administrador) {
				administradores.put(ju);
			} else {
				Cliente cliente = (Cliente) usuario;
				ju.put("nombre", cliente.getNombre());
				ju.put("apellidos", cliente.getApellidos());
				ju.put("puntosFidelidad", cliente.getPuntosFidelidad());
				if (usuario instanceof Socio) {
					Socio socio = (Socio) usuario;
					ju.put("socioID", socio.getSocioID());
					ju.put("fechaNacimiento", socio.getFechaNacimiento().toString());
					socios.put(ju);
				} else {
					Empleado empleado = (Empleado) usuario;
					ju.put("idEmpleado", empleado.getIdEmpleado());
					ju.put("socioID", JSONObject.NULL);
					if (empleado instanceof Entrenador) {
						ju.put("tipo", "Entrenador");
						Socio tambienSocio = ((Entrenador) empleado).getSocio();
						if (tambienSocio != null) {
							ju.put("socioID", tambienSocio.getSocioID());
						}
					} else if (empleado instanceof Fisioterapeuta) {
						ju.put("tipo", "Fisioterapeuta");
					} else {
						ju.put("tipo", "Empleado");
					}
					empleados.put(ju);
				}
			}
		}

		JSONObject json = new JSONObject();
		json.put("administradores", administradores);
		json.put("socios", socios);
		json.put("empleados", empleados);
		archivoJSON.escribir(archivo, json);
	}
}
