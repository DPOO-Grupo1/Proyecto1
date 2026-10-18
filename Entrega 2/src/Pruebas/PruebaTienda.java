package Pruebas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import modelo.tienda.Articulo;
import modelo.tienda.Ubicacion;
import modelo.tienda.VentaTienda;
import modelo.usuarios.Socio;

public class PruebaTienda {

    public static void main(String[] args) {
        // 1. PREPARAR: datos creados a mano
    	Club club = new Club();
    	GestorTienda gestor = new GestorTienda(club);

    	Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
    	club.agregarUsuario(socio);               // si Club tiene ese método

    	Articulo balon = new Articulo("Balón", 50000, "Balones");
    	Ubicacion tienda = new Ubicacion("Tienda");
    	club.agregarProducto(balon);
    	club.agregarUbicacion(tienda);
    	// y la Existencia: balón en la tienda, con 10 unidades

    	Map<String, Integer> items = new HashMap<String, Integer>();
    	items.put("Balón", 2);                    // nombre del producto → cantidad

    	Map<String, Integer> itemsDeMas = new HashMap<String, Integer>();
    	itemsDeMas.put("Balón", 999);             // más de las 10 que hay
    	
        // (Club, socio, producto, existencias...)

        // 2. EJECUTAR + 3. MOSTRAR
        System.out.println("=== Compra normal ===");
        try {
            VentaTienda v = gestor.comprarEnTienda(socio, items, null, 0);
            System.out.println("ACEPTADO. Total: " + v.getMontoTotal());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        System.out.println("=== Compra sin inventario ===");
        try {
            gestor.comprarEnTienda(socio, itemsDeMas, null, 0);
            System.out.println("ACEPTADO (esto sería un error de la prueba)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());   // esto es lo esperado
        }
    }
}
