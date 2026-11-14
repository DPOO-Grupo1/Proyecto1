package logica;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.Nivel;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.personal.Turno;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;

/**
 * Club pequeño para las pruebas de torneos. Hoy es 1 de noviembre de 2026 y los torneos
 * se juegan el sábado 21.
 * Tenis intermedio: s1 (ELO 1200), s2 (1100) y s3 (1000). s4 es avanzado y s5 no está en la academia.
 * e1 entrena a Águilas y Halcones y trabaja los sábados; e2 entrena a Lobos; f1 no tiene turnos.
 * Águilas y Lobos tienen 7 jugadores (el mínimo del fútbol); Halcones solo 5.
 */
public class ClubDePruebaTorneos {

	public static final LocalDate HOY = LocalDate.of(2026, 11, 1);
	public static final LocalDate SABADO = LocalDate.of(2026, 11, 21);

	public Club club;
	public Administrador admin;
	public Socio s1;
	public Socio s2;
	public Socio s3;
	public Socio s4;
	public Socio s5;
	public Entrenador e1;
	public Entrenador e2;
	public Fisioterapeuta f1;
	public ParticipanteExterno x1;
	public Disciplina tenis;
	public DeporteEquipo futbol;
	public Categoria mayores;
	public Equipo aguilas;
	public Equipo halcones;
	public Equipo lobos;

	public ClubDePruebaTorneos() {
		club = new Club();
		club.setFechaActual(HOY);

		admin = new Administrador("admin", "123");
		club.agregarUsuario(admin);
		s1 = crearSocio("s1");
		s2 = crearSocio("s2");
		s3 = crearSocio("s3");
		s4 = crearSocio("s4");
		s5 = crearSocio("s5");
		e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
		e2 = new Entrenador("e2", "pass", "Pedro", "Díaz", 2);
		f1 = new Fisioterapeuta("f1", "pass", "Laura", "Mesa", 3);
		club.agregarUsuario(e1);
		club.agregarUsuario(e2);
		club.agregarUsuario(f1);
		x1 = new ParticipanteExterno("x1", "pass", "Valentina", "Rojas", "1001", true);
		club.agregarUsuario(x1);
		club.getTurnos().add(new Turno(1, e1, DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));

		tenis = new Disciplina("Tenis", 2);
		club.agregarDisciplina(tenis);
		inscribirEnAcademia(s1, Nivel.INTERMEDIO, 1200);
		inscribirEnAcademia(s2, Nivel.INTERMEDIO, 1100);
		inscribirEnAcademia(s3, Nivel.INTERMEDIO, 1000);
		inscribirEnAcademia(s4, Nivel.AVANZADO, 1300);

		futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		mayores = new Categoria("Mayores", 18, Categoria.SIN_EDAD_MAXIMA);
		club.agregarDeporte(futbol);
		club.agregarCategoria(mayores);
		aguilas = crearEquipo("Águilas FC", "a", 7, e1);
		halcones = crearEquipo("Halcones FC", "h", 5, e1);
		lobos = crearEquipo("Lobos FC", "l", 7, e2);
	}

	private Socio crearSocio(String login) {
		Socio socio = new Socio(login, "pass", "Nombre " + login, "Apellido", login.toUpperCase(),
				LocalDate.of(1995, 5, 5));
		club.agregarUsuario(socio);
		return socio;
	}

	private void inscribirEnAcademia(Socio socio, Nivel nivel, int elo) {
		Inscripcion inscripcion = new Inscripcion(socio, tenis, nivel, elo);
		tenis.agregarInscripcion(inscripcion);
		socio.agregarInscripcion(inscripcion);
	}

	private Equipo crearEquipo(String nombre, String prefijo, int jugadores, Entrenador entrenador) {
		Equipo equipo = new Equipo(nombre, futbol, mayores, 15);
		equipo.setEntrenador(entrenador);
		for (int i = 1; i <= jugadores; i++) {
			Socio socio = crearSocio(prefijo + i);
			JugadorEquipo jugador = new JugadorEquipo(socio, equipo, i, "volante");
			equipo.agregarJugador(jugador);
			socio.agregarParticipacion(jugador);
		}
		club.agregarEquipo(equipo);
		return equipo;
	}
}
