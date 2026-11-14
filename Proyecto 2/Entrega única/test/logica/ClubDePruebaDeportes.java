package logica;

import java.time.LocalDate;

import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;

/**
 * Club pequeño para las pruebas de deportes y competencias. Hoy es 1 de noviembre de 2026.
 * Fútbol Mayores: Águilas (8 jugadores, cupo 10, entrena e1) y Lobos (7 jugadores, cupo 7).
 * Baloncesto Mayores: Titanes y Toros (5 jugadores cada uno). Fútbol Sub-17: Pumas, sin jugadores.
 * Los jugadores tienen login a1..a8, l1..l7, t1..t5 y o1..o5, y su número es el del login.
 * s1 es un socio libre de 31 años; s15 tenía 15 años el 1 de enero y s14 tenía 14.
 */
public class ClubDePruebaDeportes {

	public static final LocalDate HOY = LocalDate.of(2026, 11, 1);

	public Club club;
	public Administrador admin;
	public Entrenador e1;
	public Fisioterapeuta f1;
	public Socio s1;
	public Socio s14;
	public Socio s15;
	public DeporteEquipo futbol;
	public DeporteEquipo baloncesto;
	public Categoria mayores;
	public Categoria sub17;
	public Equipo aguilas;
	public Equipo lobos;
	public Equipo titanes;
	public Equipo toros;
	public Equipo pumas;

	public ClubDePruebaDeportes() {
		club = new Club();
		club.setFechaActual(HOY);

		admin = new Administrador("admin", "123");
		e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
		f1 = new Fisioterapeuta("f1", "pass", "Laura", "Mesa", 2);
		club.agregarUsuario(admin);
		club.agregarUsuario(e1);
		club.agregarUsuario(f1);
		s1 = crearSocio("s1", LocalDate.of(1995, 5, 5));
		s14 = crearSocio("s14", LocalDate.of(2011, 3, 1));
		s15 = crearSocio("s15", LocalDate.of(2010, 6, 1));

		futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		baloncesto = new DeporteEquipo("Baloncesto", 5, 2, 0, 0, false, false);
		mayores = new Categoria("Mayores", 18, Categoria.SIN_EDAD_MAXIMA);
		sub17 = new Categoria("Sub-17", 15, 16);
		club.agregarDeporte(futbol);
		club.agregarDeporte(baloncesto);
		club.agregarCategoria(mayores);
		club.agregarCategoria(sub17);

		aguilas = crearEquipo("Águilas FC", futbol, mayores, 10, "a", 8);
		aguilas.setEntrenador(e1);
		lobos = crearEquipo("Lobos FC", futbol, mayores, 7, "l", 7);
		titanes = crearEquipo("Titanes", baloncesto, mayores, 10, "t", 5);
		toros = crearEquipo("Toros", baloncesto, mayores, 10, "o", 5);
		pumas = crearEquipo("Pumas", futbol, sub17, 15, "p", 0);
	}

	private Socio crearSocio(String login, LocalDate nacimiento) {
		Socio socio = new Socio(login, "pass", "Nombre " + login, "Apellido", login.toUpperCase(), nacimiento);
		club.agregarUsuario(socio);
		return socio;
	}

	private Equipo crearEquipo(String nombre, DeporteEquipo deporte, Categoria categoria, int cupo, String prefijo,
			int jugadores) {
		Equipo equipo = new Equipo(nombre, deporte, categoria, cupo);
		for (int i = 1; i <= jugadores; i++) {
			Socio socio = crearSocio(prefijo + i, LocalDate.of(1995, 5, 5));
			JugadorEquipo jugador = new JugadorEquipo(socio, equipo, i, "campo");
			equipo.agregarJugador(jugador);
			socio.agregarParticipacion(jugador);
		}
		club.agregarEquipo(equipo);
		return equipo;
	}

	/**
	 * El jugador con ese número en el equipo. Las pruebas lo usan para no repetir el try.
	 */
	public JugadorEquipo jugador(Equipo equipo, int numero) {
		for (JugadorEquipo jugador : equipo.getJugadores()) {
			if (jugador.getNumeroCamiseta() == numero) {
				return jugador;
			}
		}
		return null;
	}
}
