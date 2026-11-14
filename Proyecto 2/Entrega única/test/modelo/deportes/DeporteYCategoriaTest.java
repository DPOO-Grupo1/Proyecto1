package modelo.deportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import modelo.partidos.EstadisticaBaloncesto;
import modelo.partidos.EstadisticaFutbol;
import modelo.usuarios.Socio;

/**
 * Pruebas unitarias de las reglas de DeporteEquipo y Categoria.
 */
public class DeporteYCategoriaTest {

	private DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
	private DeporteEquipo voleibol = new DeporteEquipo("Voleibol", 6, 2, 0, 0, false, true);

	@Test
	public void cadaDeporteDaSusPropiosPuntos() {
		assertEquals(3, futbol.puntosPara(2, 1));
		assertEquals(1, futbol.puntosPara(1, 1));
		assertEquals(0, futbol.puntosPara(0, 1));
		assertEquals(2, voleibol.puntosPara(3, 1));
		assertEquals(0, voleibol.puntosPara(1, 3));
	}

	@Test
	public void soloAdmiteEstadisticasDeSuDeporte() {
		Equipo equipo = new Equipo("Águilas", futbol, new Categoria("Mayores", 18, 0), 10);
		Socio socio = new Socio("j1", "pass", "Ana", "Gómez", "J1", LocalDate.of(1995, 1, 1));
		JugadorEquipo jugador = new JugadorEquipo(socio, equipo, 1, "volante");

		assertTrue(futbol.admiteEstadistica(new EstadisticaFutbol(jugador, 1, 0, 0, 0)));
		assertFalse(futbol.admiteEstadistica(new EstadisticaBaloncesto(jugador, 10, 2)));
	}

	@Test
	public void mayoresNoTieneEdadMaxima() {
		Categoria mayores = new Categoria("Mayores", 18, Categoria.SIN_EDAD_MAXIMA);

		assertFalse(mayores.tieneEdadMaxima());
		assertTrue(mayores.admiteEdad(60));
		assertFalse(mayores.admiteEdad(17));
	}

	@Test
	public void laEdadSeCuentaAlPrimeroDeEnero() {
		Categoria sub17 = new Categoria("Sub-17", 15, 16);
		// Cumple 17 en marzo de 2026: el 1 de enero todavía tenía 16
		Socio socio = new Socio("j2", "pass", "Luis", "Rojas", "J2", LocalDate.of(2009, 3, 10));

		assertTrue(sub17.admite(socio, LocalDate.of(2026, 11, 1)));
		assertFalse(sub17.admite(socio, LocalDate.of(2027, 2, 1)));
	}
}
