package modelo.torneos;

import java.util.List;

import excepciones.DatoInvalidoException;

/**
 * Recreativo o competitivo. Es una clase aparte (como Formato en las competencias)
 * porque lo que cambia entre los dos es la tarifa y el premio, y eso es igual
 * para torneos de conjunto e individuales.
 */
public abstract class TipoTorneo {

	public abstract String getNombre();

	/**
	 * Revisa que el valor que puso el administrador tenga sentido.
	 */
	public abstract void validar() throws DatoInvalidoException;

	/**
	 * Cuánto paga una inscripción.
	 */
	public abstract double tarifaPara(InscripcionTorneo inscripcion);

	/**
	 * Bonos que reciben los jugadores del ganador. Vacía si el premio no es un bono.
	 * Los ids empiezan en primerId.
	 */
	public abstract List<BonoDescuento> crearBonos(Torneo torneo, InscripcionTorneo ganador, int primerId);

	/**
	 * Texto con el premio para mostrar en el historial del torneo.
	 */
	public abstract String describirPremio(Torneo torneo);
}
