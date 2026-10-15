package modelo.competencias;

import modelo.deportes.JugadorEquipo;

/**
 * Una fila de goleadores o destacados. Tampoco se guarda.
 */
public class FilaClasificacion {

	private JugadorEquipo jugador;
	private int valor;

	public FilaClasificacion(JugadorEquipo jugador) {
		this.jugador = jugador;
		this.valor = 0;
	}

	public void sumar(int cantidad) {
		valor += cantidad;
	}

	public JugadorEquipo getJugador() {
		return jugador;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return jugador.getSocio().getNombreCompleto() + " (" + jugador.getEquipo().getNombre() + "): " + valor;
	}
}
