package modelo.torneos;

import modelo.usuarios.Usuario;

/**
 * Bono que gana cada jugador del ganador de un torneo recreativo. Se usa una sola vez
 * en la tienda o la cafetería y no se suma con otros descuentos.
 */
public class BonoDescuento {

	private int id;
	private Usuario dueno;
	private int porcentaje;
	private String torneo;
	private boolean usado;

	public BonoDescuento(int id, Usuario dueno, int porcentaje, String torneo) {
		this.id = id;
		this.dueno = dueno;
		this.porcentaje = porcentaje;
		this.torneo = torneo;
		this.usado = false;
	}

	public int getId() {
		return id;
	}

	public Usuario getDueno() {
		return dueno;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	/**
	 * El porcentaje como fracción (20 % es 0.2), para multiplicar por el valor de la compra.
	 */
	public double getFraccion() {
		return porcentaje / 100.0;
	}

	/**
	 * Nombre del torneo donde se ganó.
	 */
	public String getTorneo() {
		return torneo;
	}

	public boolean isUsado() {
		return usado;
	}

	/**
	 * Solo lo puede usar su dueño y una sola vez.
	 */
	public boolean puedeUsar(Usuario quien) {
		return !usado && quien == dueno;
	}

	public void usar() {
		usado = true;
	}

	@Override
	public String toString() {
		return "Bono #" + id + ": " + porcentaje + "% de descuento (torneo " + torneo + ")"
				+ (usado ? ", ya usado" : ", disponible");
	}
}
