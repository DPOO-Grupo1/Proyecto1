package modelo.personal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import modelo.usuarios.Empleado;

public class Turno {
    private int id;
    private Empleado empleado;
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<SolicitudCambioTurno> solicitudes;

    public Turno(int id, Empleado empleado, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.empleado = empleado;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.solicitudes = new ArrayList<SolicitudCambioTurno>();
    }

    public int getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public DayOfWeek getDia() { return dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public List<SolicitudCambioTurno> getSolicitudes() { return solicitudes; }

    public void agregarSolicitud(SolicitudCambioTurno solicitud) {
        solicitudes.add(solicitud);
    }

    public boolean tieneSolicitudPendiente() {
        for (SolicitudCambioTurno s : solicitudes) {
            if (s.estaPendiente()) {
                return true;
            }
        }
        return false;
    }

    // Dos turnos se solapan si son el mismo día y los horarios se cruzan
    // (bloques contiguos, como 8-10 y 10-12, no se solapan)
    public boolean seSolapaCon(Turno otro) {
        return dia == otro.dia
                && horaInicio.isBefore(otro.horaFin)
                && horaFin.isAfter(otro.horaInicio);
    }

    // true si el turno cubre esa hora en ese día
    public boolean cubre(DayOfWeek d, LocalTime hora) {
        return dia == d && !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    public void cambiar(DayOfWeek nuevoDia, LocalTime nuevoInicio, LocalTime nuevoFin) {
        this.dia = nuevoDia;
        this.horaInicio = nuevoInicio;
        this.horaFin = nuevoFin;
    }
}