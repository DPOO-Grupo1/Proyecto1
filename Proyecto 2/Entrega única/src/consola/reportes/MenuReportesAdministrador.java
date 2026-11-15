package consola.reportes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import consola.Consola;
import consola.Menu;
import excepciones.ClubException;
import logica.GestorReportes;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.reportes.FiltroHistorial;
import modelo.reportes.Historial;
import modelo.reportes.Mensualidad;
import modelo.reportes.Periodo;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteFinanciero;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Reportes para el administrador: historial global (RF28), reportes financiero
 * y deportivo (RF34) y registro de mensualidades (RF35).
 */
public class MenuReportesAdministrador extends Menu {

	private GestorReportes gestor;

	public MenuReportesAdministrador(Consola consola) {
		super(consola);
		gestor = new GestorReportes(club);
	}

	@Override
	public String getTitulo() {
		return "Reportes y mensualidades";
	}

	@Override
	public List<String> getOpciones() {
		List<String> opciones = new ArrayList<String>();
		opciones.add("Consultar el historial global"); // 1
		opciones.add("Generar reporte financiero"); // 2
		opciones.add("Generar reporte deportivo"); // 3
		opciones.add("Registrar el pago de una mensualidad"); // 4
		return opciones;
	}

	@Override
	public void ejecutar(int opcion) throws ClubException {
		if (opcion == 1) {
			consultarHistorial();
		} else if (opcion == 2) {
			reporteFinanciero();
		} else if (opcion == 3) {
			reporteDeportivo();
		} else if (opcion == 4) {
			registrarMensualidad();
		}
	}

	// RF28: cada filtro es opcional; el que no se pide queda en null
	private void consultarHistorial() throws ClubException {
		LocalDate desde = null;
		LocalDate hasta = null;
		if (consola.confirmar("¿Filtrar por fechas?")) {
			desde = consola.leerFecha("Desde");
			hasta = consola.leerFecha("Hasta");
		}
		Socio socio = null;
		if (consola.confirmar("¿Filtrar por socio?")) {
			socio = club.buscarSocio(consola.leerTexto("Id del socio (por ejemplo B1)"));
		}
		Entrenador entrenador = null;
		if (consola.confirmar("¿Filtrar por entrenador?")) {
			entrenador = elegirEntrenador();
		}
		Equipo equipo = null;
		if (consola.confirmar("¿Filtrar por equipo?")) {
			equipo = consola.elegir("Equipo", club.getEquipos());
		}
		DeporteEquipo deporte = null;
		if (consola.confirmar("¿Filtrar por deporte?")) {
			deporte = consola.elegir("Deporte", club.getDeportes());
		}
		Categoria categoria = null;
		if (consola.confirmar("¿Filtrar por categoría?")) {
			categoria = elegirCategoria();
		}

		FiltroHistorial filtro = new FiltroHistorial(desde, hasta, equipo, socio, entrenador, deporte, categoria);
		Historial historial = gestor.consultarHistorial(usuario, filtro);
		consola.mostrarLista("Partidos internos y de competencia:", historial.getPartidos());
		consola.mostrarLista("Partidos de práctica:", historial.getPartidosPractica());
		consola.mostrarLista("Reservas:", historial.getReservas());
		consola.mostrarLista("Sesiones de entrenamiento:", historial.getSesiones());
	}

	// RF34
	private void reporteFinanciero() throws ClubException {
		Periodo periodo = elegirPeriodo();
		if (periodo == null) {
			return;
		}
		LocalDate desde = consola.leerFecha("Fecha de inicio");
		ReporteFinanciero reporte = gestor.generarReporteFinanciero(usuario, periodo, desde);
		System.out.println("Reporte financiero del " + reporte.getDesde() + " al " + reporte.getHasta() + ":");
		System.out.println("   Tienda:       neto $" + (long) reporte.getSubtotalTienda() + ", IVA $"
				+ (long) reporte.getImpuestoTienda() + ", total $" + (long) reporte.getTotalTienda());
		System.out.println("   Cafetería:    neto $" + (long) reporte.getSubtotalCafeteria() + ", impuesto al consumo $"
				+ (long) reporte.getImpuestoCafeteria() + ", total $" + (long) reporte.getTotalCafeteria());
		System.out.println("   Mensualidades: $" + (long) reporte.getTotalMensualidades());
		if (reporte.getTotalTienda() == 0 && reporte.getTotalCafeteria() == 0 && reporte.getTotalMensualidades() == 0) {
			System.out.println("   (no hubo movimientos en este periodo)");
		}
	}

	// RF34
	private void reporteDeportivo() throws ClubException {
		Periodo periodo = elegirPeriodo();
		if (periodo == null) {
			return;
		}
		LocalDate desde = consola.leerFecha("Fecha de inicio");
		ReporteDeportivo reporte = gestor.generarReporteDeportivo(usuario, periodo, desde);
		consola.mostrarLista("Reporte deportivo del " + reporte.getDesde() + " al " + reporte.getHasta() + ":",
				reporte.getResultados());
	}

	// RF35
	private void registrarMensualidad() throws ClubException {
		String socioID = consola.leerTexto("Id del socio (por ejemplo B1)");
		int anio = consola.leerEntero("Año", 2000, 2100);
		int mes = consola.leerEntero("Mes", 1, 12);
		double valor = consola.leerDecimal("Valor pagado", 0);
		Mensualidad mensualidad = gestor.registrarPago(usuario, socioID, YearMonth.of(anio, mes), valor);
		System.out.println("ACEPTADO: el socio " + socioID + " pagó " + mensualidad.getPeriodo() + " por $"
				+ (long) mensualidad.getValor() + " y quedó al día.");
	}

	private Periodo elegirPeriodo() {
		List<String> nombres = new ArrayList<String>();
		nombres.add("Diario");
		nombres.add("Semanal (7 días)");
		nombres.add("Mensual");
		int opcion = consola.leerOpcion("Periodo", nombres, "Cancelar");
		if (opcion == 0) {
			return null;
		}
		return Periodo.values()[opcion - 1];
	}

	private Entrenador elegirEntrenador() {
		List<Entrenador> entrenadores = new ArrayList<Entrenador>();
		List<String> nombres = new ArrayList<String>();
		for (Usuario candidato : club.getUsuarios()) {
			if (candidato instanceof Entrenador) {
				entrenadores.add((Entrenador) candidato);
				nombres.add(candidato.getNombreCompleto());
			}
		}
		int opcion = consola.leerOpcion("Entrenador", nombres, "Sin filtro");
		if (opcion == 0) {
			return null;
		}
		return entrenadores.get(opcion - 1);
	}

	// Categoria no tiene toString, por eso se arma la lista de nombres
	private Categoria elegirCategoria() {
		List<String> nombres = new ArrayList<String>();
		for (Categoria categoria : club.getCategorias()) {
			nombres.add(categoria.getNombre());
		}
		int opcion = consola.leerOpcion("Categoría", nombres, "Sin filtro");
		if (opcion == 0) {
			return null;
		}
		return club.getCategorias().get(opcion - 1);
	}
}