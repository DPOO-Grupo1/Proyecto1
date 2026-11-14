package modelo.torneos;

/**
 * Un encuentro de un torneo. Si no tiene visitante es un pase directo: el local
 * pasa a la siguiente ronda sin jugar (pasa en eliminación cuando los participantes son impares).
 */
public class EncuentroTorneo {

	private int numero;
	private int ronda;
	private InscripcionTorneo local;
	private InscripcionTorneo visitante;
	private int marcadorLocal;
	private int marcadorVisitante;
	private boolean jugado;

	/**
	 * El visitante va en null para un pase directo, que queda jugado de una vez.
	 */
	public EncuentroTorneo(int numero, int ronda, InscripcionTorneo local, InscripcionTorneo visitante) {
		this.numero = numero;
		this.ronda = ronda;
		this.local = local;
		this.visitante = visitante;
		this.marcadorLocal = 0;
		this.marcadorVisitante = 0;
		this.jugado = visitante == null;
	}

	public int getNumero() {
		return numero;
	}

	public int getRonda() {
		return ronda;
	}

	public InscripcionTorneo getLocal() {
		return local;
	}

	/**
	 * Null si es un pase directo.
	 */
	public InscripcionTorneo getVisitante() {
		return visitante;
	}

	public boolean isPaseDirecto() {
		return visitante == null;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public boolean isJugado() {
		return jugado;
	}

	public void registrarResultado(int marcadorLocal, int marcadorVisitante) {
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
		this.jugado = true;
	}

	public boolean participa(InscripcionTorneo inscripcion) {
		return local == inscripcion || visitante == inscripcion;
	}

	/**
	 * Null si no se ha jugado o si fue empate.
	 */
	public InscripcionTorneo getGanador() {
		if (!jugado) {
			return null;
		}
		if (isPaseDirecto() || marcadorLocal > marcadorVisitante) {
			return local;
		}
		if (marcadorVisitante > marcadorLocal) {
			return visitante;
		}
		return null;
	}

	@Override
	public String toString() {
		String texto = "Ronda " + ronda + ", encuentro #" + numero + ": ";
		if (isPaseDirecto()) {
			return texto + local.getNombre() + " pasa directo a la siguiente ronda";
		}
		texto += local.getNombre() + " vs " + visitante.getNombre();
		if (jugado) {
			return texto + " " + marcadorLocal + "-" + marcadorVisitante;
		}
		return texto + " (pendiente)";
	}
}
