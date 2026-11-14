package modelo.torneos;

import java.time.LocalDate;

import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;

/**
 * Torneo de un deporte de conjunto en una categoría, por ejemplo un mini campeonato
 * de fútbol Sub-15. Se inscriben equipos completos.
 */
public class TorneoConjunto extends Torneo {

	private DeporteEquipo deporte;
	private Categoria categoria;

	public TorneoConjunto(String nombre, LocalDate fecha, int cupos, TipoTorneo tipo, FormatoTorneo formato,
			DeporteEquipo deporte, Categoria categoria) {
		super(nombre, fecha, cupos, tipo, formato);
		this.deporte = deporte;
		this.categoria = categoria;
	}

	public DeporteEquipo getDeporte() {
		return deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	/**
	 * Usa los puntos del deporte, igual que en las competencias.
	 */
	@Override
	public int puntosPara(int propios, int rival) {
		return deporte.puntosPara(propios, rival);
	}

	@Override
	public boolean admiteEmpate() {
		return deporte.isAdmiteEmpate();
	}

	@Override
	public String getDescripcion() {
		return deporte.getNombre() + " " + categoria.getNombre();
	}
}
