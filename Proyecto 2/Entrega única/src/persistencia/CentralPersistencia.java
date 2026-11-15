package persistencia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import excepciones.NoEncontradoException;
import excepciones.PersistenciaException;
import logica.Club;

/**
 * Carga y guarda todo el club. Cada parte del club tiene su archivo dentro de la carpeta de datos,
 * y se cargan en orden porque unas dependen de otras (los equipos necesitan a los socios,
 * las competencias necesitan a los equipos, los torneos necesitan casi todo).
 */
public class CentralPersistencia {

	/** Nombre de la carpeta de datos, que va junto al proyecto (fuera de src). */
	public static final String CARPETA_DATOS = "datos";

	private List<IPersistenciaArea> areas;
	private List<String> archivos;

	public CentralPersistencia() {
		areas = new ArrayList<IPersistenciaArea>();
		archivos = new ArrayList<String>();
		agregarArea(new PersistenciaUsuarios(), "usuarios.json");
		agregarArea(new PersistenciaDeportes(), "deportes.json");
		agregarArea(new PersistenciaCompetencias(), "competencias.json");
		agregarArea(new PersistenciaAcademias(), "academias.json");
		agregarArea(new PersistenciaEntrenamiento(), "entrenamiento.json");
		agregarArea(new PersistenciaTienda(), "tienda.json");
		agregarArea(new PersistenciaPersonal(), "personal.json");
		agregarArea(new PersistenciaTorneos(), "torneos.json");
	}

	private void agregarArea(IPersistenciaArea area, String archivo) {
		areas.add(area);
		archivos.add(archivo);
	}

	/**
	 * Busca la carpeta datos/ sin importar desde dónde se ejecute el programa:
	 * desde el proyecto en Eclipse, desde la raíz del repositorio o desde un terminal.
	 * Si no la encuentra, devuelve "datos" y la carga fallará con un mensaje claro.
	 */
	public static String carpetaPorDefecto() {
		String[] candidatas = { CARPETA_DATOS, "Entrega única/" + CARPETA_DATOS,
				"Proyecto 2/Entrega única/" + CARPETA_DATOS, "../" + CARPETA_DATOS };
		for (String candidata : candidatas) {
			if (new File(candidata, "usuarios.json").exists()) {
				return candidata;
			}
		}
		return CARPETA_DATOS;
	}

	/**
	 * Crea un club nuevo con los datos de los archivos de la carpeta.
	 */
	public Club cargarClub(String carpeta) throws PersistenciaException {
		if (!new File(carpeta).isDirectory()) {
			throw new PersistenciaException("No se encuentra la carpeta de datos '" + carpeta
					+ "'. El programa debe ejecutarse desde la carpeta del proyecto (la que contiene src y datos)");
		}
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
