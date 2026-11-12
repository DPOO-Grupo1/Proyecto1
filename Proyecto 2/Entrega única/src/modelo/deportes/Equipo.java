package modelo.deportes;

import java.util.ArrayList;
import java.util.List;

import excepciones.NoEncontradoException;
import modelo.partidos.Partido;
import modelo.usuarios.Entrenador;

/**
 * Equipo de un deporte y una categoría. Al darlo de baja queda inactivo, no se borra.
 */
public class Equipo {

	private String nombre;
	private int cupoMaximo;
	private boolean activo;
	private DeporteEquipo deporte;
	private Categoria categoria;
	private Entrenador entrenador;
	private List<JugadorEquipo> jugadores;

	public Equipo(String nombre, DeporteEquipo deporte, Categoria categoria, int cupoMaximo) {
		this.nombre = nombre;
		this.deporte = deporte;
		this.categoria = categoria;
		this.cupoMaximo = cupoMaximo;
		this.activo = true;
		this.entrenador = null;
		this.jugadores = new ArrayList<JugadorEquipo>();
	}

	public String getNombre() {
		return nombre;
	}

	public int getCupoMaximo() {
		return cupoMaximo;
	}

	public DeporteEquipo getDeporte() {
		return deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public Entrenador getEntrenador() {
		return entrenador;
	}

	public void setEntrenador(Entrenador entrenador) {
		this.entrenador = entrenador;
	}

	public boolean estaActivo() {
		return activo;
	}

	/**
	 * Incluye a los jugadores inactivos.
	 */
	public List<JugadorEquipo> getJugadores() {
		return jugadores;
	}

	public List<JugadorEquipo> getJugadoresActivos() {
		List<JugadorEquipo> activos = new ArrayList<JugadorEquipo>();
		for (JugadorEquipo jugador : jugadores) {
			if (jugador.estaActivo()) {
				activos.add(jugador);
			}
		}
		return activos;
	}

	public boolean tieneCupo() {
		return getJugadoresActivos().size() < cupoMaximo;
	}

	/**
	 * Solo cuenta a los jugadores activos, así un número retirado queda libre.
	 */
	public boolean numeroDisponible(int numero) {
		for (JugadorEquipo jugador : getJugadoresActivos()) {
			if (jugador.getNumeroCamiseta() == numero) {
				return false;
			}
		}
		return true;
	}

	public void agregarJugador(JugadorEquipo jugador) {
		jugadores.add(jugador);
	}

	/**
	 * Busca entre los jugadores activos.
	 */
	public JugadorEquipo buscarJugador(int numero) throws NoEncontradoException {
		for (JugadorEquipo jugador : getJugadoresActivos()) {
			if (jugador.getNumeroCamiseta() == numero) {
				return jugador;
			}
		}
		throw new NoEncontradoException("El equipo " + nombre + " no tiene un jugador activo con el número " + numero);
	}

	public int contarHabilitadosPara(Partido partido) {
		int habilitados = 0;
		for (JugadorEquipo jugador : jugadores) {
			if (jugador.estaHabilitadoPara(partido)) {
				habilitados++;
			}
		}
		return habilitados;
	}

	/**
	 * Busca la participación de un socio en el equipo (activa o no). Si el socio
	 * estuvo dos veces en el equipo, devuelve la más reciente. Lo usa la persistencia.
	 */
	public JugadorEquipo buscarJugadorPorSocio(String socioID) throws NoEncontradoException {
		for (int i = jugadores.size() - 1; i >= 0; i--) {
			JugadorEquipo jugador = jugadores.get(i);
			if (jugador.getSocio().getSocioID().equals(socioID)) {
				return jugador;
			}
		}
		throw new NoEncontradoException("El socio " + socioID + " no ha jugado en " + nombre);
	}

	/**
	 * También desactiva a todos sus jugadores.
	 */
	public void desactivar() {
		activo = false;
		for (JugadorEquipo jugador : jugadores) {
			jugador.desactivar();
		}
	}

	@Override
	public String toString() {
		return nombre + " (" + deporte.getNombre() + ", " + categoria.getNombre() + ")";
	}
}
