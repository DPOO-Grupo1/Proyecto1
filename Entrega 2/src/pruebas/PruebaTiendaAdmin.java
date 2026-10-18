package pruebas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.Producto;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Socio;

public class PruebaTiendaAdmin {

    public static void main(String[] args) {

        // PREPARAR
        Club club = new Club();
        GestorTienda gestor = new GestorTienda(club);

        Administrador admin = new Administrador("admin", "1234");   // ajusta si el constructor es otro
        Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        club.agregarUsuario(admin);
        club.agregarUsuario(socio);

        club.agregarUbicacion(new Ubicacion("Tienda"));
        club.agregarUbicacion(new Ubicacion("Bodega"));
        club.agregarUbicacion(new Ubicacion("Cafetería"));

        // 1. Admin registra un producto
        System.out.println("=== 1. Admin registra un balón (10 unidades en Tienda) ===");
        try {
            gestor.registrarProducto(admin, new Articulo("Balón", 50000, "Balones"), "Tienda", 10);
            System.out.println("ACEPTADO");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 2. Producto repetido
        System.out.println("=== 2. Admin registra otro 'Balón' (repetido) ===");
        try {
            gestor.registrarProducto(admin, new Articulo("Balón", 50000, "Balones"), "Tienda", 10);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 3. Reabastecer
        System.out.println("=== 3. Admin reabastece 5 balones ===");
        try {
            gestor.reabastecer(admin, "Balón", "Tienda", 5);
            System.out.println("ACEPTADO");
            System.out.println(gestor.consultarInventario(admin, "Tienda").size() + " producto(s) en Tienda");
            Map<Producto, Integer> inventario = gestor.consultarInventario(admin, "Tienda");
            for (Producto producto : inventario.keySet()) {
                System.out.println("  " + producto.getNombre() + ": " + inventario.get(producto));
            }
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 4. Mover a la bodega
        System.out.println("=== 4. Admin mueve 6 balones de Tienda a Bodega ===");
        try {
            gestor.moverProductos(admin, "Balón", "Tienda", "Bodega", 6);
            System.out.println("ACEPTADO");
            Map<Producto, Integer> enTienda = gestor.consultarInventario(admin, "Tienda");
            for (Producto producto : enTienda.keySet()) {
                System.out.println("  Tienda: " + producto.getNombre() + " = " + enTienda.get(producto));
            }
            Map<Producto, Integer> enBodega = gestor.consultarInventario(admin, "Bodega");
            for (Producto producto : enBodega.keySet()) {
                System.out.println("  Bodega: " + producto.getNombre() + " = " + enBodega.get(producto));
            }
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 5. Mover más de lo que hay
        System.out.println("=== 5. Admin intenta mover 999 balones ===");
        try {
            gestor.moverProductos(admin, "Balón", "Tienda", "Bodega", 999);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 6. Administrador intenta comprar
        System.out.println("=== 6. El administrador intenta comprar ===");
        Map<String, Integer> items = new HashMap<String, Integer>();
        items.put("Balón", 1);
        try {
            gestor.comprarEnTienda(admin, items, null, 0);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 7. Cafetería: alérgenos y compra
        System.out.println("=== 7. Socio consulta alérgenos y compra un snack con propina ===");
        try {
            java.util.List<String> alergenos = new java.util.ArrayList<String>();
            alergenos.add("maní");
            Snack snack = new Snack(alergenos, "Barra de maní", 4000);
            gestor.registrarProducto(admin, snack, "Cafetería", 10);

            Map<String, Integer> pedido = new HashMap<String, Integer>();
            pedido.put("Barra de maní", 2);
            System.out.println("Alérgenos: " + gestor.consultarAlergenos(socio, pedido));

            gestor.comprarEnCafeteria(socio, pedido, null, 0, 10);
            System.out.println("ACEPTADO");
            System.out.println("Total pagado: " + socio.getCompras().get(0).getMontoTotal());
            System.out.println("Puntos ganados: " + socio.getPuntosFidelidad());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }
    }
}