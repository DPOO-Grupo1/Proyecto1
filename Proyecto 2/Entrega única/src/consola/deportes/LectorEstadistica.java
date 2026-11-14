package consola.deportes;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import modelo.deportes.JugadorEquipo;
import modelo.partidos.Estadistica;
import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.partidos.EstadisticaVoleibol;
import modelo.partidos.Partido;

/**
 * Pide los datos de una estadística según el deporte del jugador, porque cada deporte
 * guarda cosas distintas. Lo usan los menús del entrenador (partidos internos) y del
 * administrador (partidos de competencia).
 */
public class LectorEstadistica {

	private Consola consola;

	public LectorEstadistica(Consola consola) {
		this.consola = consola;
	}

	/**
	 * Los jugadores que pueden jugar el partido, para no pedir la estadística de un suspendido.
	 * Muestra por qué se deja por fuera a cada uno; el gestor igual vuelve a revisar.
	 */
	public static List<JugadorEquipo> habilitados(List<JugadorEquipo> jugadores, Partido partido) {
		List<JugadorEquipo> habilitados = new ArrayList<JugadorEquipo>();
		for (JugadorEquipo jugador : jugadores) {
			if (jugador.estaHabilitadoPara(partido)) {
				habilitados.add(jugador);
			} else {
				System.out.println("No aparece " + jugador + ": " + jugador.motivoNoHabilitadoPara(partido));
			}
		}
		return habilitados;
	}

	/**
	 * Null si el club no tiene estadísticas para ese deporte.
	 */
	public Estadistica leer(JugadorEquipo jugador) {
		String deporte = jugador.getEquipo().getDeporte().getNombre();
		if (deporte.equalsIgnoreCase(EstadisticaFutbol.DEPORTE)) {
			int goles = consola.leerEntero("Goles", 0, 50);
			int asistencias = consola.leerEntero("Asistencias", 0, 50);
			int amarillas = consola.leerEntero("Tarjetas amarillas", 0, 2);
			int rojas = consola.leerEntero("Tarjetas rojas", 0, 1);
			return new EstadisticaFutbol(jugador, goles, asistencias, amarillas, rojas);
		}
		if (deporte.equalsIgnoreCase(EstadisticaBaloncesto.DEPORTE)) {
			int puntos = consola.leerEntero("Puntos", 0, 200);
			int rebotes = consola.leerEntero("Rebotes", 0, 100);
			return new EstadisticaBaloncesto(jugador, puntos, rebotes);
		}
		if (deporte.equalsIgnoreCase(EstadisticaVoleibol.DEPORTE)) {
			int aces = consola.leerEntero("Aces", 0, 100);
			int bloqueos = consola.leerEntero("Bloqueos", 0, 100);
			return new EstadisticaVoleibol(jugador, aces, bloqueos);
		}
		System.out.println("No hay estadísticas definidas para " + deporte + ".");
		return null;
	}
}
