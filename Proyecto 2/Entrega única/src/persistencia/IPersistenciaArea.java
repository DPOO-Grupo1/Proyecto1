package persistencia;

import excepciones.PersistenciaException;
import logica.Club;

/**
 * Carga y guarda en un archivo los datos de un área del club.
 * Hay una implementación por archivo.
 */
public interface IPersistenciaArea {

	void cargar(String archivo, Club club) throws PersistenciaException;

	void guardar(String archivo, Club club) throws PersistenciaException;
}
