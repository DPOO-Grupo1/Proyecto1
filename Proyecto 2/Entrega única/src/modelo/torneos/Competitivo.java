package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

import excepciones.DatoInvalidoException;
import modelo.usuarios.Empleado;
import modelo.usuarios.Usuario;

/**
 * Torneo con tarifa de inscripción. El ganador recibe en efectivo una parte de lo recaudado.
 * Los empleados no pagan, pero tampoco reciben premio en efectivo.
 */
public class Competitivo extends TipoTorneo {

	/**
	 * Parte del recaudo que va al premio. El enunciado no dice cuánto; el resto
	 * cubre los gastos del club.
	 */
	public static final int PORCENTAJE_PREMIO = 70;

	private double tarifa;

	public Competitivo(double tarifa) {
		this.tarifa = tarifa;
	}

	public double getTarifa() {
		return tarifa;
	}

	@Override
	public String getNombre() {
		return "Competitivo";
	}

	@Override
	public void validar() throws DatoInvalidoException {
		if (tarifa <= 0) {
			throw new DatoInvalidoException("La tarifa de inscripción debe ser mayor a 0");
		}
	}

	@Override
	public double tarifaPara(InscripcionTorneo inscripcion) {
		return tarifa * inscripcion.contarTarifas();
	}

	/**
	 * El premio no da bonos.
	 */
	@Override
	public List<BonoDescuento> crearBonos(Torneo torneo, InscripcionTorneo ganador, int primerId) {
		return new ArrayList<BonoDescuento>();
	}

	public double calcularPremio(Torneo torneo) {
		return torneo.getRecaudo() * PORCENTAJE_PREMIO / 100;
	}

	/**
	 * Lo que recibe un jugador: el premio se reparte entre los jugadores del ganador
	 * y la parte de un empleado no se entrega.
	 */
	public double premioPara(Torneo torneo, Usuario jugador) {
		InscripcionTorneo ganador = torneo.getGanador();
		if (ganador == null || !ganador.incluye(jugador) || jugador instanceof Empleado) {
			return 0;
		}
		return calcularPremio(torneo) / ganador.getJugadores().size();
	}

	@Override
	public String describirPremio(Torneo torneo) {
		String texto = "Premio en efectivo de $" + (long) calcularPremio(torneo) + " (" + PORCENTAJE_PREMIO + "% de $"
				+ (long) torneo.getRecaudo() + " recaudados)";
		InscripcionTorneo ganador = torneo.getGanador();
		if (ganador != null) {
			texto += ". Ganador: " + ganador.getNombre();
			for (Usuario jugador : ganador.getJugadores()) {
				if (jugador instanceof Empleado) {
					texto += ". " + jugador.getNombreCompleto() + " es empleado y no recibe premio en efectivo";
				}
			}
		}
		return texto;
	}

	@Override
	public String toString() {
		return "competitivo, tarifa $" + (long) tarifa;
	}
}
