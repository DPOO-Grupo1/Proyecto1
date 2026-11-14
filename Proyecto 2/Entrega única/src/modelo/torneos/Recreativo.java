package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

import excepciones.DatoInvalidoException;
import modelo.usuarios.Usuario;

/**
 * Torneo sin costo de inscripción. Cada jugador del ganador recibe un bono de descuento
 * para la tienda o la cafetería.
 */
public class Recreativo extends TipoTorneo {

	private int porcentajeBono;

	public Recreativo(int porcentajeBono) {
		this.porcentajeBono = porcentajeBono;
	}

	public int getPorcentajeBono() {
		return porcentajeBono;
	}

	@Override
	public String getNombre() {
		return "Recreativo";
	}

	@Override
	public void validar() throws DatoInvalidoException {
		if (porcentajeBono <= 0 || porcentajeBono > 100) {
			throw new DatoInvalidoException("El porcentaje del bono debe estar entre 1 y 100");
		}
	}

	@Override
	public double tarifaPara(InscripcionTorneo inscripcion) {
		return 0;
	}

	@Override
	public List<BonoDescuento> crearBonos(Torneo torneo, InscripcionTorneo ganador, int primerId) {
		List<BonoDescuento> bonos = new ArrayList<BonoDescuento>();
		int id = primerId;
		for (Usuario jugador : ganador.getJugadores()) {
			bonos.add(new BonoDescuento(id, jugador, porcentajeBono, torneo.getNombre()));
			id++;
		}
		return bonos;
	}

	@Override
	public String describirPremio(Torneo torneo) {
		String texto = "Bono del " + porcentajeBono + "% en tienda o cafetería para cada jugador del ganador";
		InscripcionTorneo ganador = torneo.getGanador();
		if (ganador != null) {
			texto += ". Ganador: " + ganador.getNombre();
		}
		return texto;
	}

	@Override
	public String toString() {
		return "recreativo, bono del " + porcentajeBono + "%";
	}
}
