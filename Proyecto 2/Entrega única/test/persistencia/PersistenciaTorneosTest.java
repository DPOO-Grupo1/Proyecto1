package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import logica.ClubDePruebaTorneos;
import logica.GestorTorneos;
import modelo.academias.Modalidad;
import modelo.academias.Nivel;
import modelo.torneos.Competitivo;
import modelo.torneos.EliminacionDirecta;
import modelo.torneos.EstadoTorneo;
import modelo.torneos.Recreativo;
import modelo.torneos.TodosContraTodos;
import modelo.torneos.Torneo;

/**
 * Guarda torneos y bonos en un archivo temporal y los carga en otro club igual.
 * Lo que se calcula (posiciones, ganador) tiene que dar lo mismo después de cargar.
 */
public class PersistenciaTorneosTest {

	@Test
	public void losTorneosYLosBonosSobrevivenAGuardarYCargar() throws Exception {
		ClubDePruebaTorneos datos = new ClubDePruebaTorneos();
		GestorTorneos gestor = new GestorTorneos(datos.club);
		Torneo futbol = gestor.crearTorneoConjunto(datos.admin, "Copa", ClubDePruebaTorneos.SABADO, 4, datos.futbol,
				datos.mayores, new Recreativo(20), new TodosContraTodos());
		gestor.inscribirEquipo(datos.admin, futbol, datos.aguilas);
		gestor.inscribirEquipo(datos.admin, futbol, datos.lobos);
		gestor.iniciarTorneo(datos.admin, futbol);
		gestor.registrarResultado(datos.admin, futbol, futbol.getEncuentros().get(0), 2, 1);
		Torneo tenis = gestor.crearTorneoIndividual(datos.admin, "Tenis", ClubDePruebaTorneos.SABADO, 6, datos.tenis,
				Nivel.INTERMEDIO, Modalidad.SENCILLOS, new Competitivo(30000), new EliminacionDirecta());
		gestor.inscribirIndividual(datos.s1, tenis, null);
		gestor.inscribirIndividual(datos.x1, tenis, null);

		File archivo = File.createTempFile("torneos", ".json");
		archivo.deleteOnExit();
		PersistenciaTorneos persistencia = new PersistenciaTorneos();
		persistencia.guardar(archivo.getPath(), datos.club);

		ClubDePruebaTorneos otro = new ClubDePruebaTorneos();
		persistencia.cargar(archivo.getPath(), otro.club);

		Torneo futbolCargado = otro.club.buscarTorneo("Copa");
		assertEquals(EstadoTorneo.FINALIZADO, futbolCargado.getEstado());
		assertEquals("Águilas FC", futbolCargado.getGanador().getNombre());
		assertEquals(7, otro.club.getBonos().size());
		assertEquals(8, otro.club.siguienteIdBono());

		Torneo tenisCargado = otro.club.buscarTorneo("Tenis");
		assertTrue(tenisCargado.estaAbierto());
		assertTrue(tenisCargado.buscarInscripcionDe(otro.s1).isPreferencial());
		assertEquals(60000, tenisCargado.getRecaudo());
		assertEquals(tenis.getCuposRegularesLibres(), tenisCargado.getCuposRegularesLibres());
	}
}
