package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.NoEncontradoException;
import excepciones.VentaInvalidaException;
import logica.Club;
import logica.GestorTienda;
import modelo.academias.CanchaPadel;
import modelo.academias.Disciplina;
import modelo.academias.MesaTenisMesa;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.tienda.Articulo;
import modelo.tienda.Bebida;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.Existencia;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;

/**
 * Programa de Prueba PP3: Ventas, fidelidad y restricciones.
 * Evalúa: RF3, RF4, RF5, RF17 y RF20.
 */
public class ConsolaPP3 {

	public static void main(String[] args) {
		System.out.println("=== PP3: Ventas, fidelidad y restricciones ===");

		try {
			Club club = new Club();
			LocalDate hoy = LocalDate.now();
			club.setFechaActual(hoy);
			GestorTienda gestor = new GestorTienda(club);

			// 1. Ubicaciones (Sección 4.1: U1 = Bodega, U2 = Tienda, Cafetería)
			Ubicacion bodega = new Ubicacion("Bodega");
			Ubicacion tienda = new Ubicacion("Tienda");
			Ubicacion cafeteria = new Ubicacion("Cafetería");
			club.agregarUbicacion(bodega);
			club.agregarUbicacion(tienda);
			club.agregarUbicacion(cafeteria);

			// 2. Usuarios
			Administrador admin = new Administrador("admin", "123");
			club.agregarUsuario(admin);

			Entrenador e1 = new Entrenador("e1", "pass", "Marta", "López", 1);
			Entrenador e2 = new Entrenador("e2", "pass", "Carlos", "Ramos", 2);
			Fisioterapeuta f1 = new Fisioterapeuta("f1", "pass", "Felipe", "Gómez", 4);
			club.agregarUsuario(e1);
			club.agregarUsuario(e2);
			club.agregarUsuario(f1);

			// Códigos de descuento de empleados (CD1 de E1 activo, CD2 de E2 inactivo)
			CodigoDescuento cd1 = new CodigoDescuento(1, e1);
			CodigoDescuento cd2 = new CodigoDescuento(2, e2);
			cd2.desactivar();
			CodigoDescuento cd4 = new CodigoDescuento(4, f1);
			club.agregarCodigo(cd1);
			club.agregarCodigo(cd2);
			club.agregarCodigo(cd4);

			// Socios (S1 con 9000 puntos para prueba RF5, S4, S6)
			Socio s1 = new Socio("s1", "pass", "Ana", "Rincón", "S1", LocalDate.of(1995, 3, 10));
			s1.sumarPuntos(9000);
			Socio s4 = new Socio("s4", "pass", "Diego", "Peña", "S4", LocalDate.of(1991, 10, 11));
			Socio s6 = new Socio("s6", "pass", "Fabio", "Ortiz", "S6", LocalDate.of(1997, 6, 12));
			club.agregarUsuario(s1);
			club.agregarUsuario(s4);
			club.agregarUsuario(s6);

			// 3. Productos y Existencias (Sección 4.1)
			// AR1: raqueta de tenis ($250.000)
			// AR2: balón de fútbol ($80.000)
			Articulo ar1 = new Articulo("AR1", 250000, "Tenis");
			Articulo ar2 = new Articulo("AR2", 80000, "Fútbol");
			club.agregarProducto(ar1);
			club.agregarProducto(ar2);
			club.agregarExistencia(new Existencia(ar1, tienda, 5));
			club.agregarExistencia(new Existencia(ar1, bodega, 10));
			club.agregarExistencia(new Existencia(ar2, tienda, 20));

			// Cafetería: SN1 (barrita frutos secos $6.000 con maní y almendras), BC1 (capuchino caliente $8.000), BF1 (limonada fría $7.000)
			List<String> alergenosSN1 = Arrays.asList("maní", "almendras");
			Snack sn1 = new Snack(alergenosSN1, "SN1", 6000);
			Bebida bc1 = new Bebida("CALIENTE", 250, "BC1", 8000); // caliente
			Bebida bf1 = new Bebida("FRIA", 300, "BF1", 7000); // fría
			club.agregarProducto(sn1);
			club.agregarProducto(bc1);
			club.agregarProducto(bf1);
			club.agregarExistencia(new Existencia(sn1, cafeteria, 20));
			club.agregarExistencia(new Existencia(bc1, cafeteria, 20));
			club.agregarExistencia(new Existencia(bf1, cafeteria, 20));

			// 4. Instalaciones para reglas cruzadas de bebidas calientes
			Disciplina padel = new Disciplina("Pádel", 2);
			Disciplina tenisMesa = new Disciplina("Tenis de mesa", 3);
			CanchaPadel i3 = new CanchaPadel("I3", "Cancha Pádel 1", padel); // restringe bebida caliente
			MesaTenisMesa i6 = new MesaTenisMesa("I6", "Mesa Aire Libre", tenisMesa, false); // no restringe (aire libre)
			club.agregarInstalacion(i3);
			club.agregarInstalacion(i6);

			// Reserva R2 de S4 en I3 (pádel) activa justo en este momento
			LocalTime ahora = LocalTime.now();
			Reserva r2 = new Reserva("R2", s4, i3, hoy, ahora.minusMinutes(15), ahora.plusMinutes(45), 4, Modalidad.DOBLES);
			club.agregarReserva(r2);

			// Reserva de S6 en I6 (mesa al aire libre) activa
			Reserva r6 = new Reserva("R6", s6, i6, hoy, ahora.minusMinutes(15), ahora.plusMinutes(45), 2, Modalidad.SENCILLOS);
			club.agregarReserva(r6);

			System.out.println("Datos iniciales de tienda y cafetería cargados.");

			// EP3.1 y EP3.2: Comprar en la tienda (RF3)
			System.out.println("\n--- ESCENARIO EP3: Comprar en la tienda (RF3) ---");
			// (a) S1 compra 2 AR2 ($160.000) y 1 AR1 ($250.000) -> subtotal $410.000 + IVA 19% ($77.900) = $487.900
			Map<String, Integer> compra1 = new HashMap<String, Integer>();
			compra1.put("AR2", 2);
			compra1.put("AR1", 1);
			VentaTienda v1 = gestor.comprarEnTienda(s1, compra1, null, 0);
			System.out.println("EP3.1 (a) APROBADO: Subtotal: $" + v1.getSubtotal() + ", IVA: $" + v1.calcularImpuesto() + ", Total: $" + v1.getMontoTotal());

			// (b) Consulta de puntos generados (acumula el 2% en puntos)
			System.out.println("EP3.1 (b) Puntos acumulados en esta compra (2%): " + v1.calcularPuntosGenerados() + " pts.");

			// (c) Compra con código CD1 de E1 (8% de descuento por código de otro empleado)
			Map<String, Integer> compraConDesc = new HashMap<String, Integer>();
			compraConDesc.put("AR2", 1);
			VentaTienda vDesc = gestor.comprarEnTienda(s1, compraConDesc, cd1.getIdDescuento(), 0);
			System.out.println("EP3.1 (c) APROBADO con código CD1 (8% desc): Descuento: $" + vDesc.getDescuento() + ", Total pagado: $" + vDesc.getMontoTotal());

			// Rechazos EP3.2:
			// (a) Código inexistente y luego inactivo CD2
			try {
				gestor.comprarEnTienda(s1, compraConDesc, 999, 0);
				System.err.println("ERROR: debió rechazar código inexistente");
			} catch (NoEncontradoException e) {
				System.out.println("EP3.2 (a) RECHAZADO: Código inexistente: " + e.getMessage());
			}
			try {
				gestor.comprarEnTienda(s1, compraConDesc, cd2.getIdDescuento(), 0);
				System.err.println("ERROR: debió rechazar código inactivo");
			} catch (VentaInvalidaException e) {
				System.out.println("EP3.2 (a) RECHAZADO: Código inactivo: " + e.getMessage());
			}

			// (b) Comprar 30 unidades de AR2 cuando hay existencias menores
			Map<String, Integer> compraExceso = new HashMap<String, Integer>();
			compraExceso.put("AR2", 30);
			try {
				gestor.comprarEnTienda(s1, compraExceso, null, 0);
				System.err.println("ERROR: debió rechazar compra que supera existencias");
			} catch (VentaInvalidaException e) {
				System.out.println("EP3.2 (b) RECHAZADO: Existencias insuficientes: " + e.getMessage());
			}

			// EP4.1 y EP4.2: Consumir en la cafetería (RF4)
			System.out.println("\n--- ESCENARIO EP4: Consumir en la cafetería (RF4) ---");
			// (a) S1 compra BF1 ($7.000) y SN1 ($6.000) con propina sugerida (10%)
			Map<String, Integer> pedidoCafeteria = new HashMap<String, Integer>();
			pedidoCafeteria.put("BF1", 1);
			pedidoCafeteria.put("SN1", 1);
			List<String> alergenosInformados = gestor.consultarAlergenos(s1, pedidoCafeteria);
			System.out.println("Alérgenos informados antes de confirmar: " + alergenosInformados);

			VentaCafeteria vCaf1 = gestor.comprarEnCafeteria(s1, pedidoCafeteria, null, 0, 10);
			System.out.println("EP4.1 (a) APROBADO: Subtotal: $" + vCaf1.getSubtotal() + ", Impoconsumo (8%): $" + vCaf1.calcularImpuesto()
					+ ", Propina (10%): $" + vCaf1.calcularPropina() + ", Total: $" + vCaf1.getMontoTotal());

			// (b) S6, con reserva en I6 (al aire libre), compra BC1 (caliente). Permitido en mesas sin techo
			Map<String, Integer> pedidoCaliente = new HashMap<String, Integer>();
			pedidoCaliente.put("BC1", 1);
			VentaCafeteria vAireLibre = gestor.comprarEnCafeteria(s6, pedidoCaliente, null, 0, 10);
			System.out.println("EP4.1 (b) APROBADO: Bebida caliente permitida en instalación al aire libre: Total: $" + vAireLibre.getMontoTotal());

			// EP4.2 RECHAZADO: S4, con R2 (pádel) vigente a esa hora, intenta comprar BC1 (caliente)
			try {
				gestor.comprarEnCafeteria(s4, pedidoCaliente, null, 0, 10);
				System.err.println("ERROR: debió rechazar bebida caliente en pádel");
			} catch (VentaInvalidaException e) {
				System.out.println("EP4.2 RECHAZADO: Bebida caliente en pádel prohibida: " + e.getMessage());
			}

			// EP5.1: Redimir puntos de fidelidad (RF5)
			System.out.println("\n--- ESCENARIO EP5: Redimir puntos de fidelidad (RF5) ---");
			int puntosAntes = s1.getPuntosFidelidad();
			System.out.println("Puntos de S1 antes de redimir: " + puntosAntes);
			// (a) Redime 5000 puntos en compra
			Map<String, Integer> compraPuntos = new HashMap<String, Integer>();
			compraPuntos.put("AR2", 1); // $80.000 + IVA $15.200 = $95.200
			VentaTienda vPuntos = gestor.comprarEnTienda(s1, compraPuntos, null, 5000);
			System.out.println("EP5.1 (a) APROBADO: Puntos redimidos: $" + vPuntos.getPuntosRedimidos() + ", Total con descuento de puntos: $" + vPuntos.getMontoTotal());
			System.out.println("Nuevo saldo de puntos de S1: " + s1.getPuntosFidelidad());

			// (b) Intenta redimir 20.000 puntos (saldo insuficiente)
			try {
				gestor.comprarEnTienda(s1, compraPuntos, null, 20000);
				System.err.println("ERROR: debió rechazar por saldo insuficiente de puntos");
			} catch (VentaInvalidaException e) {
				System.out.println("EP5.1 (b) RECHAZADO: Saldo insuficiente de puntos: " + e.getMessage());
			}

			// EP17.1 y EP17.2: Compra con código de empleado (RF17)
			System.out.println("\n--- ESCENARIO EP17: Compra con código de empleado (RF17) ---");
			Map<String, Integer> itemEmpleado = new HashMap<String, Integer>();
			itemEmpleado.put("AR2", 1);
			// (a) E1 compra con su propio código CD1 (15% de descuento)
			VentaTienda vPropio = gestor.comprarEnTienda(e1, itemEmpleado, cd1.getIdDescuento(), 0);
			System.out.println("EP17.1 (a) APROBADO: E1 con código propio recibe 15% desc: Descuento: $" + vPropio.getDescuento());

			// (b) E2 compra con el código CD1 de E1 (8% de descuento por ser ajeno)
			VentaTienda vAjeno = gestor.comprarEnTienda(e2, itemEmpleado, cd1.getIdDescuento(), 0);
			System.out.println("EP17.1 (b) APROBADO: E2 con código ajeno recibe 8% desc: Descuento: $" + vAjeno.getDescuento());

			// EP17.2 (b): Entrenador con sesión en pádel intenta comprar BC1
			SesionEntrenamiento sesionE1 = new SesionEntrenamiento("SE_PADEL", e1, i3, hoy, ahora.minusMinutes(10), 60, padel);
			club.agregarSesion(sesionE1);
			try {
				gestor.comprarEnCafeteria(e1, pedidoCaliente, null, 0, 10);
				System.err.println("ERROR: debió rechazar bebida caliente a entrenador con sesión en pádel");
			} catch (VentaInvalidaException e) {
				System.out.println("EP17.2 (b) RECHAZADO: Restricción cubre sesiones del entrenador: " + e.getMessage());
			}

			// EP20.1: Descuento de empleado en cafetería (RF20)
			System.out.println("\n--- ESCENARIO EP20: Descuento de empleado en cafetería (RF20) ---");
			Map<String, Integer> pedidoFisio = new HashMap<String, Integer>();
			pedidoFisio.put("BF1", 1);
			// (a) F1 compra en cafetería con su código CD4 (15% de descuento)
			VentaCafeteria vFisio = gestor.comprarEnCafeteria(f1, pedidoFisio, cd4.getIdDescuento(), 0, 0);
			System.out.println("EP20.1 (a) APROBADO: F1 recibe 15% de descuento en cafetería: Descuento: $" + vFisio.getDescuento());

			// (b) Código inválido / inexistente
			try {
				gestor.comprarEnCafeteria(f1, pedidoFisio, 9999, 0, 0);
				System.err.println("ERROR: debió rechazar código inexistente");
			} catch (NoEncontradoException e) {
				System.out.println("EP20.1 (b) RECHAZADO: Código inválido: " + e.getMessage());
			}

			System.out.println("\nFin de PP3.");

		} catch (Exception e) {
			System.err.println("Error inesperado:");
			e.printStackTrace();
		}
	}
}
