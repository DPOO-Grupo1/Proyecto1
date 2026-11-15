package logica;

import java.time.LocalDate;

import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.Disciplina;
import modelo.academias.EscenarioConjunto;
import modelo.academias.Inscripcion;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Nivel;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.deportes.JugadorEquipo;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;

/**
 * Club pequeño para las pruebas de academias y entrenamiento. Hoy es 1 de noviembre de 2026.
 */
public class ClubDePruebaAcademias {

	public static final LocalDate HOY = LocalDate.of(2026, 11, 1);

	public Club club;
	public Administrador admin;
	public Entrenador e1;
	public Entrenador e2;
	public Fisioterapeuta f1;
	public Socio s1;
	public Socio s2;
	public Socio s3;
	public Socio s4;
	public Socio s5;
	public Socio s6;
	public Socio sMoroso;
	public Disciplina tenis;
	public Disciplina padel;
	public Disciplina tenisMesa;
	public CanchaTenis canchaTenis1;
	public CanchaTenis canchaTenis2;
	public CanchaTenis canchaTenis3;
	public CanchaPadel canchaPadel1;
	public MesaTenisMesa mesaTechada;
	public EscenarioConjunto canchaFutbol;
	public Equipo aguilas;

	public ClubDePruebaAcademias() {
		club = new Club();
		club.setFechaActual(HOY);

		admin = new Administrador("admin", "123");
		e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
		e2 = new Entrenador("e2", "pass", "Carlos", "Ruiz", 2);
		f1 = new Fisioterapeuta("f1", "pass", "Laura", "Mesa", 3);
		club.agregarUsuario(admin);
		club.agregarUsuario(e1);
		club.agregarUsuario(e2);
		club.agregarUsuario(f1);

		s1 = crearSocio("s1", "S1");
		s2 = crearSocio("s2", "S2");
		s3 = crearSocio("s3", "S3");
		s4 = crearSocio("s4", "S4");
		s5 = crearSocio("s5", "S5");
		s6 = crearSocio("s6", "S6");
		sMoroso = crearSocio("smoroso", "S7");
		sMoroso.setAlDia(false);

		tenis = new Disciplina("Tenis", 2);
		padel = new Disciplina("Pádel", 2);
		tenisMesa = new Disciplina("Tenis de mesa", 3);
		club.agregarDisciplina(tenis);
		club.agregarDisciplina(padel);
		club.agregarDisciplina(tenisMesa);

		canchaTenis1 = new CanchaTenis("I1", "Cancha Tenis 1", tenis);
		canchaTenis2 = new CanchaTenis("I2", "Cancha Tenis 2", tenis);
		canchaTenis3 = new CanchaTenis("I3", "Cancha Tenis 3", tenis);
		canchaPadel1 = new CanchaPadel("I4", "Cancha Pádel 1", padel);
		mesaTechada = new MesaTenisMesa("I5", "Mesa Tenis 1", tenisMesa, true);
		canchaFutbol = new EscenarioConjunto("I6", "Cancha Fútbol", "Fútbol");
		club.agregarInstalacion(canchaTenis1);
		club.agregarInstalacion(canchaTenis2);
		club.agregarInstalacion(canchaTenis3);
		club.agregarInstalacion(canchaPadel1);
		club.agregarInstalacion(mesaTechada);
		club.agregarInstalacion(canchaFutbol);

		inscribir(s1, tenis, Nivel.PRINCIPIANTE, 1000);
		inscribir(s2, tenis, Nivel.INTERMEDIO, 1050);
		inscribir(s3, tenis, Nivel.AVANZADO, 1200);
		inscribir(s4, padel, Nivel.INTERMEDIO, 1000);
		inscribir(s5, padel, Nivel.INTERMEDIO, 1020);

		DeporteEquipo futbol = new DeporteEquipo("Fútbol", 7, 3, 1, 0, true, false);
		Categoria mayores = new Categoria("Mayores", 18, Categoria.SIN_EDAD_MAXIMA);
		club.agregarDeporte(futbol);
		club.agregarCategoria(mayores);
		aguilas = new Equipo("Águilas FC", futbol, mayores, 10);
		aguilas.setEntrenador(e1);
		JugadorEquipo j1 = new JugadorEquipo(s1, aguilas, 1, "campo");
		JugadorEquipo j2 = new JugadorEquipo(s2, aguilas, 2, "campo");
		aguilas.agregarJugador(j1);
		aguilas.agregarJugador(j2);
		s1.agregarParticipacion(j1);
		s2.agregarParticipacion(j2);
		club.agregarEquipo(aguilas);
	}

	private Socio crearSocio(String login, String socioID) {
		Socio socio = new Socio(login, "pass", "Nombre " + login, "Apellido", socioID, LocalDate.of(1995, 5, 5));
		club.agregarUsuario(socio);
		return socio;
	}

	public Inscripcion inscribir(Socio socio, Disciplina disciplina, Nivel nivel, int elo) {
		Inscripcion ins = new Inscripcion(socio, disciplina, nivel, elo);
		socio.agregarInscripcion(ins);
		disciplina.agregarInscripcion(ins);
		return ins;
	}
}

