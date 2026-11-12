package pruebas;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.ClubException;
import logica.Club;
import logica.GestorPersonal;
import logica.GestorTienda;
import modelo.academias.CanchaPadel;
import modelo.academias.Disciplina;
import modelo.academias.Modalidad;
import modelo.academias.Reserva;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Turno;
import modelo.tienda.Bebida;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.Ubicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;
import persistencia.PersistenciaPersonal;
import persistencia.PersistenciaTienda;

public class PruebasExtraAreaC {

    public static void main(String[] args) throws Exception {

        Club club = new Club();
        GestorTienda tienda = new GestorTienda(club);
        GestorPersonal personal = new GestorPersonal(club);

        Administrador admin = new Administrador("admin", "1234");
        Socio ana = new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14));
        Socio luis = new Socio("luis", "1234", "Luis", "Gómez", "S002", LocalDate.of(2004, 5, 2));
        Entrenador e1 = new Entrenador("carlos", "1234", "Carlos", "Gómez", 1);
        Entrenador e2 = new Entrenador("laura", "1234", "Laura", "Ruiz", 2);
        Entrenador e3 = new Entrenador("pedro", "1234", "Pedro", "Soto", 4);
        Fisioterapeuta f1 = new Fisioterapeuta("juan", "1234", "Juan", "Pardo", 3);
        club.agregarUsuario(admin);
        club.agregarUsuario(ana);
        club.agregarUsuario(luis);
        club.agregarUsuario(e1);
        club.agregarUsuario(e2);
        club.agregarUsuario(e3);
        club.agregarUsuario(f1);

        // PARTE 1: bebida caliente en pádel
        club.agregarUbicacion(new Ubicacion("Cafetería"));
        tienda.registrarProducto(admin, new Bebida("CALIENTE", 250, "Café", 3000), "Cafetería", 10);

        // Reserva de pádel para AHORA (±10 minutos), a nombre de Ana
        Disciplina padel = new Disciplina("Pádel", 2);
        club.agregarDisciplina(padel);
        CanchaPadel cancha = new CanchaPadel("C1", "Cancha 1", padel);
        club.agregarInstalacion(cancha);
        LocalTime ahora = LocalTime.now();
        Reserva reserva = new Reserva("R1", ana, cancha, club.getFechaActual(),
                ahora.minusMinutes(10), ahora.plusMinutes(10), 4, Modalidad.DOBLES);
        club.agregarReserva(reserva);

        Map<String, Integer> cafe = new HashMap<String, Integer>();
        cafe.put("Café", 1);

        System.out.println("=== 1. Ana (en cancha de pádel) pide un café caliente ===");
        try {
            tienda.comprarEnCafeteria(ana, cafe, null, 0, 10);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        System.out.println("=== 2. Luis (sin reserva) pide el mismo café ===");
        try {
            tienda.comprarEnCafeteria(luis, cafe, null, 0, 10);
            System.out.println("ACEPTADO");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // PARTE 2: cambio de turno rechazado por cobertura
        // e1 solo trabaja el lunes; e2 y f1 todos los días; e3 de martes a sábado.
        List<Turno> turnos = new ArrayList<Turno>();
        int id = 1;
        LocalTime ini = LocalTime.of(6, 0), fin = LocalTime.of(20, 0);
        for (DayOfWeek dia : club.getHorario().getDias()) {
            turnos.add(new Turno(id++, e2, dia, ini, fin));
            turnos.add(new Turno(id++, f1, dia, ini, fin));
            if (dia == DayOfWeek.MONDAY) {
                turnos.add(new Turno(id++, e1, dia, ini, fin));
            } else {
                turnos.add(new Turno(id++, e3, dia, ini, fin));
            }
        }

        System.out.println("=== 3. Admin publica turnos ===");
        try {
            personal.publicarTurnos(admin, turnos);
            System.out.println("ACEPTADO. Turnos: " + club.getTurnos().size());
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        System.out.println("=== 4. Carlos pide pasar su turno del lunes al martes; admin aprueba ===");
        try {
            Turno turnoLunes = e1.getTurnos().get(0);
            SolicitudCambioTurno s = personal.solicitarCambio(e1, turnoLunes, DayOfWeek.TUESDAY, ini, fin);
            personal.resolverSolicitud(admin, s, true);
            System.out.println("ACEPTADO (esto sería un error)");
        } catch (ClubException e) {
            System.out.println("RECHAZADO: " + e.getMessage());
        }

        // PARTE 3: ida y vuelta de códigos y turnos
        club.agregarCodigo(new CodigoDescuento(7, e1));
        String carpeta = "salida_pruebas";
        new File(carpeta).mkdirs();
        new PersistenciaTienda().guardar(carpeta + "/tienda.json", club);
        new PersistenciaPersonal().guardar(carpeta + "/personal.json", club);

        Club club2 = new Club();
        club2.agregarUsuario(new Administrador("admin", "1234"));
        club2.agregarUsuario(new Socio("ana16", "1234", "Ana", "Pérez", "S001", LocalDate.of(2005, 3, 14)));
        club2.agregarUsuario(new Socio("luis", "1234", "Luis", "Gómez", "S002", LocalDate.of(2004, 5, 2)));
        club2.agregarUsuario(new Entrenador("carlos", "1234", "Carlos", "Gómez", 1));
        club2.agregarUsuario(new Entrenador("laura", "1234", "Laura", "Ruiz", 2));
        club2.agregarUsuario(new Entrenador("pedro", "1234", "Pedro", "Soto", 4));
        club2.agregarUsuario(new Fisioterapeuta("juan", "1234", "Juan", "Pardo", 3));
        new PersistenciaTienda().cargar(carpeta + "/tienda.json", club2);
        new PersistenciaPersonal().cargar(carpeta + "/personal.json", club2);

        System.out.println("=== 5. Ida y vuelta (antes y después de cargar) ===");
        System.out.println("Códigos: " + club.getCodigos().size() + " -> " + club2.getCodigos().size());
        System.out.println("Turnos: " + club.getTurnos().size() + " -> " + club2.getTurnos().size());
        System.out.println("Ventas: " + club.getVentas().size() + " -> " + club2.getVentas().size());
    }
}