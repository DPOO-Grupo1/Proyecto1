package consola.deportes;

import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import excepciones.ClubException;
import logica.GestorDeportes;
import modelo.deportes.JugadorEquipo;
import modelo.deportes.Suspension;
import modelo.partidos.Estadistica;
import modelo.partidos.Partido;

/**
 * Menú de deportes y competencias para el socio: su información deportiva (RF1)
 * y las tablas y clasificaciones de las competencias (RF2).
 */
public class MenuDeportesSocio extends MenuConsultaCompetencias {

	private GestorDeportes gestorDeportes;

	public MenuDeportesSocio(Consola consola) {
		super(consola);
		gestorDeportes = new GestorDeportes(club);
	}

	@Override
	public String getTitulo() {
		return "Deportes y competencias";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Ver mi información deportiva");
		opciones.add("Ver la tabla de posiciones de una competencia");
		opciones.add("Ver goleadores y jugadores destacados de una competencia");
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			verInformacion();
		} else if (opcion == 2) {
			verTabla();
		} else if (opcion == 3) {
			verClasificaciones();
		}
	}

	// RF1
	private void verInformacion() throws ClubException {
		List<JugadorEquipo> participaciones = gestorDeportes.consultarInformacion(usuario);
		if (participaciones.isEmpty()) {
			System.out.println("No está inscrito en ningún equipo.");
			return;
		}
		for (JugadorEquipo participacion : participaciones) {
			System.out.println(participacion.getEquipo() + ", " + participacion.getPosicion() + " #"
					+ participacion.getNumeroCamiseta() + (participacion.estaActivo() ? "" : " (retirado)"));
			for (Partido partido : gestorDeportes.consultarPartidosDe(usuario, participacion)) {
				Estadistica estadistica = partido.getEstadisticaDe(participacion);
				System.out.println("   " + partido + " | " + (estadistica == null ? "no jugó" : estadistica));
			}
			Suspension suspension = participacion.getSuspensionPendiente();
			if (suspension != null) {
				System.out.println("   Suspendido: " + suspension);
			}
		}
	}
}
