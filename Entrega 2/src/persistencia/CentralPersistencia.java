package persistencia;

import java.util.ArrayList;
import java.util.List;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;

/**
 * Carga y guarda todo el club. Cada área tiene su archivo dentro de la carpeta de datos,
 * y se cargan en orden porque unas dependen de otras (los equipos necesitan a los socios,
 * las competencias necesitan a los equipos).
 */
public class CentralPersistencia {

	private List<IPersistenciaArea> areas;
	private List<String> archivos;

	public CentralPersistencia() {
		areas = new ArrayList<IPersistenciaArea>();
		archivos = new ArrayList<String>();
		agregarArea(new PersistenciaUsuarios(), "usuarios.json");
		agregarArea(new PersistenciaDeportes(), "deportes.json");
		agregarArea(new PersistenciaCompetencias(), "competencias.json");
		// Aquí van las de las áreas B y C, en orden: academias, entrenamiento, tienda, personal.
	}

	private void agregarArea(IPersistenciaArea area, String archivo) {
		areas.add(area);
		archivos.add(archivo);
	}

	/**
	 * Crea un club nuevo con los datos de los archivos de la carpeta.
	 */
	public Club cargarClub(String carpeta) throws PersistenciaException {
		Club club = new Club();
		for (int i = 0; i < areas.size(); i++) {
			areas.get(i).cargar(carpeta + "/" + archivos.get(i), club);
		}
		verificarIdsPartidos(club);
		return club;
	}

	public void guardarClub(Club club, String carpeta) throws PersistenciaException {
		for (int i = 0; i < areas.size(); i++) {
			areas.get(i).guardar(carpeta + "/" + archivos.get(i), club);
		}
	}

	// Los ids de partido tienen que ir de 1 a n, porque el siguiente id es n + 1.
	private void verificarIdsPartidos(Club club) throws PersistenciaException {
		int total = club.siguienteIdPartido() - 1;
		for (int id = 1; id <= total; id++) {
			try {
				club.buscarPartido(id);
			} catch (NoEncontradoException e) {
				throw new PersistenciaException("Los ids de partido deben ir de 1 a " + total + " sin saltos, y falta el " + id);
			}
		}
	}
}
