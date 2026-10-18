package Pruebas;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorReportes;
import logica.GestorTienda;
import modelo.reportes.FiltroHistorial;
import modelo.reportes.Historial;
import modelo.reportes.Periodo;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteFinanciero;
import modelo.tienda.Articulo;
import modelo.tienda.Existencia;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Socio;

public class PruebaReportes {

    public static void main(String[] args) {

        // ===== PREPARAR =====
        Club club = new Club();
        GestorReportes reportes = new GestorReportes(club);
        GestorTienda tienda = new GestorTienda(club);

        Administrador admin = new Administrador("admin", "1234");   // ajusta si el constructor es otro
        Socio socio = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        club.agregarUsuario(admin);
        club.agregarUsuario(socio);

        Ubicacion ubicacionTienda = new Ubicacion("Tienda");
        club.agregarUbicacion(ubicacionTienda);
        Articulo balon = new Articulo("Balón", 50000, "Balones");
        club.agregarProducto(balon);
        club.agregarExistencia(new Existencia(balon, ubicacionTienda, 10));

        LocalDate hoy = club.getFechaActual();
        YearMonth mes = YearMonth.from(hoy);

        // ===== 1. Registrar mensualidad =====
        System.out.println("=== 1. Admin registra la mensualidad del mes ===");
        try {
            reportes.registrarPago(admin, "S001", mes, 120000);
            System.out.println("ACEPTADO");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 2. Mensualidad repetida =====
        System.out.println("=== 2. Admin registra el mismo mes otra vez ===");
        try {
            reportes.registrarPago(admin, "S001", mes, 120000);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 3. Socio intenta registrar un pago =====
        System.out.println("=== 3. Socio intenta registrar un pago ===");
        try {
            reportes.registrarPago(socio, "S001", mes.plusMonths(1), 120000);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 4. Reporte financiero =====
        System.out.println("=== 4. Reporte financiero mensual (1 venta de tienda + mensualidad) ===");
        try {
            Map<String, Integer> items = new HashMap<String, Integer>();
            items.put("Balón", 2);
            tienda.comprarEnTienda(socio, items, null, 0);

            ReporteFinanciero rf = reportes.generarReporteFinanciero(admin, Periodo.MENSUAL, mes.atDay(1));
            System.out.println("Rango: " + rf.getDesde() + " a " + rf.getHasta());
            System.out.println("Tienda: subtotal " + rf.getSubtotalTienda() + ", impuesto " + rf.getImpuestoTienda()
                    + ", total " + rf.getTotalTienda());
            System.out.println("Cafetería total: " + rf.getTotalCafeteria());
            System.out.println("Mensualidades: " + rf.getTotalMensualidades());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 5. Reporte deportivo =====
        System.out.println("=== 5. Reporte deportivo mensual ===");
        try {
            ReporteDeportivo rd = reportes.generarReporteDeportivo(admin, Periodo.MENSUAL, mes.atDay(1));
            for (String linea : rd.getResultados()) {
                System.out.println("  " + linea);
            }
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 6. Historial global =====
        System.out.println("=== 6. Historial global ===");
        try {
            Historial h = reportes.consultarHistorial(admin, new FiltroHistorial(null, null, null));
            System.out.println("Partidos: " + h.getPartidos().size() + ", reservas: " + h.getReservas().size()
                    + ", sesiones: " + h.getSesiones().size() + ", prácticas: " + h.getPartidosPractica().size());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 7. Socio intenta ver reportes =====
        System.out.println("=== 7. Socio intenta generar un reporte financiero ===");
        try {
            reportes.generarReporteFinanciero(socio, Periodo.DIARIO, hoy);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }
    }
}