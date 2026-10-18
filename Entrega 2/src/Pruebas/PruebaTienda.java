package Pruebas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.Existencia;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Socio;

public class PruebaTienda {

    public static void main(String[] args) {

        // ===== PREPARAR =====
        Club club = new Club();
        GestorTienda gestor = new GestorTienda(club);

        Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        club.agregarUsuario(socio);

        Ubicacion tienda = new Ubicacion("Tienda");
        club.agregarUbicacion(tienda);

        Articulo balon = new Articulo("Balón", 50000, "Balones");
        club.agregarProducto(balon);
        Existencia existencia = new Existencia(balon, tienda, 10);
        club.agregarExistencia(existencia);

        // ===== PRUEBA 1: compra normal =====
        System.out.println("=== 1. Socio compra 2 balones ===");
        System.out.println("Inventario antes: " + existencia.getCantidad());
        Map<String, Integer> items = new HashMap<String, Integer>();
        items.put("Balón", 2);
        try {
            gestor.comprarEnTienda(socio, items, null, 0);
            System.out.println("ACEPTADO");
            System.out.println("Inventario después: " + existencia.getCantidad());
            System.out.println("Compras del socio: " + socio.getCompras().size());
            System.out.println("Total pagado: " + socio.getCompras().get(0).getMontoTotal());
            System.out.println("Puntos ganados: " + socio.getPuntosFidelidad());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== PRUEBA 2: pide más de lo que hay =====
        System.out.println("=== 2. Socio intenta comprar 999 balones ===");
        Map<String, Integer> demasiados = new HashMap<String, Integer>();
        demasiados.put("Balón", 999);
        try {
            gestor.comprarEnTienda(socio, demasiados, null, 0);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== PRUEBA 3: socio intenta registrar un producto =====
        System.out.println("=== 3. Socio intenta registrar un producto ===");
        try {
            gestor.registrarProducto(socio, new Articulo("Raqueta", 80000, "Raquetas"), "Tienda", 5);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }
    }
}
