package modelo.entrenamiento;

import excepciones.DatoInvalidoException;

/**
 * Evaluación formativa del desempeño técnico de un socio en una sesión de entrenamiento.
 * La calificación es numérica en la escala de 1 a 5.
 */
public class Evaluacion {

	private int nota;
	private String comentario;

	public Evaluacion(int nota, String comentario) throws DatoInvalidoException {
		if (nota < 1 || nota > 5) {
			throw new DatoInvalidoException("La nota de evaluación debe estar entre 1 y 5 (recibido: " + nota + ")");
		}
		this.nota = nota;
		this.comentario = comentario != null ? comentario.trim() : "";
	}

	public int getNota() {
		return nota;
	}

	public String getComentario() {
		return comentario;
	}

	@Override
	public String toString() {
		return "Nota: " + nota + "/5" + (comentario.isEmpty() ? "" : " - \"" + comentario + "\"");
	}
}
