package Pruebas;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import excepciones.ClubException;
import logica.Club;
import logica.GestorPersonal;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;

public class PruebaPersonal {

    public static void main(String[] args) {

        // ===== PREPARAR =====
        Club club = new Club();
        GestorPersonal gestor = new GestorPersonal(club);

        Administrador admin = new Administrador("admin", "1234");   // ajusta si el constructor es otro
        Entrenador e1 = new Entrenador("carlos", "1234", "Carlos", "Gómez", 1);
        Entrenador e2 = new Entrenador("laura", "1234", "Laura", "Ruiz", 2);
        Fisioterapeuta f1 = new Fisioterapeuta("juan", "1234", "Juan", "Pardo", 3);
        club.agregarUsuario(admin);
        club.agregarUsuario(e1);
        club.agregarUsuario(e2);
        club.agregarUsuario(f1);

        // Horario reducido para que la prueba sea corta: solo lunes, 6:00 a 20:00
        // (el horario real de Club es lunes a sábado, así que publicamos turnos para todos los días)
        List<Turno> turnos = new ArrayList<Turno>();
        int id = 1;
        for (DayOfWeek dia : club.getHorario().getDias()) {
            turnos.add(new Turno(id++, e1, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
            turnos.add(new Turno(id++, e2, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
            turnos.add(new Turno(id++, f1, dia, LocalTime.of(6, 0), LocalTime.of(20, 0)));
        }

        // ===== 1. Publicar turnos con cobertura completa =====
        System.out.println("=== 1. Admin publica turnos (2 entrenadores y 1 fisio todo el día) ===");
        try {
            gestor.publicarTurnos(admin, turnos);
            System.out.println("ACEPTADO. Turnos publicados: " + club.getTurnos().size());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 2. Publicar turnos sin cobertura =====
        System.out.println("=== 2. Admin publica turnos sin fisioterapeuta ===");
        List<Turno> incompletos = new ArrayList<Turno>();
        incompletos.add(new Turno(100, e1, DayOfWeek.MONDAY, LocalTime.of(6, 0), LocalTime.of(20, 0)));
        incompletos.add(new Turno(101, e2, DayOfWeek.MONDAY, LocalTime.of(6, 0), LocalTime.of(20, 0)));
        try {
            gestor.publicarTurnos(admin, incompletos);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 3. Solicitar cambio de turno válido =====
        System.out.println("=== 3. Entrenador pide mover su turno del lunes al domingo ===");
        Turno turnoLunes = e1.getTurnos().get(0);
        try {
            gestor.solicitarCambio(e1, turnoLunes, DayOfWeek.SUNDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 4. Solicitar cambio dentro del horario =====
        System.out.println("=== 4. Entrenador pide mover su turno del lunes al martes 8:00-12:00 ===");
        SolicitudCambioTurno solicitud = null;
        try {
            solicitud = gestor.solicitarCambio(e1, turnoLunes, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
            System.out.println("ACEPTADO. Estado: " + solicitud.getEstado());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 5. Segunda solicitud pendiente =====
        System.out.println("=== 5. Entrenador pide otro cambio del mismo turno (ya hay uno pendiente) ===");
        try {
            gestor.solicitarCambio(e1, turnoLunes, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 6. Aprobar el cambio deja un bloque sin cobertura =====
        System.out.println("=== 6. Admin aprueba el cambio (deja el lunes con 1 solo entrenador) ===");
        try {
            gestor.resolverSolicitud(admin, solicitud, true);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 7. Sugerir producto =====
        System.out.println("=== 7. Fisioterapeuta sugiere un producto ===");
        Sugerencia sugerencia = null;
        try {
            sugerencia = gestor.sugerirProducto(f1, "Vendas", "Artículo", "Para lesiones leves");
            System.out.println("ACEPTADO. Estado: " + sugerencia.getEstado());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 8. Rechazar sin motivo =====
        System.out.println("=== 8. Admin rechaza la sugerencia sin motivo ===");
        try {
            gestor.resolverSugerencia(admin, sugerencia, false, "");
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 9. Aprobar sugerencia =====
        System.out.println("=== 9. Admin aprueba la sugerencia ===");
        try {
            gestor.resolverSugerencia(admin, sugerencia, true, null);
            System.out.println("ACEPTADO. Estado: " + sugerencia.getEstado());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // ===== 10. Resolver sugerencia ya resuelta =====
        System.out.println("=== 10. Admin vuelve a resolver la misma sugerencia ===");
        try {
            gestor.resolverSugerencia(admin, sugerencia, true, null);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }
    }
}