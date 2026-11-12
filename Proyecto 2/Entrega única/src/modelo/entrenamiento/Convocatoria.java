package modelo.entrenamiento;

import excepciones.DatoInvalidoException;
import modelo.usuarios.Socio;

/**
 * Convocatoria de un socio a una sesión de entrenamiento.
 * Registra su asistencia (asistió, faltó o llegó tarde) y su evaluación técnica (opcional).
 */
public class Convocatoria {

	private Socio socio;
	private SesionEntrenamiento sesion;
	private EstadoAsistencia asistencia;
	private Evaluacion evaluacion;

	public Convocatoria(Socio socio, SesionEntrenamiento sesion) {
		this.socio = socio;
		this.sesion = sesion;
		this.asistencia = EstadoAsistencia.PENDIENTE;
		this.evaluacion = null;
	}

	public Socio getSocio() {
		return socio;
	}

	public SesionEntrenamiento getSesion() {
		return sesion;
	}

	public EstadoAsistencia getAsistencia() {
		return asistencia;
	}

	public void setAsistencia(EstadoAsistencia asistencia) {
		this.asistencia = asistencia;
	}

	/**
	 * Registra la asistencia efectiva del convocado.
	 * Se rechaza si se intenta marcar como 'pendiente' (EP16.2b).
	 */
	public void registrarAsistencia(EstadoAsistencia nuevoEstado) throws DatoInvalidoException {
		if (nuevoEstado == null || nuevoEstado == EstadoAsistencia.PENDIENTE) {
			throw new DatoInvalidoException("El estado de asistencia debe ser 'asistió', 'faltó' o 'llegó tarde'");
		}
		this.asistencia = nuevoEstado;
	}

	public Evaluacion getEvaluacion() {
		return evaluacion;
	}

	public void asignarEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}

	public boolean tieneEvaluacion() {
		return evaluacion != null;
	}

	@Override
	public String toString() {
		return socio.getNombreCompleto() + " [" + asistencia + "]"
				+ (evaluacion != null ? " (" + evaluacion + ")" : "");
	}
}
