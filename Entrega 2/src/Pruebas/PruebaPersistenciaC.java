package Pruebas;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorPersonal;
import logica.GestorReportes;
import logica.GestorTienda;
import modelo.tienda.Articulo;
import modelo.tienda.Existencia;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import persistencia.PersistenciaPersonal;
import persistencia.PersistenciaTienda;

public class PruebaPersistenciaC {

    public static void main(String[] args) throws Exception {
        String carpeta = "C:/datos_cds";
        new File(carpeta).mkdirs();

        // ===== PARTE 1: crear datos y guardarlos =====
        Club club = new Club();
        GestorTienda tienda = new GestorTienda(club);
        GestorReportes reportes = new GestorReportes(club);
        GestorPersonal personal = new GestorPersonal(club);

        Administrador admin = new Administrador("admin", "1234");
        Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        Entrenador entrenador = new Entrenador("carlos", "1234", "Carlos", "Gómez", 1);
        club.agregarUsuario(admin);
        club.agregarUsuario(socio);
        club.agregarUsuario(entrenador);

        club.agregarUbicacion(new Ubicacion("Tienda"));
        try {
            tienda.registrarProducto(admin, new Articulo("Balón", 50000, "Balones"), "Tienda", 10);
            Map<String, Integer> items = new HashMap<String, Integer>();
            items.put("Balón", 2);
            tienda.comprarEnTienda(socio, items, null, 0);
            reportes.registrarPago(admin, "S001", YearMonth.now(), 120000);
            personal.sugerirProducto(entrenador, "Vendas", "Artículo", "Para lesiones");
        } catch (ClubException e) {
            System.out.println("Error preparando datos: " + e.getMessage());
        }

        System.out.println("=== ANTES DE GUARDAR ===");
        imprimirEstado(club, socio);

        new PersistenciaTienda().guardar(carpeta + "/tienda.json", club);
        new PersistenciaPersonal().guardar(carpeta + "/personal.json", club);
        System.out.println("Guardado en " + carpeta);

        // ===== PARTE 2: club nuevo y vacío, cargar desde los archivos =====
        Club club2 = new Club();
        Socio socio2 = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        Entrenador entrenador2 = new Entrenador("carlos", "1234", "Carlos", "Gómez", 1);
        club2.agregarUsuario(new Administrador("admin", "1234"));
        club2.agregarUsuario(socio2);
        club2.agregarUsuario(entrenador2);

        new PersistenciaTienda().cargar(carpeta + "/tienda.json", club2);
        new PersistenciaPersonal().cargar(carpeta + "/personal.json", club2);

        System.out.println("=== DESPUÉS DE CARGAR EN UN CLUB NUEVO ===");
        imprimirEstado(club2, socio2);
    }

    private static void imprimirEstado(Club club, Socio socio) {
        System.out.println("Productos: " + club.getProductos().size());
        for (Existencia e : club.getExistencias()) {
            System.out.println("  " + e.getProducto().getNombre() + " en " + e.getUbicacion().getNombre()
                    + ": " + e.getCantidad());
        }
        System.out.println("Ventas: " + club.getVentas().size());
        System.out.println("Compras del socio: " + socio.getCompras().size());
        System.out.println("Mensualidades del socio: " + socio.getMensualidades().size());
        System.out.println("Sugerencias: " + club.getSugerencias().size());
    }
}
