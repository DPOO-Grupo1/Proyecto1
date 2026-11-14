package modelo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.NoEncontradoException;
import modelo.usuarios.Usuario;

/**
 * Torneo interno del club. Puede ser de un deporte de conjunto (se inscriben equipos)
 * o de una disciplina individual (se inscriben jugadores o parejas).
 * El tipo (recreativo o competitivo) decide la tarifa y el premio, y el formato decide
 * cómo se arman los encuentros y las posiciones.
 */
public abstract class Torneo {

	/**
	 * Parte de los cupos que se reserva para los mejores del ranking.
	 */
	public static final int PORCENTAJE_PREFERENCIAL = 20;

	protected String nombre;
	protected LocalDate fecha;
	protected int cupos;
	protected TipoTorneo tipo;
	protected FormatoTorneo formato;
	protected EstadoTorneo estado;
	protected boolean preferencialesLiberados;
	protected List<InscripcionTorneo> inscripciones;
	protected List<EncuentroTorneo> encuentros;

	public Torneo(String nombre, LocalDate fecha, int cupos, TipoTorneo tipo, FormatoTorneo formato) {
		this.nombre = nombre;
		this.fecha = fecha;
		this.cupos = cupos;
		this.tipo = tipo;
		this.formato = formato;
		this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;
		this.preferencialesLiberados = false;
		this.inscripciones = new ArrayList<InscripcionTorneo>();
		this.encuentros = new ArrayList<EncuentroTorneo>();
	}

	public String getNombre() {
		return nombre;
	}

	/**
	 * Día en que se juega el torneo.
	 */
	public LocalDate getFecha() {
		return fecha;
	}

	public int getCupos() {
		return cupos;
	}

	public TipoTorneo getTipo() {
		return tipo;
	}

	public FormatoTorneo getFormato() {
		return formato;
	}

	public EstadoTorneo getEstado() {
		return estado;
	}

	public void setEstado(EstadoTorneo estado) {
		this.estado = estado;
	}

	public boolean estaAbierto() {
		return estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS;
	}

	// Cupos

	/**
	 * El 20 % de los cupos redondeado hacia arriba. Sumar 99 antes de dividir por 100
	 * hace el redondeo hacia arriba con enteros: 10 cupos dan 2, 6 cupos dan 2.
	 */
	public int getCuposReservados() {
		return (cupos * PORCENTAJE_PREFERENCIAL + 99) / 100;
	}

	public boolean isPreferencialesLiberados() {
		return preferencialesLiberados;
	}

	/**
	 * Los cupos preferenciales que no se llenaron pasan a ser regulares.
	 */
	public void liberarCuposPreferenciales() {
		preferencialesLiberados = true;
	}

	/**
	 * Cuántos cupos siguen reservados para los mejores del ranking. Es 0 si ya se liberaron.
	 */
	public int getCuposPreferencialesLibres() {
		if (preferencialesLiberados) {
			return 0;
		}
		int libres = getCuposReservados() - contarPreferenciales();
		return libres > 0 ? libres : 0;
	}

	/**
	 * Cupos que puede tomar cualquiera: los que no están ocupados ni reservados.
	 */
	public int getCuposRegularesLibres() {
		return cupos - inscripciones.size() - getCuposPreferencialesLibres();
	}

	public int contarPreferenciales() {
		int total = 0;
		for (InscripcionTorneo inscripcion : inscripciones) {
			if (inscripcion.isPreferencial()) {
				total++;
			}
		}
		return total;
	}

	// Inscripciones

	public List<InscripcionTorneo> getInscripciones() {
		return inscripciones;
	}

	public void agregarInscripcion(InscripcionTorneo inscripcion) {
		inscripciones.add(inscripcion);
	}

	/**
	 * Al salirse, el cupo queda libre otra vez.
	 */
	public void retirarInscripcion(InscripcionTorneo inscripcion) {
		inscripciones.remove(inscripcion);
	}

	/**
	 * La inscripción donde juega el usuario, o null si no está inscrito.
	 */
	public InscripcionTorneo buscarInscripcionDe(Usuario usuario) {
		for (InscripcionTorneo inscripcion : inscripciones) {
			if (inscripcion.incluye(usuario)) {
				return inscripcion;
			}
		}
		return null;
	}

	public InscripcionTorneo buscarInscripcion(String clave) throws NoEncontradoException {
		for (InscripcionTorneo inscripcion : inscripciones) {
			if (inscripcion.getClave().equalsIgnoreCase(clave)) {
				return inscripcion;
			}
		}
		throw new NoEncontradoException("'" + clave + "' no está inscrito en el torneo " + nombre);
	}

	/**
	 * Suma de lo que pagaron todos los inscritos.
	 */
	public double getRecaudo() {
		double total = 0;
		for (InscripcionTorneo inscripcion : inscripciones) {
			total += inscripcion.getValorPagado();
		}
		return total;
	}

	// Encuentros

	public List<EncuentroTorneo> getEncuentros() {
		return encuentros;
	}

	public void agregarEncuentros(List<EncuentroTorneo> nuevos) {
		encuentros.addAll(nuevos);
	}

	public EncuentroTorneo buscarEncuentro(int numero) throws NoEncontradoException {
		for (EncuentroTorneo encuentro : encuentros) {
			if (encuentro.getNumero() == numero) {
				return encuentro;
			}
		}
		throw new NoEncontradoException("El torneo " + nombre + " no tiene el encuentro #" + numero);
	}

	/**
	 * La ronda más alta que se ha creado, o 0 si el torneo no ha empezado.
	 */
	public int getRondaActual() {
		int ronda = 0;
		for (EncuentroTorneo encuentro : encuentros) {
			if (encuentro.getRonda() > ronda) {
				ronda = encuentro.getRonda();
			}
		}
		return ronda;
	}

	public List<EncuentroTorneo> getEncuentrosDeRonda(int ronda) {
		List<EncuentroTorneo> deRonda = new ArrayList<EncuentroTorneo>();
		for (EncuentroTorneo encuentro : encuentros) {
			if (encuentro.getRonda() == ronda) {
				deRonda.add(encuentro);
			}
		}
		return deRonda;
	}

	public List<EncuentroTorneo> getEncuentrosPendientes() {
		List<EncuentroTorneo> pendientes = new ArrayList<EncuentroTorneo>();
		for (EncuentroTorneo encuentro : encuentros) {
			if (!encuentro.isJugado()) {
				pendientes.add(encuentro);
			}
		}
		return pendientes;
	}

	// Resultados

	public List<FilaTorneo> calcularPosiciones() {
		return formato.calcularPosiciones(this);
	}

	/**
	 * Null mientras el torneo no termine.
	 */
	public InscripcionTorneo getGanador() {
		return formato.getGanador(this);
	}

	// Lo que cambia entre torneos de conjunto e individuales

	/**
	 * Puntos que da un resultado en la tabla.
	 */
	public abstract int puntosPara(int propios, int rival);

	public abstract boolean admiteEmpate();

	/**
	 * Deporte y categoría, o disciplina, nivel y modalidad.
	 */
	public abstract String getDescripcion();

	@Override
	public String toString() {
		return nombre + " (" + getDescripcion() + ", " + tipo + ", " + formato + "), " + fecha + ", " + estado + ", "
				+ inscripciones.size() + "/" + cupos + " inscritos";
	}
}
