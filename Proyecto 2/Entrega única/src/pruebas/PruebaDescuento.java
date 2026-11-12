package pruebas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.Existencia;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

public class PruebaDescuento {

    public static void main(String[] args) {

        // PREPARAR
        Club club = new Club();
        GestorTienda gestor = new GestorTienda(club);

        Entrenador entrenador = new Entrenador("carlos", "1234", "Carlos", "Gómez", 1);   // ajusta si el constructor es otro
        Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        club.agregarUsuario(entrenador);
        club.agregarUsuario(socio);

        Ubicacion tienda = new Ubicacion("Tienda");
        club.agregarUbicacion(tienda);
        Articulo balon = new Articulo("Balón", 50000, "Balones");
        club.agregarProducto(balon);
        club.agregarExistencia(new Existencia(balon, tienda, 10));

        CodigoDescuento codigo = new CodigoDescuento(7, entrenador);
        club.agregarCodigo(codigo);

        Map<String, Integer> items = new HashMap<String, Integer>();
        items.put("Balón", 1);

        // 1. El empleado dueño usa su código (15 %)
        System.out.println("=== 1. Empleado compra 1 balón con su código ===");
        try {
            gestor.comprarEnTienda(entrenador, items, 7, 0);
            System.out.println("ACEPTADO. Total pagado: " + entrenador.getCompras().get(0).getMontoTotal());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 2. Un socio usa el código del empleado (8 %)
        System.out.println("=== 2. Socio compra 1 balón con el código del empleado ===");
        try {
            gestor.comprarEnTienda(socio, items, 7, 0);
            System.out.println("ACEPTADO. Total pagado: " + socio.getCompras().get(0).getMontoTotal());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 3. Código que no existe
        System.out.println("=== 3. Socio usa un código inexistente ===");
        try {
            gestor.comprarEnTienda(socio, items, 99, 0);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // 4. Redimir puntos que no tiene
        System.out.println("=== 4. Socio intenta redimir 999999 puntos ===");
        try {
            gestor.comprarEnTienda(socio, items, null, 999999);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }
    }
}