package logica;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import modelo.academias.CanchaPadel;
import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.personal.Turno;
import modelo.tienda.Articulo;
import modelo.tienda.Bebida;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.Existencia;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.torneos.BonoDescuento;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;

/**
 * Club pequeño para las pruebas de tienda, personal y reportes. Hoy es lunes 2 de noviembre de 2026.
 * Tienda: Camiseta ($45.000, 20 en Tienda y 10 en Bodega) y Balón ($80.000, 5 en Tienda).
 * Cafetería: Café caliente ($3.500), Limonada ($5.000), Mix de frutos secos ($6.000, maní y almendras)
 * y Galleta ($4.000, gluten). Códigos: 1 de e1, 2 de e2 y 4 de f1 activos; 3 de e1 inactivo.
 * s1 tiene 9.000 puntos y un bono del 20 %. s2 tiene una reserva de pádel que dura todo el día.
 * Turnos: e1, e2 y f1 trabajan de lunes a sábado de 6:00 a 20:00 (cobertura justa); e3 no tiene turnos.
 */
public class ClubDePruebaTienda {

	public static final LocalDate HOY = LocalDate.of(2026, 11, 2);

	public Club club;
	public Administrador admin;
	public Socio s1;
	public Socio s2;
	public Entrenador e1;
	public Entrenador e2;
	public Entrenador e3;
	public Fisioterapeuta f1;
	public BonoDescuento bonoS1;

	public ClubDePruebaTienda() {
		club = new Club();
		club.setFechaActual(HOY);

		admin = new Administrador("admin", "123");
		s1 = new Socio("s1", "pass", "Ana", "Pérez", "S1", LocalDate.of(1995, 5, 5));
		s2 = new Socio("s2", "pass", "Luis", "Gómez", "S2", LocalDate.of(1990, 1, 1));
		e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
		e2 = new Entrenador("e2", "pass", "Julián", "Prieto", 2);
		e3 = new Entrenador("e3", "pass", "Rosa", "Medina", 3);
		f1 = new Fisioterapeuta("f1", "pass", "Laura", "Mesa", 4);
		club.agregarUsuario(admin);
		club.agregarUsuario(s1);
		club.agregarUsuario(s2);
		club.agregarUsuario(e1);
		club.agregarUsuario(e2);
		club.agregarUsuario(e3);
		club.agregarUsuario(f1);
		s1.setPuntosFidelidad(9000);

		Ubicacion tienda = new Ubicacion("Tienda");
		Ubicacion cafeteria = new Ubicacion("Cafetería");
		Ubicacion bodega = new Ubicacion("Bodega");
		club.agregarUbicacion(tienda);
		club.agregarUbicacion(cafeteria);
		club.agregarUbicacion(bodega);

		agregar(new Articulo("Camiseta", 45000, "ropa"), tienda, 20);
		club.agregarExistencia(new Existencia(buscar("Camiseta"), bodega, 10));
		agregar(new Articulo("Balón", 80000, "implementos"), tienda, 5);
		agregar(new Bebida("CALIENTE", 250, "Café", 3500), cafeteria, 40);
		agregar(new Bebida("FRIA", 400, "Limonada", 5000), cafeteria, 30);
		agregar(new Snack(Arrays.asList("maní", "almendras"), "Mix de frutos secos", 6000), cafeteria, 15);
		agregar(new Snack(Arrays.asList("gluten"), "Galleta", 4000), cafeteria, 20);

		club.agregarCodigo(new CodigoDescuento(1, e1));
		club.agregarCodigo(new CodigoDescuento(2, e2));
		CodigoDescuento inactivo = new CodigoDescuento(3, e1);
		inactivo.desactivar();
		club.agregarCodigo(inactivo);
		club.agregarCodigo(new CodigoDescuento(4, f1));

		bonoS1 = new BonoDescuento(1, s1, 20, "Copa Recreativa");
		club.agregarBono(bonoS1);

		Disciplina padel = new Disciplina("Pádel", 2);
		club.agregarDisciplina(padel);
		CanchaPadel cancha = new CanchaPadel("P1", "Cancha de pádel 1", padel);
		club.agregarInstalacion(cancha);
		club.agregarReserva(new Reserva("R1", s2, cancha, HOY, LocalTime.of(0, 0), LocalTime.of(23, 59), 4,
				Modalidad.DOBLES));

		List<Turno> semana = semanaCompleta();
		club.setTurnos(semana);
		for (Turno turno : semana) {
			turno.getEmpleado().agregarTurno(turno);
		}
	}

	/**
	 * e1, e2 y f1 de lunes a sábado de 6:00 a 20:00: la cobertura mínima justa.
	 */
	public List<Turno> semanaCompleta() {
		List<Turno> turnos = new ArrayList<Turno>();
		int id = 1;
		for (DayOfWeek dia : club.getHorario().getDias()) {
			turnos.add(new Turno(id++, e1, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
			turnos.add(new Turno(id++, e2, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
			turnos.add(new Turno(id++, f1, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
		}
		return turnos;
	}

	/**
	 * El turno del empleado ese día. Las pruebas lo usan para no repetir el ciclo.
	 */
	public Turno turnoDe(modelo.usuarios.Empleado empleado, DayOfWeek dia) {
		for (Turno turno : empleado.getTurnos()) {
			if (turno.getDia() == dia) {
				return turno;
			}
		}
		return null;
	}

	public Producto buscar(String nombre) {
		for (Producto producto : club.getProductos()) {
			if (producto.getNombre().equals(nombre)) {
				return producto;
			}
		}
		return null;
	}

	/**
	 * Unidades de un producto en una ubicación.
	 */
	public int existencias(String producto, String ubicacion) {
		for (Existencia existencia : club.getExistencias()) {
			if (existencia.getProducto().getNombre().equals(producto)
					&& existencia.getUbicacion().getNombre().equals(ubicacion)) {
				return existencia.getCantidad();
			}
		}
		return 0;
	}

	private void agregar(Producto producto, Ubicacion ubicacion, int cantidad) {
		club.agregarProducto(producto);
		club.agregarExistencia(new Existencia(producto, ubicacion, cantidad));
	}
}
