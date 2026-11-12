package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

import excepciones.PersistenciaException;

/**
 * Lee y escribe un archivo JSON completo, como ArchivoPlano del ejemplo de clase.
 * Siempre usa UTF-8 para que las tildes se lean igual en cualquier sistema operativo.
 */
public class ArchivoJSON {

	public JSONObject leer(String ruta) throws PersistenciaException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), StandardCharsets.UTF_8));
			String texto = "";
			String linea;
			while ((linea = br.readLine()) != null) {
				texto += linea + "\n";
			}
			br.close();
			return new JSONObject(texto);
		} catch (FileNotFoundException e) {
			throw new PersistenciaException("No existe el archivo " + ruta, e);
		} catch (IOException e) {
			throw new PersistenciaException("No se pudo leer el archivo " + ruta, e);
		} catch (JSONException e) {
			throw new PersistenciaException("El archivo " + ruta + " no es un JSON válido: " + e.getMessage(), e);
		}
	}

	public void escribir(String ruta, JSONObject json) throws PersistenciaException {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ruta), StandardCharsets.UTF_8));
			bw.write(json.toString(2));
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			throw new PersistenciaException("No se pudo escribir el archivo " + ruta, e);
		}
	}
}
